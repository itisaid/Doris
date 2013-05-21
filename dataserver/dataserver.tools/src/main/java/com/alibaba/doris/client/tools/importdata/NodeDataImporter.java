/**
 * Project: dataserver.tools-0.1.0-SNAPSHOT
 * 
 * File Created at 2011-8-4
 * $Id$
 * 
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.alibaba.doris.client.tools.importdata;

import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.doris.cli.CommandLineHandler;
import com.alibaba.doris.cli.Option;
import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.ConnectionFactory;
import com.alibaba.doris.client.tools.concurrent.ParralelExecutor;
import com.alibaba.doris.client.tools.concurrent.ParralelTask;
import com.alibaba.doris.client.tools.concurrent.ParralelTaskFactory;
import com.alibaba.doris.common.route.MockVirtualRouter;
import com.alibaba.doris.common.route.VirtualRouter;

/**
 * TODO Comment of NodeDataImporter
 * 
 * @author luyi.huangly
 */
public class NodeDataImporter extends CommandLineHandler {

    private String     ip;
    private int        port;
    private int        vn;
    private int        c;
    private String     f;
    private String     t;
    private String     p;
    private String     verbose;

    private Connection connection = null;

    @Override
    public void handleCommand() {

        if ("log".equals(t)) {
        } else if ("txt".equals(t)) {
            final Queue<KeyPair> keyQueue = new LinkedBlockingQueue<KeyPair>();
            final AtomicBoolean finish = new AtomicBoolean(false);
            try {
                final VirtualRouter virtualRouter = new MockVirtualRouter(vn);

                ConnectionFactory factory = ConnectionFactory.getInstance();
                InetSocketAddress remoteAddress = new InetSocketAddress(ip, port);
                final Connection connection0 = factory.getConnection(remoteAddress);
                connection0.open();

                connection = connection0;

                boolean needProfiling = "true".equals(p);
                final ReentrantLock lock = new ReentrantLock(); //锁
                //根据锁产生Condition对象
                final Condition produced = lock.newCondition();
                //读取数据线程池
                ParralelExecutor readExecutor = new ImportExecutorImpl(0, 0, 1, false,
                        FileReadTask.class);

                readExecutor.setParralelTaskFactory(new ParralelTaskFactory() {
                    @Override
                    public ParralelTask createTask(Class<? extends ParralelTask> parralelTaskClass,
                                                   int i, long start, long end,
                                                   CountDownLatch countDownLatch,
                                                   CountDownLatch resultCountDownLatch) {
                        FileReadTask fileReadTask = (FileReadTask) super.createTask(
                                parralelTaskClass, i, start, end, countDownLatch,
                                resultCountDownLatch);
                        fileReadTask.setFileName(f);
                        fileReadTask.setKeyQueue(keyQueue);
                        fileReadTask.setFinish(finish);
                        fileReadTask.setLock(lock);
                        fileReadTask.setProduced(produced);
                        return fileReadTask;
                    }
                });

                readExecutor.start();
                //导入数据线程池
                ParralelExecutor putExecutor = new ImportExecutorImpl(0, 0, c, needProfiling,
                        QueuePutTask.class);

                putExecutor.setParralelTaskFactory(new ParralelTaskFactory() {
                    @Override
                    public ParralelTask createTask(Class<? extends ParralelTask> parralelTaskClass,
                                                   int i, long start, long end,
                                                   CountDownLatch countDownLatch,
                                                   CountDownLatch resultCountDownLatch) {
                        QueuePutTask queuePutTask = (QueuePutTask) super.createTask(
                                parralelTaskClass, i, start, end, countDownLatch,
                                resultCountDownLatch);

                        queuePutTask.setConnection(connection0);
                        queuePutTask.setVirtualRouter(virtualRouter);
                        queuePutTask.setKeyQueue(keyQueue);
                        queuePutTask.setLock(lock);
                        queuePutTask.setFinish(finish);
                        queuePutTask.setProduced(produced);
                        return queuePutTask;
                    }
                });

                putExecutor.start();

                Object result;
                try {
                    result = readExecutor.getResult();
                } catch (Exception e) {
                }
                try {
                    result = putExecutor.getResult();
                } catch (Exception e) {
                }
                if ("true".endsWith(p)) {
                    putExecutor.getPermMeter().printReport();
                }
            } finally {
                if (connection != null)
                    connection.close();
            }

        }

    }

    public NodeDataImporter() {
        options.add(new Option("-ip", "IP", "DataServer IP"));
        options.add(new Option("-port", "Port", "DataServer Port"));
        options.add(new Option("-vn", "VirtualNumber", "Virtual Number to routing.", false, true));
        options.add(new Option("-c", "Concurrent", "number, Concurrent worker thread,default =1 ",
                false, true));
        options.add(new Option("-f", "file", "the import file path", true, true));
        options.add(new Option("-t", "type", "log/txt, the format of import file", false, true));
        options.add(new Option("-h", "Help", "Print command usage", false, false));
        options.add(new Option("-p", "Performance",
                "true/false, performance anlyze and print result. ", false, true));

    }

    @Override
    public void prepareParameters() {
        ip = commandLine.getValue("-ip");
        port = commandLine.getInt("-port");
        vn = commandLine.getInt("-vn");
        c = commandLine.getInt("-c");
        f = commandLine.getValue("-f");
        t = commandLine.getValue("-t");
        p = commandLine.getValue("-p");
        if ("true".equals(verbose)) {
            System.out.println(" ip: " + ip);
            System.out.println(" port: " + port);
            System.out.println(" v: " + vn);
            System.out.println(" c: " + c);
            System.out.println(" f: " + f);
            System.out.println(" t: " + t);
        }
    }

    public static void main(String[] args) {
        //Command e.g : clientDataMaker -config doris-client.properties -ns StringName -kp abc -vp vvv -s 0 -e 1000 -c 10 -p true

        NodeDataImporter nodeDataImporter = new NodeDataImporter();
        nodeDataImporter.handle(args);
        System.exit(0);
    }

}

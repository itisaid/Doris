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

import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.tools.concurrent.ParralelTaskImpl;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.KeyImpl;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.common.route.VirtualRouter;

/**
 * TODO Comment of QueuePutTask
 * 
 * @author luyi.huangly
 */
public class QueuePutTask extends ParralelTaskImpl {
    private Queue<KeyPair> keyQueue;
    private Connection     connection;
    private VirtualRouter  virtualRouter;
    ReentrantLock          lock     = new ReentrantLock(); //锁
    //根据锁产生Condition对象
    Condition              produced = lock.newCondition();
    private AtomicBoolean  finish;

    public AtomicBoolean getFinish() {
        return finish;
    }

    public void setFinish(AtomicBoolean finish) {
        this.finish = finish;
    }

    public Queue<KeyPair> getKeyQueue() {
        return keyQueue;
    }

    public void setKeyQueue(Queue<KeyPair> keyQueue) {
        this.keyQueue = keyQueue;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public VirtualRouter getVirtualRouter() {
        return virtualRouter;
    }

    public void setVirtualRouter(VirtualRouter virtualRouter) {
        this.virtualRouter = virtualRouter;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public void setLock(ReentrantLock lock) {
        this.lock = lock;
    }

    public Condition getProduced() {
        return produced;
    }

    public void setProduced(Condition produced) {
        this.produced = produced;
    }

    public void run() {
        startCountDownLatch.countDown();
        while (true) {
            long startTime = System.currentTimeMillis();
            if (needPofiling) {

                permMeter.startRecord();
            }
            lock.lock();
            KeyPair keyPair = null;
            try {
                if (keyQueue.isEmpty()) {
                    if (finish.get() == Boolean.TRUE) {//当队列中没有
                        resultCountDownLatch.countDown();
                        produced.signalAll();
                        return;
                    } else {
                        try {
                            produced.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (keyQueue.isEmpty()) {
                    continue;
                }
                keyPair = keyQueue.poll();
            } finally {
                lock.unlock();
            }
            doRun(keyPair);
            if (needPofiling) {
                permMeter.endRecord();
                long endTime = System.currentTimeMillis();
                long ellapseTime = endTime - startTime;
                permMeter.addItem(ellapseTime);
                //              permMeter.printReport();
            }
        }
    }

    public void doRun(KeyPair keyPair) {
        String key = keyPair.getKey();
        int vnode = virtualRouter.findVirtualNode(key);

        int commaIndex = key.indexOf(":");

        if (commaIndex == -1) {
            throw new IllegalArgumentException("Invalid kp parameter: " + key + ", Format 101:abc");
        }
        String ns = key.substring(0, commaIndex);
        String logicKey = key.substring(commaIndex + 1);
        int namespaceId = Integer.valueOf(ns);

        Key key1 = new KeyImpl(namespaceId, logicKey, vnode);

        String value = keyPair.getValue();
        Value value1 = new ValueImpl(value.getBytes(), System.currentTimeMillis());

        try {
            connection.put(key1, value1).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}

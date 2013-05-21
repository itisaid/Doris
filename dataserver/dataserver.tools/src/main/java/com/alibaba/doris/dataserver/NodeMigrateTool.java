package com.alibaba.doris.dataserver;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.alibaba.doris.cli.CommandLineHandler;
import com.alibaba.doris.cli.Option;
import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.ConnectionFactory;
import com.alibaba.doris.client.net.OperationFuture;
import com.alibaba.doris.common.data.ActionPair;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.dataserver.migrator.task.migrate.ExitPair;
import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.StorageConfig;
import com.alibaba.doris.dataserver.store.bdb.BDBStorageDriver;
import com.alibaba.doris.dataserver.store.log.LogStorageDriver;

/**
 * 节点数据迁移工具： 遍历指定目录下面的数据，并将数据迁移到指定的目标节点；<br>
 * srcPath：设置要迁移数据的原目录； <br>
 * targetIp: 设置目标节点的服务器ip和端口号；<br>
 * [threads]：设置远程写入线程数量；默认10个；<BR>
 * [namespace]: 设置要迁移数据属于指定的namespace；默认迁移所有namespace的数据；<BR>
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class NodeMigrateTool extends CommandLineHandler {

    public static void main(String[] args) {
        NodeMigrateTool migrationTool = new NodeMigrateTool();
        migrationTool.handle(args);
    }

    public NodeMigrateTool() {
        options.add(new Option("-srcPath", "srcPath", "The source path of migration data."));
        options.add(new Option("-configFile", "configFile", "The file path of storage configuration."));
        options.add(new Option("-targetIp", "targetIp", "The target dataserver ip."));
        options.add(new Option("-storageType", "Storage type",
                               "Setting the storage type: bdb or log. Default value is bdb.", false, true, "bdb"));
        options.add(new Option("-threads", "threads", "Setting the migrationg threads.", false, true, "10"));
        options.add(new Option("-namespace", "Name space", "Setting the name space which will migrating.", false, true,
                               null));
    }

    @Override
    public void prepareParameters() {
        srcPath = commandLine.getValue("-srcPath");
        targetIp = commandLine.getValue("-targetIp");
        threads = commandLine.getInt("-threads");
        namespace = commandLine.getValue("-namespace");
        storageType = commandLine.getValue("-storageType");
        configFile = commandLine.getValue("-configFile");
        System.out.println("--------------------configuration--------------------");
        System.out.println("srcPath=" + srcPath);
        System.out.println("targetIp=" + targetIp);
        System.out.println("threads=" + threads);
        System.out.println("namespace=" + namespace);
        System.out.println("storageType=" + storageType);
        System.out.println("configFile=" + configFile);
        System.out.println("-----------------------------------------------------");
    }

    @Override
    public void handleCommand() {
        List<MigrateDataThread> migrateThread = new ArrayList<MigrateDataThread>(threads);
        dataQueue = new ArrayBlockingQueue<Pair>(10000);

        // 启动迁移数据线程
        List<Future<TaskResult>> taskFuterList = startMigratingThread(migrateThread);

        // 启动定时打印统计数据线程；
        startPrinterThread(migrateThread);

        // 读取存储层数据；
        iteratorStorage();

        long totalRecord = 0;

        // 阻塞并等待各子任务的执行结果。
        for (int index = 0; index < taskFuterList.size(); index++) {
            Future<TaskResult> future = taskFuterList.get(index);
            try {
                totalRecord += future.get().successDataCount;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ConnectionFactory.getInstance().releaseResources();
        System.out.println("Finish migrating task. The total records is " + totalRecord);
    }

    protected void iteratorStorage() {
        Storage storage = getStorage();
        try {
            storage.open();
            Iterator<Pair> iterator = storage.iterator();
            while (iterator.hasNext()) {
                this.dataQueue.put(iterator.next());
            }

            for (int i = 0; i < threads; i++) {
                dataQueue.put(new ExitPair());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            storage.close();
        }
    }

    private Storage getStorage() {
        if ("log".equalsIgnoreCase(storageType)) {
            System.out.println("Loading log storage driver.");
            LogStorageDriver driver = new LogStorageDriver();
            driver.init(getStorageConfig());
            return driver.createStorage();
        } else {
            System.out.println("Loading bdb storage driver.");
            BDBStorageDriver driver = new BDBStorageDriver();
            driver.init(getStorageConfig());
            return driver.createStorage();
        }
    }

    private StorageConfig getStorageConfig() {
        StorageConfig storageConfig = new StorageConfig();
        storageConfig.setPropertiesFile(configFile);
        storageConfig.setDatabasePath(srcPath);
        return storageConfig;
    }

    protected List<Future<TaskResult>> startMigratingThread(List<MigrateDataThread> migrateThread) {
        List<Future<TaskResult>> taskFuterList = new ArrayList<Future<TaskResult>>(threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads; i++) {
            Callable<TaskResult> callableTask = new MigrateDataThread(dataQueue, targetIp);
            taskFuterList.add(executor.submit(callableTask));
            migrateThread.add((MigrateDataThread) callableTask);
        }

        return taskFuterList;
    }

    protected void startPrinterThread(final List<MigrateDataThread> migrateThread) {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {

            public void run() {
                long failedOpetionCount = 0;
                long successDataCount = 0;
                long totalTime = 0;

                for (MigrateDataThread mThread : migrateThread) {
                    failedOpetionCount += mThread.failedOpetionCount;
                    successDataCount += mThread.successDataCount;
                    totalTime += mThread.totalTime;
                }

                System.out.println("Total:" + (failedOpetionCount + successDataCount) + " sucess:" + successDataCount
                                   + " failed:" + failedOpetionCount + " success incr:"
                                   + (successDataCount - prevSuccessDataCount));

                prevSuccessDataCount = successDataCount;
                prevFailedOperationCount = failedOpetionCount;
            }

            private long prevFailedOperationCount;
            private long prevSuccessDataCount;

        }, 1, 1, TimeUnit.SECONDS);
    }

    private static class MigrateDataThread implements Callable<TaskResult> {

        public MigrateDataThread(BlockingQueue<Pair> dataQueue, String targetIp) {
            this.dataQueue = dataQueue;
            this.targetIp = targetIp;
            initConnection();
        }

        private void initConnection() {
            String[] pId = targetIp.split(":");
            String ip = pId[0];
            int port = Integer.valueOf(pId[1]).intValue();

            // 和目标机器建立连接
            InetSocketAddress remoteAddress = new InetSocketAddress(ip, port);
            ConnectionFactory factory = ConnectionFactory.getInstance();
            connection = factory.getConnection(remoteAddress);
        }

        public TaskResult call() {
            try {
                connection.open();
                while (true) {
                    Pair pair = dataQueue.take();
                    if (pair instanceof ExitPair) {
                        break;
                    }

                    long begin = System.currentTimeMillis();
                    migrateDataEntry(pair);
                    totalTime += System.currentTimeMillis() - begin;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.close();
            }

            System.out.println("Exit thread....");

            TaskResult result = new TaskResult();
            result.failedOpetionCount = failedOpetionCount;
            result.successDataCount = successDataCount;
            result.totalTime = totalTime;
            return result;
        }

        private void migrateDataEntry(Pair pair) throws InterruptedException, ExecutionException {
            // 取出要迁移的数据
            if (pair instanceof ActionPair) {
                ActionPair ap = (ActionPair) pair;
                if (ap.getActionType() == ActionPair.Type.SET) {
                    OperationFuture<Boolean> future = connection.cas(pair.getKey(), pair.getValue());
                    if (!future.get()) {
                        failedOpetionCount++;
                    } else {
                        successDataCount++;
                    }
                } else if (ap.getActionType() == ActionPair.Type.DELETE) {
                    OperationFuture<Boolean> future = connection.cad(pair.getKey(), pair.getValue());
                    if (!future.get()) {
                        failedOpetionCount++;
                    } else {
                        successDataCount++;
                    }
                }
            } else {
                OperationFuture<Boolean> future = connection.cas(pair.getKey(), pair.getValue());
                if (!future.get()) {
                    failedOpetionCount++;
                } else {
                    successDataCount++;
                }
            }
        }

        public long getTotalTime() {
            return totalTime;
        }

        private Connection          connection;
        private volatile int        successDataCount;
        private volatile int        failedOpetionCount;
        private String              targetIp;
        private BlockingQueue<Pair> dataQueue;
        private volatile long       totalTime;
    }

    private static class TaskResult {

        private int  successDataCount;
        private int  failedOpetionCount;
        private long totalTime;
    }

    private String              configFile;
    private String              srcPath;
    private String              targetIp;
    private int                 threads;
    private String              namespace;
    private BlockingQueue<Pair> dataQueue;
    private String              storageType;
}

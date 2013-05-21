package com.alibaba.doris.dataserver.migrator.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.NetException;
import com.alibaba.doris.client.net.OperationFuture;
import com.alibaba.doris.common.data.ActionPair;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.dataserver.migrator.MigrationException;
import com.alibaba.doris.dataserver.migrator.MigrationManager;
import com.alibaba.doris.dataserver.migrator.action.MigrationActionData;
import com.alibaba.doris.dataserver.migrator.connection.ConnectionManager;
import com.alibaba.doris.dataserver.migrator.task.migrate.ExitPair;
import com.alibaba.doris.dataserver.store.Storage;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class MigrateDataThread implements Callable<Integer> {

    public MigrateDataThread(BaseMigrationTask migrationTask, String targetMachine, BlockingQueue<Pair> dataQueue) {
        this.migrationTask = migrationTask;
        this.migrationManager = migrationTask.getMigrationManager();
        this.migrationActionData = migrationTask.getMigrationActionData();
        this.storage = migrationManager.getStorage();
        this.dataQueue = dataQueue;
        this.targetMachine = targetMachine;
        this.connectionManager = migrationTask.getConnectionManager();
    }

    public Integer call() throws Exception {
        // 获取和目标机器的连接
        Connection connection = connectionManager.getConnection(targetMachine);

        int mcount = 0;
        long start = System.currentTimeMillis();
        long totalTime = 0;

        while (true) {
            Pair pair = dataQueue.take();
            if (pair instanceof ExitPair) {
                break;
            }

            mcount++;

            if (migrationTask.isCancel()) {
                if (logger.isInfoEnabled()) {
                    logger.info("Migrate Task has been cancelled.Terminate it.");
                }
                break;
            }

            // TODO: 待puts实现后，可以批量传输，以加快迁移速度
            // 迁移一条数据到目标机器
            long begin = System.currentTimeMillis();
            try {
                migrateDataEntry(connection, pair);
                totalTime += System.currentTimeMillis() - begin;
            } catch (MigrationException e) {
                if (e.getCause() instanceof NetException) {
                    logger.error("Fail to Migrate data " + pair.getKey().getPhysicalKey() + ". Cause:" + e.getCause(),
                                 e.getCause());
                    Thread.sleep(RE_CONNECTE_INTERVAL);
                    connection = connectionManager.getConnection(targetMachine);
                    if (!connection.isConnected()) {
                        Thread.sleep(RE_CONNECTE_INTERVAL);
                        connection = connectionManager.getConnection(targetMachine);
                        if (!connection.isConnected()) {
                            throw new MigrationException("Migration failed bacause of  target connection close."
                                                         + targetMachine);
                        }
                    }

                    migrateDataEntry(connection, pair);
                } else {
                    logger.error("Fail to Migrate data " + pair.getKey().getPhysicalKey() + ". Cause:" + e, e);
                    throw e;
                }
            }
        }

        long end = System.currentTimeMillis();
        long ellapse = end - start;
        if (logger.isDebugEnabled()) {
            logger.debug("--------Migrate  data on storage by vnodes , to " + targetMachine + ",record count:" + mcount
                         + ", time:" + ellapse + "  write remote data(ms):" + totalTime);
        }

        return mcount;
    }

    /**
     * 迁移一条数据
     * 
     * @param connection
     * @param key
     * @param value
     * @throws MigrationException
     */
    protected void migrateDataEntry(Connection connection, Pair pair) throws MigrationException {
        Key key = pair.getKey();
        Value value = pair.getValue();

        Throwable failCause = null;
        try {

            migrateDataEntry0(connection, pair, key, value);
            return;
        } catch (Throwable t) {
            failCause = t;
        }

        throw new MigrationException("Fail to migrate data entry, cause:" + failCause, failCause);
    }

    /**
     * migrateDataEntry0
     * 
     * @param connection
     * @param pair
     * @param key
     * @param value
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private void migrateDataEntry0(Connection connection, Pair pair, Key key, Value value) throws InterruptedException,
                                                                                          ExecutionException {
        // 取出要迁移的数据
        if (pair instanceof ActionPair) {
            ActionPair ap = (ActionPair) pair;
            if (ap.getActionType() == ActionPair.Type.SET) {

                OperationFuture<Boolean> future = connection.cas(key, value);
                if (!future.get()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Migrating log item (cas), result is false. key=" + key + " value=" + value);
                    }
                }
            } else if (ap.getActionType() == ActionPair.Type.DELETE) {

                OperationFuture<Boolean> future = connection.cad(key, value);
                if (!future.get()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Migrating log item (cad), result is false. key=" + key + " value=" + value);
                    }
                }
            }
        } else {
            OperationFuture<Boolean> future = connection.cas(key, value);
            if (!future.get()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Migrating item (cas), result is false. key=" + key + " value=" + value);
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Migrating item (cas), result is true. key=" + key + " value=" + value);
                }
            }
        }
    }

    protected Logger              logger               = LoggerFactory.getLogger(MigrateDataThread.class);
    protected int                 retryCount           = 3;
    protected BaseMigrationTask   migrationTask;
    protected MigrationManager    migrationManager;
    protected Storage             storage;
    protected MigrationActionData migrationActionData;
    private BlockingQueue<Pair>   dataQueue;
    private String                targetMachine;
    private ConnectionManager     connectionManager;
    private static final int      RE_CONNECTE_INTERVAL = 1000;
}

/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.dataserver.migrator.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.client.net.NetException;
import com.alibaba.doris.common.MigrateTypeEnum;
import com.alibaba.doris.common.migrate.NodeMigrateStatus;
import com.alibaba.doris.common.route.MigrationRoutePair;
import com.alibaba.doris.dataserver.migrator.MigrationException;
import com.alibaba.doris.dataserver.migrator.MigrationManager;
import com.alibaba.doris.dataserver.migrator.action.MigrationActionData;
import com.alibaba.doris.dataserver.migrator.connection.ConnectionManager;
import com.alibaba.doris.dataserver.migrator.event.MigrationEvent;
import com.alibaba.doris.dataserver.migrator.event.MigrationListener;
import com.alibaba.doris.dataserver.migrator.filter.MigrationVirtualNodeFinder;
import com.alibaba.doris.dataserver.migrator.task.dataclean.DataCleanExecutor;
import com.alibaba.doris.dataserver.migrator.utils.DateUtil;

/**
 * MigrationTask
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-25
 */
public abstract class BaseMigrationTask extends Thread implements MigrationTask {

    private static final Logger          logger                                     = LoggerFactory.getLogger(BaseMigrationTask.class);
    // 等待所有任务迁移完成，接受到删除数据指令的等待超时时间；默认为5小时
    private static final long            DEFAULT_DATACLEAN_SIGNAL_WAITTING_TIME_OUT = 5;
    // 前台代理连接管理器
    private ConnectionManager            proxyConnectionManager;

    protected String                     taskName;
    protected String                     taskKey;
    protected MigrateTypeEnum            migrateType;

    protected String                     message;
    protected MigrationActionData        migrationActionData;
    protected NodeMigrateStatus          migrateStatus                              = NodeMigrateStatus.NORMAL;
    protected int                        progress                                   = DEFAULT_PROCESS;
    protected boolean                    finished;

    final protected long                 createTime                                 = System.currentTimeMillis();
    protected long                       startTime;
    protected long                       cancelTime;
    protected long                       finishTime;

    protected MigrationManager           migrationManager;
    protected DataCleanExecutor          dataCleanExecutor;
    protected DataMigrationExecutor      dataMigrationExecutor;
    protected List<MigrationListener>    listeners                                  = new ArrayList<MigrationListener>(
                                                                                                                       3);
    protected MigrationReportTimer       migrationReportTimer;
    public static final int              DEFAULT_PROCESS                            = -1;

    protected MigrationVirtualNodeFinder migrationVirtualNodeFinder;                                                                   // targetNode
    // <->
    // vnode
    // index
    // of
    // migrationActionData.
    protected ProgressComputer           progressComputer;

    protected Semaphore                  cancelSignal                               = new Semaphore(0);
    protected Semaphore                  dataCleanStartSignal                       = new Semaphore(0);

    protected ReentrantLock              controlLock                                = new ReentrantLock();

    public Semaphore getDataCleanStartSignal() {
        return dataCleanStartSignal;
    }

    public BaseMigrationTask(String taskName, MigrationManager migrationManager) {
        super(taskName + "-" + new Date());
        this.taskName = taskName;
        this.migrationManager = migrationManager;
        this.proxyConnectionManager = migrationManager.getMigrationConnectionManager();
    }

    public String getTaskName() {
        return taskName;
    }

    public MigrateTypeEnum getMigrateType() {
        return this.migrateType;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setMigrateType(MigrateTypeEnum migrateType) {
        this.migrateType = migrateType;
    }

    public void setMigrationActionData(MigrationActionData migrationActionData) {
        this.migrationActionData = migrationActionData;

        this.taskKey = BaseMigrationTask.buildTaskKey(this.migrationActionData);
        initMigrationVirtualNodeFinder(migrationActionData.getMigrationRoutePairs());
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public String getMessage() {
        return message;
    }

    public void setMigrationManager(MigrationManager migrationManager) {
        this.migrationManager = migrationManager;
    }

    protected void initMigrationVirtualNodeFinder(List<MigrationRoutePair> migrationRoutePairs) {
        this.migrationVirtualNodeFinder = new MigrationVirtualNodeFinder(migrationRoutePairs);
    }

    public MigrationVirtualNodeFinder getMigrationVirtualNodeFinder() {
        return migrationVirtualNodeFinder;
    }

    /**
     * 获取Cancel信号量.
     * 
     * @return
     */
    public Semaphore getCancelSignal() {
        return cancelSignal;
    }

    /**
     * 开始一个迁移任务
     */
    public void run() {
        // do
        try {
            // start0
            if (begin() == false) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Fail to start new migration task thread.  " + getMigrateType() + ", currrent state: "
                                 + migrateStatus);
                }

                return;
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Start a migrate task " + getMigrateType() + ",route: "
                             + migrationActionData.getMigrationRouteString());
            }

            migrate();

            // finish
            finish();

            dataCleanPrepare();
        } catch (MigrationException e) {
            logger.error("Migration Task Error:" + e, e);
            notifyMigrateError(e.toString());

        } catch (Throwable t) {
            logger.error("Migration Task Error:" + t, t);
            notifyMigrateError(t.toString());

        } finally {
            if (logger.isInfoEnabled()) {
                logger.info("Migration Task Thread Finished, and exit.");
            }

            finished = true;
            finishTime = System.currentTimeMillis();
            if (migrationReportTimer != null) {
                migrationReportTimer.cancel();
            }

            notifyExitMigrationTask();

            // 通知退出任务后等待2s再释放所有连接；
            sleeps(2000);

            // 释放代理过程中用到的连接；
            releaseProxyConnection();
        }
    }

    public boolean prepareTask() {
        if (!establishProxyConnection()) {
            return false;
        }
        return true;
    }

    /**
     * 数据清理准备，不立即开始清理，任务阻塞. 等待 All_Finished 指令收到后才开始.
     */
    protected void dataCleanPrepare() throws MigrationException {
        try {
            if (logger.isWarnEnabled()) {
                logger.warn("Prepare data cleaning.");
            }

            if (!dataCleanStartSignal.tryAcquire(DEFAULT_DATACLEAN_SIGNAL_WAITTING_TIME_OUT, TimeUnit.HOURS)) {
                logger.error("Waiting data clean signal time out! After " + DEFAULT_DATACLEAN_SIGNAL_WAITTING_TIME_OUT
                             + " hours.");
                throw new MigrationException("Waiting all_finished command time out!");
            }

            // 当cancel任务时，不能删除本地的数据。
            if (!isFinished()) {
                dataClean();
            } else {
                logger.warn("To skip data cleaning.");
            }
        } catch (InterruptedException e) {
            logger.error("Waiting the start signal of data cleaning failed!", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 开始迁移任务。为了和 thread 和 start 方法区别。
     * 
     * @return
     */
    protected boolean begin() {
        controlLock.lock();
        try {
            if (migrateStatus != NodeMigrateStatus.NORMAL) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Node status is " + NodeMigrateStatus.MIGRATING + " or "
                                 + NodeMigrateStatus.MIGRATE_NODE_FINISHED + ",Migraion task can't start!");
                }

                return false;
            }

            // Notify listener, Before we set the status of task to be migrating, we should establish the connections of
            // Proxy first.
            notifyMigrateStart();
            return true;
        } finally {
            controlLock.unlock();
        }
    }

    protected boolean establishProxyConnection() {
        try {
            proxyConnectionManager.connect(migrationActionData.getMigrationRoutePairs());
        } catch (NetException e) {
            String msg = "Can't establish migration connection. Give up migration task." + e;
            logger.error(msg);

            notifyMigrateError(e.getMessage());
            return false;
        }

        logger.info("Succeed to create migraion connections.");
        return true;
    }

    protected void releaseProxyConnection() {
        proxyConnectionManager.close();
    }

    /**
     * migrate
     * 
     * @throws MigrationException
     * @see com.alibaba.doris.dataserver.migrator.task.BaseMigrationTask#migrate()
     */
    public void migrate() throws MigrationException {
        controlLock.lock();
        try {
            migrate0();
        } finally {
            controlLock.unlock();
        }
    }

    private void migrate0() throws MigrationException {
        migrationReportTimer = getMigrationReportTimer();
        migrationReportTimer.start();

        dataMigrationExecutor = getDataMigrationExecutor();
        dataMigrationExecutor.execute();
    }

    protected DataMigrationExecutor getDataMigrationExecutor() {
        DataMigrationExecutor executor = new DataMigrationExecutor();
        executor.setMigrationTask(this);
        return executor;
    }

    protected MigrationReportTimer getMigrationReportTimer() {
        MigrationReportTimer timer = new MigrationReportTimer();
        timer.setMigrationTask(this);
        return timer;
    }

    protected DataCleanExecutor getDataCleanExecutor() {
        DataCleanExecutor executor = new DataCleanExecutor();
        executor.setMigrationTask(this);
        return executor;
    }

    /**
     * 完成本次迁移任务
     */
    protected void finish() {
        controlLock.lock();
        try {
            finishTime = System.currentTimeMillis();
            notifyMigrateNodeFinish();
        } finally {
            controlLock.unlock();
        }
    }

    /**
     * 数据清理
     */
    private void dataClean0() {
        if (logger.isInfoEnabled()) {
            logger.info("Start migrate data clean.");
        }

        notifyDataCleaningStart();

        // 通知启动数据清理后，等待5s开始实际启动数据删除任务；
        sleeps(5000);

        // 在执行器中，会调用 task 通知数据清理进度.
        try {
            dataCleanExecutor = getDataCleanExecutor();
            if (null != dataCleanExecutor) {
                dataCleanExecutor.execute();
            }

            notifyDataCleanFinish();

            if (logger.isDebugEnabled()) {
                logger.debug("DataClean Finish................");
            }
        } catch (MigrationException e) {
            notifyDataCleanError(e.getMessage());
        }
    }

    public void sleeps(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private MigrationEvent createMigrationEvent() {
        MigrationEvent event = new MigrationEvent();
        event.setMigrateType(getMigrateType());
        event.setMigrationTask(this);
        event.setMigrateRoute(migrationActionData.getMigrationRoutePairs());
        event.setServerPort(migrationManager.getPort());
        return event;
    }

    /**
     * Cancel right now
     * 
     * @see com.alibaba.doris.dataserver.migrator.task.BaseMigrationTask#cancel()
     */
    public void cancel() {
        cancel(0);
    }

    /**
     * @see com.alibaba.doris.dataserver.migrator.task.BaseMigrationTask#cancel()
     */
    public void cancel0() {
        controlLock.lock();
        try {
            if (!finished) {
                migrateStatus = NodeMigrateStatus.CANCELLING;
                cancelTime = System.currentTimeMillis();
                finished = true;

                // 如果正在迁移数据通知迁移任务取消；
                if (null != dataMigrationExecutor) {
                    dataMigrationExecutor.cancel();
                }

                // 如果在删除数据，则通知取消；
                if (null != dataCleanExecutor) {
                    dataCleanExecutor.cancel();
                }

                notifyMigrateCancel();
                dataCleanStartSignal.release();

                migrateStatus = NodeMigrateStatus.CANCELLED;
            }
        } finally {
            controlLock.unlock();
        }
    }

    public boolean isFinished() {
        return finished;
    }

    /**
     * 在指定时间内cancel 迁移任务.
     * 
     * @see com.alibaba.doris.dataserver.migrator.task.BaseMigrationTask#cancel()
     */
    public void cancel(long timeout) {
        controlLock.lock();
        try {
            cancel0();
        } finally {
            controlLock.unlock();
        }
    }

    /**
     * @see com.alibaba.doris.dataserver.migrator.task.BaseMigrationTask#isCancel()
     */
    public boolean isCancel() {
        return migrateStatus == NodeMigrateStatus.CANCELLED || migrateStatus == NodeMigrateStatus.CANCELLING
               || finished == true;
    }

    /**
     * @see com.alibaba.doris.dataserver.migrator.task.BaseMigrationTask#isNodeFinish()
     */
    public boolean isNodeFinish() {
        return migrateStatus == NodeMigrateStatus.MIGRATE_NODE_FINISHED;
    }

    /**
     * 一次集群迁移整体结束，本Node回复NORMAL状态
     */
    public void allFinish() {
        allFinished0();
    }

    /**
     * 通过信号量通知数据数据清理开始.
     */
    public void dataCleanStart() {
        controlLock.lock();
        try {
            dataCleanStartSignal.release();
            if (logger.isInfoEnabled()) {
                logger.info("Migrate data clean start notify.");
            }
        } finally {
            controlLock.unlock();
        }

    }

    private void allFinished0() {
        controlLock.lock();
        try {
            boolean hasLatestRouteTable = migrationManager.refreshRouteTable();
            if (!hasLatestRouteTable) {
                // 如果第一次刷新路由，没有路由更新信息，则等待5s继续查看；路由有可能被定时刷新线程刷新到最新；
                sleeps(5000);

                hasLatestRouteTable = migrationManager.refreshRouteTable();
                if (!hasLatestRouteTable) {
                    logger.error("Warring: Migration manager want to refresh the route table, but the route table has nothing changed.");
                }
            }

            if (logger.isInfoEnabled()) {
                logger.info("Migration allFinish operaiton complete.  Task: " + this);
            }

            notifyMigrateAllFinish();

            // 获取最新的RouteTable.
            if (logger.isInfoEnabled()) {
                logger.info("Data migration ALL_FINSIHED.");
            }

            if (logger.isInfoEnabled()) {
                logger.info("Data clean starting by task: " + this);
            }

            // 开始数据清理
            this.dataCleanStart();

        } finally {
            controlLock.unlock();
        }
    }

    /**
     * 执行数据清理
     */
    public void dataClean() {
        controlLock.lock();
        try {
            dataClean0();
        } finally {
            controlLock.unlock();
        }
    }

    /**
     * @see com.alibaba.doris.dataserver.migrator.task.BaseMigrationTask#addListener(com.alibaba.doris.dataserver.migrator.event.MigrationListener)
     */
    public void addListener(MigrationListener listener) {
        this.listeners.add(listener);
    }

    /**
     * @see com.alibaba.doris.dataserver.migrator.task.BaseMigrationTask#removeListener(com.alibaba.doris.dataserver.migrator.event.MigrationListener)
     */
    public void removeListener(MigrationListener listener) {
        this.listeners.remove(listener);

    }

    /**
     * getMigrationManager
     */
    public MigrationManager getMigrationManager() {
        return migrationManager;
    }

    /**
     * 通知迁移开始
     */
    protected void notifyMigrateStart() {
        controlLock.lock();
        try {
            this.progress = 0;
            this.migrateStatus = NodeMigrateStatus.MIGRATING;
            this.startTime = System.currentTimeMillis();

            if (logger.isDebugEnabled()) {
                logger.debug("notifyMigrateStart, migrateStatus: " + migrateStatus + ", progress: " + progress
                             + ", task: " + this);
            }

            MigrationEvent event = createMigrationEvent();

            event.setProgress(this.progress);
            event.setMigrateStatus(this.migrateStatus);
            event.setMigrateType(getMigrateType());
            for (MigrationListener listener : listeners) {
                listener.onMigrationStart(event);
            }
        } finally {
            controlLock.unlock();
        }
    }

    /**
     * 通知迁移取消
     */
    protected void notifyMigrateCancel() {

        controlLock.lock();

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("notifyMigrateCancel, current migrateStatus: " + migrateStatus + ", progress: " + progress
                             + ", task: " + this);
            }
            MigrationEvent event = createMigrationEvent();

            event.setProgress(this.progress);
            event.setMigrateStatus(this.migrateStatus);

            for (MigrationListener listener : listeners) {
                listener.onMigrationCancelled(event);
            }

            revert2NormalState();

        } finally {
            controlLock.unlock();
        }
    }

    protected void notifyMigrateNodeFinish() {
        controlLock.lock();
        try {
            this.migrateStatus = NodeMigrateStatus.MIGRATE_NODE_FINISHED;
            this.progress = 100;

            if (logger.isDebugEnabled()) {
                logger.debug("DataMigration Finish, notifyMigrateNodeFinish: migrateStatus: " + migrateStatus
                             + ", progress: " + progress + ", task: " + this);
            }

            MigrationEvent event = createMigrationEvent();

            event.setProgress(this.progress);
            event.setMigrateStatus(this.migrateStatus);

            for (MigrationListener listener : listeners) {
                listener.onMigrationNodeFinished(event);
            }
        } finally {
            controlLock.unlock();
        }
    }

    /**
     * 通知迁移所有完成
     */
    protected void notifyMigrateAllFinish() {
        controlLock.lock();
        try {
            this.migrateStatus = NodeMigrateStatus.MIGRATE_ALL_FINISHED;
            this.progress = 100;

            if (logger.isDebugEnabled()) {
                logger.debug("DataMigration All Finish, notifyMigrateAllFinish: migrateStatus: " + migrateStatus
                             + ", progress: " + progress + ", task: " + this);
            }

            MigrationEvent event = createMigrationEvent();

            event.setProgress(this.progress);
            event.setMigrateStatus(this.migrateStatus);
            for (MigrationListener listener : listeners) {
                listener.onMigrationAllFinished(event);
            }

        } finally {
            controlLock.unlock();
        }
    }

    /**
     * 通知迁移失败
     * 
     * @param t
     */
    protected void notifyMigrateError(String message) {
        controlLock.lock();
        try {

            MigrationEvent event = createMigrationEvent();

            event.setProgress(this.progress);
            event.setMigrateStatus(this.migrateStatus);
            event.setMessage("Migrate Fail:" + message);
            event.setFailed(true);

            for (MigrationListener listener : listeners) {
                listener.onMigrationFail(event);
            }

            revert2NormalState();

        } finally {
            controlLock.unlock();
        }
    }

    protected void revert2NormalState() {
        // controlLock.lock();
        // try {
        this.migrateStatus = NodeMigrateStatus.NORMAL;
        this.progress = -1;

        this.finishTime = System.currentTimeMillis();

        MigrationEvent event2 = createMigrationEvent();

        event2.setProgress(this.progress);
        event2.setMigrateStatus(this.migrateStatus);
        event2.setMessage("Migrate revert to normal state");
        event2.setFailed(false);
        event2.setMigrateType(getMigrateType());
        event2.setMigrationTask(this);
        // }finally {
        // controlLock.unlock();
        // }
    }

    /**
     * 通知迁移进度.
     */
    public void notifyMigrateProgress(int newProgress) {
        controlLock.lock();
        try {
            this.migrateStatus = NodeMigrateStatus.MIGRATING;
            if (newProgress == 100) {
                this.progress = 99;
            } else {
                this.progress = newProgress;
            }

            if (logger.isDebugEnabled()) {
                logger.debug("notifyMigrateProgress: migrateStatus: " + migrateStatus + ", progress: " + progress
                             + ", task: " + this);
            }

            notifyMigrateOrDataCleanProgress();
        } finally {
            controlLock.unlock();
        }
    }

    public void notifyMigrateOrDataCleanProgress() {
        // controlLock.lock();
        // try {

        if ((migrateStatus == NodeMigrateStatus.MIGRATING || migrateStatus == NodeMigrateStatus.DATACLEANING)
            && progress < 100) {

            if (logger.isInfoEnabled()) {
                logger.info(String.format("Progress  Report - status: %s.   Task:%s", progress,
                                          migrateStatus.toString(), this.toString()));
            }
            MigrationEvent event = createMigrationEvent();

            event.setProgress(this.progress);
            event.setMigrateStatus(this.migrateStatus);

            for (MigrationListener listener : listeners) {
                listener.onMigraionProcessing(event);
            }
        }

        // }finally {
        // controlLock.unlock();
        // }
    }

    /**
     * 通知系统正在退出当前迁移任务；
     */
    protected void notifyExitMigrationTask() {
        controlLock.lock();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("notifyExitMigrationTask.");
            }

            MigrationEvent event = createMigrationEvent();
            event.setProgress(this.progress);
            event.setMigrateStatus(this.migrateStatus);

            for (MigrationListener listener : listeners) {
                listener.onExitMigrationTask(event);
            }
        } finally {
            controlLock.unlock();
        }
    }

    /**
     * 通知数据清理开始.
     */
    protected void notifyDataCleaningStart() {
        controlLock.lock();
        try {
            this.migrateStatus = NodeMigrateStatus.DATACLEANING;
            this.progress = 0;

            if (logger.isDebugEnabled()) {
                logger.debug("notifyDataCleaningStart: migrateStatus: " + migrateStatus + ", progress: " + progress);
            }

            MigrationEvent event = createMigrationEvent();
            event.setProgress(this.progress);
            event.setMigrateStatus(this.migrateStatus);

            for (MigrationListener listener : listeners) {
                listener.onDataCleanStart(event);
            }
        } finally {
            controlLock.unlock();
        }
    }

    protected void notifyDataCleanError(String string) {
        controlLock.lock();
        try {
            MigrationEvent event = createMigrationEvent();

            event.setProgress(this.progress);
            event.setMigrateStatus(this.migrateStatus);
            event.setMessage("DataClean Fail:" + message);
            event.setFailed(true);
            for (MigrationListener listener : listeners) {
                listener.onDataCleanError(event);
            }

            revert2NormalState();
        } finally {
            controlLock.unlock();
        }
    }

    /**
     * 通知数据清理完毕.
     */
    protected void notifyDataCleanFinish() {

        controlLock.lock();
        try {
            this.migrateStatus = NodeMigrateStatus.DATACLEAN_FINISH;
            this.progress = 100;

            if (logger.isDebugEnabled()) {
                logger.debug("notifyDataCleanFinish: migrateStatus: " + migrateStatus + ", progress: " + progress);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("notifyDataCleanFinish.");
            }

            MigrationEvent event = createMigrationEvent();
            event.setProgress(this.progress);
            event.setMigrateStatus(this.migrateStatus);

            for (MigrationListener listener : listeners) {
                listener.onDataCleanFinish(event);
            }
            migrationManager.onDataCleanFinish(event);
            migrationManager.completeAndClearMigration(migrationActionData);

        } finally {
            controlLock.unlock();
        }
    }

    /**
     * 通知数据清理进度
     * 
     * @param newProgress
     */
    public void notifyDataCleanProcess(int newProgress) {
        controlLock.lock();
        try {
            this.migrateStatus = NodeMigrateStatus.DATACLEANING;
            this.progress = newProgress;

            notifyDataCleanProcess();
        } finally {
            controlLock.unlock();
        }
    }

    public void notifyDataCleanProcess() {
        // controlLock.lock();
        // try {
        if (migrateStatus == NodeMigrateStatus.DATACLEANING && progress < 100) {
            MigrationEvent event = createMigrationEvent();
            event.setProgress(this.progress);
            event.setMigrateStatus(this.migrateStatus);

            for (MigrationListener listener : listeners) {
                listener.onDataCleanProcessing(event);
            }
        }
        // }finally {
        // controlLock.unlock();
        // }
    }

    public long getStartTime() {
        return startTime;
    }

    public long getCancelTime() {
        return cancelTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public NodeMigrateStatus getMigrateStatus() {
        return migrateStatus;
    }

    public void setMigrateStatus(NodeMigrateStatus status) {
        this.migrateStatus = status;
    }

    public List<MigrationListener> getListeners() {
        return listeners;
    }

    public MigrationActionData getMigrationActionData() {
        return migrationActionData;
    }

    public void setFinish(boolean b) {
        this.finished = true;
    }

    public static String buildTaskKey(MigrationActionData actionData) {
        MigrateTypeEnum migrateType = actionData.getSubcommand().getMigrateType();
        String taskKey = migrateType + "-" + actionData.getHashKey();
        return taskKey;
    }

    public static MigrateTypeEnum getMigrateType(MigrationActionData actionData) {
        MigrateTypeEnum migrateType = actionData.getSubcommand().getMigrateType();
        return migrateType;
    }

    public String getProxyTarget(int vnode) {
        return migrationVirtualNodeFinder.getTargetNodeOfVirtualNode(vnode);
    }

    public ConnectionManager getConnectionManager() {
        return this.proxyConnectionManager;
    }

    @Override
    public String toString() {
        return new StringBuilder(128).append("[MigrateTask:").append(getMigrateType()).append(",TaskKey:").append(
                                                                                                                  taskKey).append(
                                                                                                                                  ",Status:").append(
                                                                                                                                                     migrateStatus).append(
                                                                                                                                                                           ",Progress:").append(
                                                                                                                                                                                                progress).append(
                                                                                                                                                                                                                 ",CreateTime:").append(
                                                                                                                                                                                                                                        createTime).append(
                                                                                                                                                                                                                                                           ",StartTime:").append(
                                                                                                                                                                                                                                                                                 DateUtil.formatDate(startTime)).append(
                                                                                                                                                                                                                                                                                                                        ",FinishTime:").append(
                                                                                                                                                                                                                                                                                                                                               DateUtil.formatDate(finishTime)).append(
                                                                                                                                                                                                                                                                                                                                                                                       "]").toString();
    }
}

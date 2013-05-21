/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.dataserver.migrator;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.MigrateTypeEnum;
import com.alibaba.doris.common.config.ConfigManager;
import com.alibaba.doris.common.configer.RouteTableConfiger;
import com.alibaba.doris.common.migrate.MigrateStatusReport;
import com.alibaba.doris.common.migrate.MigrateSubCommand;
import com.alibaba.doris.common.migrate.NodeMigrateStatus;
import com.alibaba.doris.common.route.VirtualRouter;
import com.alibaba.doris.dataserver.migrator.action.MigrationActionData;
import com.alibaba.doris.dataserver.migrator.connection.ConnectionManager;
import com.alibaba.doris.dataserver.migrator.connection.MigrationConnectionManager;
import com.alibaba.doris.dataserver.migrator.event.DefaultMigrationListener;
import com.alibaba.doris.dataserver.migrator.event.MigrationEvent;
import com.alibaba.doris.dataserver.migrator.event.MigrationListener;
import com.alibaba.doris.dataserver.migrator.report.MigrationReporter;
import com.alibaba.doris.dataserver.migrator.task.BaseMigrationTask;
import com.alibaba.doris.dataserver.migrator.task.MigrationTask;
import com.alibaba.doris.dataserver.migrator.task.MigrationTaskFactory;
import com.alibaba.doris.dataserver.migrator.task.MigrationThreadFactory;
import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.fastjson.JSON;

/**
 * MigrationManager.
 * <p/>
 * 1. 迁移开始 ( 或迁移无法开始 ) 2. 节点迁移完毕( 或 节点迁移取消/ 或节点迁移失败 ) 3. 集群整体迁移完毕. 4. 节点数据清理. 5. 节点数据清理完毕.
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-25
 */
public class MigrationManager implements MigrationListener {

    private static final Logger               logger                 = LoggerFactory.getLogger(MigrationManager.class);

    public final static String                _MigrationManager      = "_MigrationManager";
    private static MigrationManager           manager                = new MigrationManager();

    private MigrationTaskFactory              taskFactory            = new MigrationTaskFactory();

    // 代表当前处于迁移中或尚未完成的迁移信息存储。
    protected volatile Executor               executor               = Executors.newCachedThreadPool(new MigrationThreadFactory());
    protected int                             migrateThreads;

    protected volatile MigrationTaskScheduler migrationTaskScheduler = new MigrationTaskScheduler();

    // 其他固定变量
    private int                               port;                                                                                // dataserver
    // 端口.

    protected RouteTableConfiger              routeTableConfiger;

    protected ConfigManager                   configManager;
    protected MigrationReporter               migrationReporter;
    protected VirtualRouter                   virtualRouter;
    protected Storage                         storage;

    private ReentrantLock                     controlLock            = new ReentrantLock();

    /**
     * 迁移指令执行结果消息 Message
     * 
     * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
     * @since 1.0 2011-5-31
     */
    public static class Message {

        public static final String _MIGRATION_START_TASK_ERROR     = "OK MIGRATION_NEW_TASK";

        public static final String _MIGRATION_NEW_TASK             = "OK MIGRATION_NEW_TASK";
        public static final String _MIGRATION_REPLACE_TASK         = "OK MIGRATION_REPLACE_TASK";
        public static final String _MIGRATION_TASK_CANCEL          = "OK MIGRATION_TASK_CANCEL";
        public static final String _MIGRATION_NO_TASK_TO_CANCEL    = "OK MIGRATION_NO_TASK_TO_CANCEL";
        public static final String _MIGRATION_TASK_CANT_CANCEL     = "OK MIGRATION_TASK_CANT_CANCEL";

        public static final String _MIGRATION_SAME_TASK_EXISTS     = "OK MIGRATION_SAME_TASK_EXISTS";
        public static final String _MIGRATION_PRIOR_TASK_EXISTS    = "OK MIGRATION_PRIOR_TASK_EXISTS";
        public static final String _MIGRATION_ALL_FINISHED         = "OK MIGRATION_ALL_FINISHED";

        /* 数据清理信息 */
        public static final String _MIGRATION_DATA_CLEANING        = "OK MIGRATION_DATA_CLEANING";
        public static final String _MIGRATION_DATA_CLEAN_FINISHED  = "OK MIGRATION_DATA_CLEAN_FINISHED";

        public static final String _MIGRATION_INVALID_ALL_FINISHED = "OK MIGRATION_INVALID_ALL_FINISHED";

        public static final String _MIGRATION_NO_TASK_RUNNING      = "OK MIGRATION_NO_TASK_RUNNING";

        public final static String _DATACLEAN_START                = "OK DATACLEAN_START";

    }

    public static MigrationManager getInstance() {
        return manager;
    }

    public MigrationTaskScheduler getMigrationTaskScheduler() {
        return migrationTaskScheduler;
    }

    public int getMigrateThreads() {
        return migrateThreads;
    }

    public void setMigrateThreads(int migrateThreads) {
        this.migrateThreads = migrateThreads;
    }

    /**
     * 判断当前状态
     * 
     * @return
     */
    public boolean haveMigrationTask() {
        MigrationTask lastTask = migrationTaskScheduler.getLastTask();
        if (lastTask != null) {
            if (lastTask.getMigrateType() == MigrateTypeEnum.TEMP_FAILOVER) {
                // 基于lastTask来判断的依据是：1.只有临时节点才存在临时失效task 2.临时失效可能同时存在多个任务。
                Map<String, MigrationTask> taskMap = migrationTaskScheduler.getActiveTaskMap();
                Iterator<MigrationTask> taskIterator = taskMap.values().iterator();
                while (taskIterator.hasNext()) {
                    MigrationTask task = taskIterator.next();
                    if (task != null) {
                        if (isMigratingData(task)) {
                            return true;
                        }
                    }
                }
            } else {
                return isMigratingData(lastTask);
            }
        }

        return false;
    }

    /**
     * 判断当前任务是否真正迁移数据；
     * 
     * @param task
     * @return
     */
    private boolean isMigratingData(MigrationTask task) {
        switch (task.getMigrateStatus()) {
            case MIGRATING:
            case MIGRATE_NODE_FINISHED:
            case MIGRATE_ALL_FINISHED:
            case DATACLEANING:
            case DATACLEAN_FINISH:
            case CANCELLED:
            case CANCELLING:
                return true;
        }
        return false;
    }

    /**
     * 启动迁移.
     * <p/>
     * 进行必要的逻辑判断再执行任务.
     * 
     * @param migrationParam
     * @return
     */
    public String startMigrate(MigrationActionData migrationActionData) {
        controlLock.lock();
        try {
            String retMsg = startMigrate0(migrationActionData);
            return retMsg;
        } finally {
            controlLock.unlock();
        }
    }

    /**
     * @param migrationActionData
     * @return
     */
    private String startMigrate0(MigrationActionData migrationActionData) {
        String retMsg = null;
        MigrateSubCommand subCommand = migrationActionData.getSubcommand();

        if (logger.isDebugEnabled()) {
            logger.debug("Receive new migration task, type " + subCommand + ", route:" + migrationActionData);
        }

        migrationTaskScheduler.checkAndTerminateFinishedTask();

        BaseMigrationTask newTask = taskFactory.createTask(this, migrationActionData);

        if (!migrationTaskScheduler.hasActiveTask()) {
            if (logger.isDebugEnabled()) {
                logger.debug("There is no active migration task. Preprare to start new one." + newTask);
            }

            retMsg = startTask0(migrationActionData, newTask);
            migrationActionData.setSuccess(true);

            if (logger.isDebugEnabled()) {
                logger.debug("Migration task started. " + newTask.getTaskName() + ". " + newTask);
            }
        } else { // 判断优先级，如果新任务优先级高，则取消当前执行的任务，执行新的任务
            MigrationTask lastActiveTask = migrationTaskScheduler.getLastTask();

            int newTaskPriority = newTask.getMigrateType().getPriority();
            int activeTaskPriority = lastActiveTask.getMigrateType().getPriority();

            if (newTaskPriority < activeTaskPriority) {
                if (logger.isDebugEnabled()) {
                    logger.debug("An active migration task exists, the active will cancel and new will start.Active Task: "
                                 + lastActiveTask + ", New Task:" + newTask);
                }
                return cancelTaskAndStartNewOne(migrationActionData, lastActiveTask, newTask);
            } else if (newTaskPriority == activeTaskPriority) {
                // 都是临时失效回迁, 同时起多个任务
                if (lastActiveTask.getMigrateType() == MigrateTypeEnum.TEMP_FAILOVER
                    && newTask.getMigrateType() == MigrateTypeEnum.TEMP_FAILOVER) {
                    // 查看新启动的迁移任务是否已经存在；注释：修复原有的某些情况下，重新启动临时失效回迁会启动新任务的bug；
                    MigrationTask existsTask = migrationTaskScheduler.getTask(newTask.getTaskKey());

                    // 如果待启动的迁移任务已经存在；
                    if (null != existsTask) {
                        // 如果当前task正在等待数据清理，再次收到迁移开始指令，需要取消原有task并重新启动一个迁移任务。
                        if (existsTask.getMigrateStatus() == NodeMigrateStatus.MIGRATE_NODE_FINISHED) {
                            retMsg = cancelTaskAndStartNewOne(migrationActionData, existsTask, newTask);
                        } else {
                            // 否则只打印一条任务已经存在的log信息就返回；
                            if (logger.isDebugEnabled()) {
                                logger.debug("A Tempfailover migration task exists. And same migration route is request. It's rejected!. ActiveTask:"
                                             + existsTask + ",newTask:" + newTask);
                            }
                            retMsg = Message._MIGRATION_SAME_TASK_EXISTS;
                        }

                        return retMsg;
                    } else {// 是一个新的临时失效迁移任务；
                        if (logger.isDebugEnabled()) {
                            logger.debug("Start a new migration task. newTask:" + newTask);
                        }

                        startTask0(migrationActionData, newTask);
                        retMsg = Message._MIGRATION_NEW_TASK;
                        return retMsg;
                    }
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("An same prior migration task exists. Ignore new one.   ActiveTask:"
                                     + lastActiveTask + ", newTask:" + newTask);
                    }

                    retMsg = Message._MIGRATION_SAME_TASK_EXISTS;
                    return retMsg;
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("An prior active migration task exists. New command ir rejected! " + lastActiveTask);
                }

                retMsg = Message._MIGRATION_PRIOR_TASK_EXISTS;
            }
        }

        return retMsg;
    }

    private String cancelTaskAndStartNewOne(MigrationActionData migrationActionData, MigrationTask activeTask,
                                            BaseMigrationTask newTask) {
        String retMsg = null;
        // 取消当前任务，等待其取消完毕.
        cancelActiveTask((BaseMigrationTask) activeTask);

        if (logger.isDebugEnabled()) {
            logger.debug("Cancel active migration task." + activeTask);
        }

        startTask0(migrationActionData, newTask);

        migrationActionData.setSuccess(true);
        retMsg = Message._MIGRATION_REPLACE_TASK;
        return retMsg;
    }

    /**
     * @param migrationActionData
     * @param migrateType
     * @param newTask
     * @param activeTask0
     */
    private String startTask0(MigrationActionData migrationActionData, BaseMigrationTask newTask) {

        String retMsg = Message._MIGRATION_NEW_TASK;

        // newTask.setMigrateStatus( NodeMigrateStatus.MIGRATING );
        newTask.setProgress(0);

        MigrationListener reportListener = new DefaultMigrationListener();
        reportListener.setMigrationManager(this);

        newTask.addListener(this); // MigrationManager 作为监听器.
        newTask.addListener(reportListener); // 报告监听器

        /**
         * 1.启动正式的迁移任务前先准备好连接；<br>
         * 2.将任务加入到迁移Task列表中，这样前端的代理将正式生效； <br>
         * 3.提交给Executor，执行真正的迁移任务；
         */
        if (newTask.prepareTask()) {
            migrationTaskScheduler.addMigrationTask(newTask);
            executor.execute(newTask);

            if (logger.isDebugEnabled()) {
                logger.debug("Start new migration task." + newTask);
            }
        } else {
            throw new RuntimeException("Prepare task connection failed!");
        }

        retMsg = Message._MIGRATION_NEW_TASK;
        return retMsg;
    }

    /**
     * 取消迁移
     * 
     * @param migrationActionData
     * @return
     */
    public String cancelMigrate(MigrationActionData actionData) {
        controlLock.lock();
        try {
            String retMsg = null;
            MigrationTask migrationTask = migrationTaskScheduler.getTask(actionData);
            if (null != migrationTask) {
                migrationTask.cancel();
                retMsg = Message._MIGRATION_TASK_CANCEL;
            } else {
                retMsg = Message._MIGRATION_NO_TASK_TO_CANCEL;
            }
            return retMsg;
        } finally {
            controlLock.unlock();
        }
    }

    /**
     * cancel all ActiveTask
     * 
     * @param requestMigrationActionData
     * @return
     */
    private String cancelActiveTask(BaseMigrationTask task) {
        String retMsg = null;
        NodeMigrateStatus migrateStatus = task.getMigrateStatus();
        boolean result = migrationTaskScheduler.cancelTask(task);

        if (result) {
            retMsg = Message._MIGRATION_TASK_CANCEL + " " + task.getMigrateType();
            // 如果当前任务在等待删除数据，则通知放弃删除数据。
            if (migrateStatus == NodeMigrateStatus.MIGRATE_NODE_FINISHED) {
                task.dataCleanStart();
            }
        } else {
            retMsg = Message._MIGRATION_NO_TASK_TO_CANCEL;
        }

        return retMsg;
    }

    /**
     * 一次逻辑迁移全部涉及节点结束，AdminServer通知本节点。 将 status 改为 NORMAL 状态
     * 
     * @param migrationActionData
     */
    public String allFinishMigrate(MigrationActionData migrationActionData) {
        controlLock.lock();
        try {
            return allFinishMigrate0(migrationActionData);
        } finally {
            controlLock.unlock();
        }
    }

    /**
     * 收到迁移 All Finished 指令
     * 
     * @param migrationActionData
     * @return
     */
    private String allFinishMigrate0(MigrationActionData migrationActionData) {
        String retMsg = null;

        MigrationTask task = migrationTaskScheduler.getTask(migrationActionData);

        if (logger.isInfoEnabled()) {
            logger.info("Receive allFinishMigrate. Related Task: " + task);
            logger.info("Receive allFinishMigrate. Related Route: " + migrationActionData);
        }

        MigrateTypeEnum migrateType = migrationActionData.getSubcommand().getMigrateType();
        if (task == null && migrateType == MigrateTypeEnum.EXPANSION) {
            task = migrationTaskScheduler.getLastTask();

            logger.info("Related Task is null, get current lastTask instead:" + task + ", Route:"
                        + task.getMigrationActionData());
        }

        if (task != null && task.getMigrateStatus() == NodeMigrateStatus.MIGRATE_NODE_FINISHED) {
            // 完成后, 在内部开始数据清理.
            task.allFinish();
            retMsg = Message._MIGRATION_ALL_FINISHED + " " + task.getMigrateType();
        } else {
            retMsg = Message._MIGRATION_INVALID_ALL_FINISHED + " " + task.getMigrateType() + " currentStatus:"
                     + task.getMigrateStatus();
        }

        migrationActionData.setSuccess(true);

        return retMsg;
    }

    /**
     * 刷新路由
     * 
     * @return
     */
    public boolean refreshRouteTable() {
        // 强制刷新路由, 获取最新的路由
        boolean hasLatestRouteTable = false;
        try {
            long oldRouteTableVersion = routeTableConfiger.getConfigVersion();
            if (logger.isInfoEnabled()) {
                logger.info("Try to fetch lastest route config. oldRouteTableVersion: " + oldRouteTableVersion);
            }

            configManager.refreshConfig();

            long newRouteTableVersion = routeTableConfiger.getConfigVersion();

            if (newRouteTableVersion > oldRouteTableVersion) {
                hasLatestRouteTable = true;
                if (logger.isInfoEnabled()) {
                    if (newRouteTableVersion > oldRouteTableVersion) {
                        logger.info("Succeed to  fetch lastest route config.  newRouteTableVersion: "
                                    + newRouteTableVersion);
                    } else {
                        logger.info("Succeed to  fetch lastest route config. old version == new version:"
                                    + newRouteTableVersion);
                    }
                }
            } else {
                logger.warn("Migration all finish route refresh warning: old version:" + oldRouteTableVersion
                            + ",  new version:" + newRouteTableVersion);
            }
        } catch (Exception e) {
            logger.error("Fail to fetch lastest route config after migration all finish!  " + e, e);
        }

        return hasLatestRouteTable;
    }

    /**
     * dataClean
     * 
     * @param actionData
     * @return
     */
    public String dataClean(MigrationActionData actionData) {

        MigrationTask activeTask = migrationTaskScheduler.getTask(actionData);
        if (activeTask != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Active dataClean task  already exists. Ignore this command. " + activeTask);
            }
        } else {
            BaseMigrationTask cleanTask = (BaseMigrationTask) taskFactory.createDataCleanTask(this, actionData);

            MigrationListener reportListener = new DefaultMigrationListener();
            reportListener.setMigrationManager(this);

            cleanTask.addListener(this); // MigrationManager 作为监听器.
            cleanTask.addListener(reportListener); // 报告监听器

            boolean hasLatestRouteTable = refreshRouteTable();

            if (logger.isInfoEnabled()) {
                logger.info("Before data clean start, refresh route table: . " + hasLatestRouteTable);
            }

            executor.execute(cleanTask);
            migrationTaskScheduler.addMigrationTask(cleanTask);
        }

        return Message._DATACLEAN_START;
    }

    /**
     * 通知迁移开始
     */
    public void completeAndClearMigration(MigrationActionData actionData) {

        MigrationTask task = migrationTaskScheduler.getTask(actionData);
        if (logger.isDebugEnabled()) {
            logger.debug("Complete migration task and try to clearMigrationStatus. Task key:" + task.getTaskKey());
        }

        try {
            if (task != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Set status to finish=true , taskKey=" + task.getTaskKey());
                }

                task.setFinish(true);
                reportMigrationComplete((BaseMigrationTask) task);
            }
        } finally {
            migrationTaskScheduler.removeTask(task);
        }
    }

    /**
     * 两阶段迁移完毕，报告信息.
     */
    protected void reportMigrationComplete(BaseMigrationTask task) {

        task.setMigrateStatus(NodeMigrateStatus.MIGRATE_ALL_FINISHED);

        MigrationEvent event = new MigrationEvent();
        event.setMigrateType(task.getMigrateType());
        event.setProgress(task.getProgress());
        event.setMigrateStatus(task.getMigrateStatus());
        event.setMigrateRoute(task.getMigrationActionData().getMigrationRoutePairs());
        event.setServerPort(port);
        event.setMigrationTask(task);
        event.setMessage("Overall Data Migration Complete. Node port: " + port);

        migrationReporter.report(event);

        task.setProgress(-1);
        task.setMigrateStatus(NodeMigrateStatus.NORMAL);
    }

    /**
     * 查询迁移状态
     * 
     * @param actionData
     * @return
     */
    public String queryStatus(MigrationActionData actionData) {
        controlLock.lock();

        try {
            migrationTaskScheduler.checkAndTerminateFinishedTask();

            String retMsg = null;
            if (actionData == null || actionData.getMigrationRoutePairs() == null) {
                // 如果没有传 actionData ， 则查询当前 DataServer 的整体状态，只要有一个task在执行，都认为是迁移状态
                MigrateStatusReport report = new MigrateStatusReport();

                if (migrationTaskScheduler.hasActiveTask()) {
                    MigrationTask lastTask = migrationTaskScheduler.getLastTask();

                    if (logger.isDebugEnabled()) {
                        logger.debug("Query Status: Last task: " + lastTask);
                    }

                    report.setMigrateType(lastTask.getMigrateType().toString());
                    report.setStatus(lastTask.getMigrateStatus().toString());
                    report.setProgress(lastTask.getProgress());
                    report.setStartTime(lastTask.getStartTime());
                    report.setElapseTime(System.currentTimeMillis() - lastTask.getStartTime());

                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Query Status: no active task: Status:" + NodeMigrateStatus.NORMAL);
                    }

                    report.setMigrateType(MigrateTypeEnum.NONE.toString());
                    report.setStatus(NodeMigrateStatus.NORMAL.toString());
                    report.setProgress(-1);
                }
                retMsg = "OK " + JSON.toJSONString(report);
                return retMsg;

            } else {
                MigrateStatusReport report = new MigrateStatusReport();

                MigrationTask task = migrationTaskScheduler.getTask(actionData);
                // 如果迁移任务线程存在,则取任务的状态.
                if (task != null) {
                    report.setMigrateType(task.getMigrateType().toString());
                    report.setStatus(task.getMigrateStatus().toString());
                    report.setProgress(task.getProgress());
                    report.setStartTime(task.getStartTime());
                    report.setElapseTime(System.currentTimeMillis() - task.getStartTime());
                } else {
                    report.setMigrateType(MigrateTypeEnum.NONE.toString());
                    report.setStatus(NodeMigrateStatus.NORMAL.toString());
                    report.setProgress(-1);
                }

                retMsg = "OK " + JSON.toJSONString(report);
                if (logger.isDebugEnabled()) {
                    logger.debug("Migration.manager Query migrate status. Result:" + retMsg);
                }

                actionData.setReturnMessage(retMsg);
                actionData.setSuccess(true);
                return retMsg;
            }
        } finally {
            controlLock.unlock();
        }
    }

    public void setMigrationReporter(MigrationReporter migrationReporter) {
        this.migrationReporter = migrationReporter;
    }

    public MigrationReporter getMigrationReporter() {
        return migrationReporter;
    }

    public ConnectionManager getMigrationConnectionManager() {
        return new MigrationConnectionManager();
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setVirtualRouter(VirtualRouter virtualRouter) {
        this.virtualRouter = virtualRouter;
    }

    public VirtualRouter getVirtualRouter() {
        return virtualRouter;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public Storage getStorage() {
        return storage;
    }

    public String getMigrationListenerName() {
        return this.getClass().getSimpleName();
    }

    public void onMigrationStart(MigrationEvent event) {
    }

    public void onMigraionProcessing(MigrationEvent event) {
    }

    public void onMigrationAllFinished(MigrationEvent event) {
    }

    public void onMigrationCancelled(MigrationEvent event) {
    }

    public void onMigrationNodeFinished(MigrationEvent event) {
    }

    public void onDataCleanStart(MigrationEvent event) {
    }

    public void onDataCleanProcessing(MigrationEvent event) {
    }

    public void onDataCleanError(MigrationEvent event) {
    }

    public void onDataCleanFinish(MigrationEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug(" onDataCleanFinish ");
        }
    }

    public void onExitMigrationTask(MigrationEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug(" onExitMigrationTask ");
        }
        migrationTaskScheduler.removeTask(event.getMigrationTask());
    }

    public void onMigrationFail(MigrationEvent event) {
    }

    public MigrationManager getMigrationManager() {
        return this;
    }

    public void setMigrationManager(MigrationManager migrationManager) {
        // do nothing.
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void setRouteTableConfiger(RouteTableConfiger routeTableConfiger) {
        this.routeTableConfiger = routeTableConfiger;
    }

}

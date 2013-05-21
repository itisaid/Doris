/**
 * 
 */
package com.alibaba.doris.dataserver.migrator;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.dataserver.migrator.action.MigrationActionData;
import com.alibaba.doris.dataserver.migrator.task.BaseMigrationTask;
import com.alibaba.doris.dataserver.migrator.task.MigrationTask;

/**
 * @author raymond
 */
public class MigrationTaskScheduler {

    private static final Logger                   logger        = LoggerFactory.getLogger(MigrationTaskScheduler.class);

    protected volatile Map<String, MigrationTask> activeTaskMap = new ConcurrentHashMap<String, MigrationTask>();
    protected volatile MigrationTask              lastTask;

    public void addMigrationTask(BaseMigrationTask migrationTask) {
        activeTaskMap.put(migrationTask.getTaskKey(), migrationTask);
        this.lastTask = migrationTask;
    }

    public MigrationTask getTask(String taskKey) {
        return activeTaskMap.get(taskKey);
    }

    public MigrationTask getTask(MigrationActionData actionData) {
        String taskKey = BaseMigrationTask.buildTaskKey(actionData);
        return activeTaskMap.get(taskKey);
    }

    public boolean hasActiveTask() {
        return activeTaskMap.size() > 0;
    }

    public MigrationTask getLastTask() {
        return lastTask;
    }

    public void removeTask(MigrationTask task) {
        activeTaskMap.remove(task.getTaskKey());
    }

    /**
     * checkAndTerminateActiveTask
     */
    public void checkAndTerminateFinishedTask() {
        Iterator<String> itor = activeTaskMap.keySet().iterator();
        while (itor.hasNext()) {
            String taskKey = itor.next();
            BaseMigrationTask activeTask = (BaseMigrationTask) activeTaskMap.get(taskKey);

            if (activeTask != null && activeTask.isFinished()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Clear last TERMINATED task thread. Set to null." + activeTask);
                }

                try {
                    activeTask.join();
                } catch (InterruptedException e) {
                    activeTask.interrupt();
                }

                removeTask(activeTask);
                activeTask = null;
            }
        }
    }

    public Map<String, MigrationTask> getActiveTaskMap() {
        return activeTaskMap;
    }

    /**
     * cancel all ActiveTask
     * 
     * @param requestMigrationActionData
     * @return
     */
    public boolean cancelAllActiveTask() {
        if (activeTaskMap.size() == 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("No active migration task to be cancelled. ");
            }

            return false;
        }

        Iterator<String> itor = activeTaskMap.keySet().iterator();
        while (itor.hasNext()) {
            String taskKey = itor.next();
            BaseMigrationTask activeTask = (BaseMigrationTask) activeTaskMap.get(taskKey);
            cancelTask(activeTask);
        }

        return true;
    }

    public boolean cancelTask(BaseMigrationTask task) {
        if (task != null) {
            task.cancel(); // cancel task

            if (task.isCancel()) {
                MigrationActionData actionData = task.getMigrationActionData();
                if (actionData != null) {
                    actionData.setSuccess(true);
                }

                // retMsg = Message._MIGRATION_TASK_CANCEL + " " + migrateType ;
                if (logger.isDebugEnabled()) {
                    logger.debug("Try to cancel active migration task. " + task);
                }

                task.interrupt();

                if (logger.isDebugEnabled()) {
                    logger.debug("Succeed to cancel active migration task successfully. " + task);
                }

                removeTask(task);
            }

            return true;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("No active migration task to be cancelled. ");
            }
            // retMsg = Message._MIGRATION_NO_TASK_TO_CANCEL;
            return false;
        }
    }

}

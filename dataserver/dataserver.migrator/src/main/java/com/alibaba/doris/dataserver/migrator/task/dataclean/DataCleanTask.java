/*
 * Copyright(C) 2010 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.dataserver.migrator.task.dataclean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.MigrateTypeEnum;
import com.alibaba.doris.common.migrate.NodeMigrateStatus;
import com.alibaba.doris.dataserver.migrator.MigrationManager;
import com.alibaba.doris.dataserver.migrator.task.BaseMigrationTask;

/**
 * @author Raymond He ( He Kun), raymond.he.kk@gmail.com
 * @since 1.0 2011-7-7
 */
public class DataCleanTask extends BaseMigrationTask {

    private static final Logger logger = LoggerFactory.getLogger(BaseMigrationTask.class);

    public DataCleanTask(MigrationManager migrationManager) {
        super("DataCleanTask", migrationManager);
    }

    public DataCleanTask(String taskName, MigrationManager migrationManager) {
        super(taskName, migrationManager);
    }

    public MigrateTypeEnum getMigrateType() {
        return MigrateTypeEnum.DATACLEAN;
    }

    @Override
    public void run() {
        // start0
        if (begin() == false) {
            return;
        }

        // do
        try {

            dataClean();

        } catch (Throwable t) {

            logger.error("DataClean Task Error:" + t, t);
            notifyDataCleanError(t.toString());

        } finally {
            notifyExitMigrationTask();
            if (logger.isInfoEnabled()) logger.info("DataClean Task Thread Finished, and exit.");
        }
    }

    @Override
    protected boolean begin() {
        controlLock.lock();
        try {
            // 两种情况允许开始数据清理，其他情况不允许
            if (migrateStatus == NodeMigrateStatus.NORMAL || migrateStatus == NodeMigrateStatus.DATACLEANING) {

                if (logger.isDebugEnabled()) logger.debug("DATACLEAN status check ok. ");

                migrateStatus = NodeMigrateStatus.DATACLEANING;
                progress = 0;
                startTime = System.currentTimeMillis();

                // Notify listener
                notifyDataCleaningStart();

                if (!establishProxyConnection()) {
                    return false;
                }
                return true;

            } else {
                // 其他情况不允许开始数据清理
                if (logger.isDebugEnabled()) logger.debug("DataServer status = " + migrateStatus
                                                          + ", DATACLEAN command is rejected! ");

                return false;
            }

        } finally {
            controlLock.unlock();
        }
    }

    @Override
    public void notifyMigrateProgress(int newProgress) {
        super.notifyDataCleanProcess(newProgress);
    }
}

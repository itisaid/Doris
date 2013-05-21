/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.dataserver.migrator.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.MigrateTypeEnum;
import com.alibaba.doris.dataserver.migrator.MigrationManager;

/**
 * ForeverFailoverMigrationTask
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-25
 */
public class ForeverFailoverMigrationTask extends BaseMigrationTask {

    private static final Logger logger = LoggerFactory.getLogger(ForeverFailoverMigrationTask.class);

    public ForeverFailoverMigrationTask(MigrationManager migrationManager) {
        super("ForeverFailoverMigrationTask", migrationManager);
    }

    public MigrateTypeEnum getMigrateType() {
        return MigrateTypeEnum.FOREVER_FAILOVER;
    }

    @Override
    public void dataClean() {
        if (logger.isInfoEnabled()) {
            logger.info("ForeverFailover migration needn't data clean on this node.");
            logger.info("ForeverFailover migration complete. ");
        }

        migrationManager.completeAndClearMigration(migrationActionData);
    }

    @Override
    protected void notifyDataCleanFinish() {
        super.notifyDataCleanFinish();
    }
}

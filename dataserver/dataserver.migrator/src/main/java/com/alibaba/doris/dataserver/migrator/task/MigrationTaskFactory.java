/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.dataserver.migrator.task;

import com.alibaba.doris.common.MigrateTypeEnum;
import com.alibaba.doris.common.migrate.MigrateSubCommand;
import com.alibaba.doris.dataserver.migrator.MigrationManager;
import com.alibaba.doris.dataserver.migrator.action.MigrationActionData;
import com.alibaba.doris.dataserver.migrator.task.dataclean.DataCleanTask;

/**
 * MigrationTaskFactory
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-25
 */
public class MigrationTaskFactory {

    private static MigrationTaskFactory factory = new MigrationTaskFactory();

    public static MigrationTaskFactory getInstance() {
        return factory;
    }

    /**
     * 创建一个迁移任务
     * 
     * @param migrateType
     * @return
     */
    public BaseMigrationTask createTask(MigrationManager migrationManager, MigrationActionData actionData) {
        BaseMigrationTask newMigrationTask = null;
        MigrateSubCommand subCommand = actionData.getSubcommand();
        MigrateTypeEnum migrateType = subCommand.getMigrateType();

        if (subCommand == MigrateSubCommand.TEMP_FAILOVER_START) {
            newMigrationTask = createTempFailoverMigrationTask(migrationManager);
        } else if (subCommand == MigrateSubCommand.FOREVER_FAILOVER_START) {
            newMigrationTask = createForeverFailoverMigrationTask(migrationManager);
        } else if (subCommand == MigrateSubCommand.EXPANSION_START) {
            newMigrationTask = createExpansionMigrationTask(migrationManager);
        } else if (subCommand == MigrateSubCommand.MOCK_MIGRATE_START) {
            newMigrationTask = createMockMigrateTask(migrationManager);
        } else {
            throw new IllegalArgumentException("Invalid migrate subcommand " + migrateType);
        }

        newMigrationTask.setMigrationActionData(actionData);
        newMigrationTask.setMigrateType(migrateType);

        return newMigrationTask;
    }

    public BaseMigrationTask createTempFailoverMigrationTask(MigrationManager migrationManager) {
        return new TempFailoverMigrationTask(migrationManager);
    }

    public BaseMigrationTask createForeverFailoverMigrationTask(MigrationManager migrationManager) {
        return new ForeverFailoverMigrationTask(migrationManager);
    }

    public BaseMigrationTask createExpansionMigrationTask(MigrationManager migrationManager) {
        return new ExpansionMigrationTask(migrationManager);
    }

    private BaseMigrationTask createMockMigrateTask(MigrationManager migrationManager) {
        return new MockMigrationTask(migrationManager);
    }

    /**
     * createDataCleanTask
     * 
     * @param migrationManager
     * @param actionData
     * @return
     */
    public MigrationTask createDataCleanTask(MigrationManager migrationManager, MigrationActionData actionData) {
        MigrateTypeEnum migrateType = actionData.getSubcommand().getMigrateType();

        BaseMigrationTask dataCleanTask = new DataCleanTask(migrationManager);
        dataCleanTask.setMigrationActionData(actionData);
        dataCleanTask.setMigrateType(migrateType);
        return dataCleanTask;
    }
}

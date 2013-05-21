/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.dataserver.migrator.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.migrate.MigrateSubCommand;
import com.alibaba.doris.dataserver.Module;
import com.alibaba.doris.dataserver.ModuleContext;
import com.alibaba.doris.dataserver.action.Action;
import com.alibaba.doris.dataserver.config.ModuleConstances;
import com.alibaba.doris.dataserver.core.Request;
import com.alibaba.doris.dataserver.core.Response;
import com.alibaba.doris.dataserver.migrator.MigrationManager;

/**
 * MigrationAction. 迁移指令执行器.
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-25
 */
public class MigrationAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(MigrationAction.class);

    public static class Message {

        public static final String UNKNOWN_MIGRATE_SUBCOMMAND = "UNKNOWN_MIGRATE_SUBCOMMAND";
        public static final String INVALID_MIGRATE_COMMAND    = "INVALID_MIGRATE_COMMAND";
        public static final String MIGRATE_FAILED             = "MIGRATE_FAILED";
        public static final String MIGRATE_CANCELLED          = "MIGRATE_CANCELLED";
    }

    private MigrationManager migrationManager;

    public void setMigrationManager(MigrationManager migrationManager) {
        this.migrationManager = migrationManager;
    }

    /**
     * 执行迁移
     */
    public void execute(Request request, Response response) {

        if (migrationManager == null) {
            Module module = request.getApplicationContext().getModuleByName(ModuleConstances.MIGRATION_MODULE);
            ModuleContext moduleContext = module.getModuleContext();

            migrationManager = (MigrationManager) moduleContext.getAttribute(MigrationManager._MigrationManager);
        }

        migrationManager.setPort(request.getServerPort());

        MigrationActionData actionData = (MigrationActionData) request.getActionData();

        if (logger.isInfoEnabled()) {
            logger.info("Receive command, Type:  " + actionData.getActionType() + "," + actionData.getSubcommand()
                        + ", DataServer port: " + request.getServerPort());
        }

        if (!checkSubCommand(actionData)) {
            if (logger.isDebugEnabled()) logger.debug("Invalid migrate subcommand " + actionData.getSubcommand());
            response.write(actionData);
            return;
        }

        String retMsg = null;

        if (actionData.isCancelCommand()) {
            retMsg = migrationManager.cancelMigrate(actionData);

        } else if (actionData.isAllFinishedCommand()) {
            retMsg = migrationManager.allFinishMigrate(actionData);

        } else if (actionData.isStartCommand()) {
            // start migration
            retMsg = migrationManager.startMigrate(actionData);

        } else if (actionData.isDataCleanCommand()) {
            retMsg = migrationManager.dataClean(actionData);

        } else {
            // Default , take as queryStatus.
            // if( actionData.isQueryStatus() )
            retMsg = migrationManager.queryStatus(actionData);
        }
        // else {
        // throw new IllegalArgumentException("Unkown migrate subcommand '" + actionData.getSubcommand() +"'.");
        // }

        // write response.
        actionData.setReturnMessage(retMsg);
        response.write(actionData);
    }

    /**
     * 检查子命令
     * 
     * @param actionData
     * @return
     */
    protected boolean checkSubCommand(MigrationActionData actionData) {
        MigrateSubCommand subCommand = actionData.getSubcommand();

        if (subCommand == null) {
            actionData.setErrorMessage(Message.UNKNOWN_MIGRATE_SUBCOMMAND + " " + actionData.getSubcommand());
            return false;
        }

        return true;
    }
}

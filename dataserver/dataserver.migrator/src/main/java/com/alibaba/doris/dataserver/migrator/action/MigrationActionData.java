/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.dataserver.migrator.action;

import java.util.List;

import com.alibaba.doris.common.migrate.MigrateSubCommand;
import com.alibaba.doris.common.route.MigrationRoutePair;
import com.alibaba.doris.dataserver.action.ActionType;
import com.alibaba.doris.dataserver.action.data.SupportBodyActionData;
import com.alibaba.doris.dataserver.migrator.MigrateActionType;
import com.alibaba.fastjson.JSON;

/**
 * MigrationActionData. 自由格式的ActionData, 例如给迁移指令使用
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-26
 */
public class MigrationActionData extends SupportBodyActionData {

    private static final String      MIGRATE_ERROR = "MIGRATE_ERROR";

    private ActionType               actionType;
    private MigrateSubCommand        subcommand;
    private short                    flag;
    private long                     timestamp;

    private String                   migrationRouteString;
    private List<MigrationRoutePair> migrationRoutePairs;
    private String                   returnMessage;
    private int                      hashkey;                        // convert migrationRouteString to hashKey to

    // avoid computing repeatly.
    public MigrationActionData() {
        actionType = MigrateActionType.MIGRATE;
    }

    /**
     * @see com.alibaba.doris.dataserver.action.data.ActionData#getActionType()
     */
    public ActionType getActionType() {
        return actionType;
    }

    public MigrateSubCommand getSubcommand() {
        return subcommand;
    }

    public void setSubcommand(String subcommand) {
        this.subcommand = MigrateSubCommand.enumValueOf(subcommand);
    }

    public String getMigrationRouteString() {
        return migrationRouteString;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public short getFlag() {
        return flag;
    }

    public void setFlag(short flag) {
        this.flag = flag;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMigrationRoute() {
        return migrationRouteString;
    }

    public void setMigrationRouteString(String migrationRouteString) {
        this.migrationRouteString = migrationRouteString;

        if (migrationRouteString != null && migrationRouteString.trim().length() > 0) {
            migrationRoutePairs = JSON.parseArray(migrationRouteString, MigrationRoutePair.class);
        }

        hashkey = this.migrationRouteString.hashCode();
    }

    public List<MigrationRoutePair> getMigrationRoutePairs() {
        return migrationRoutePairs;
    }

    public void setMigrationRoutePairs(List<MigrationRoutePair> migrationRoutePairs) {
        this.migrationRoutePairs = migrationRoutePairs;

        if (migrationRouteString == null) {
            migrationRouteString = JSON.toJSONString(migrationRoutePairs);
        }
    }

    public boolean isNeedReadBody() {
        return true;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.returnMessage = new StringBuilder(32).append(MIGRATE_ERROR).append(" ").append(errorMessage).toString();
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public boolean isStartCommand() {
        return subcommand != null
               && (subcommand == MigrateSubCommand.EXPANSION_START
                   || subcommand == MigrateSubCommand.FOREVER_FAILOVER_START || subcommand == MigrateSubCommand.TEMP_FAILOVER_START);
    }

    public boolean isAllFinishedCommand() {
        return subcommand != null
               && (subcommand == MigrateSubCommand.TEMP_FAILOVER_ALL_FINISHED
                   || subcommand == MigrateSubCommand.EXPANSION_ALL_FINISHED || subcommand == MigrateSubCommand.FOREVER_FAILOVER_ALL_FINISHED);
    }

    public boolean isCancelCommand() {
        return subcommand != null
               && (subcommand == MigrateSubCommand.EXPANSION_CANCEL
                   || subcommand == MigrateSubCommand.FOREVER_FAILOVER_CANCEL || subcommand == MigrateSubCommand.TEMP_FAILOVER_CANCEL);
    }

    public boolean isDataCleanCommand() {
        return subcommand != null && subcommand == MigrateSubCommand.DATACLEAN;
    }

    public boolean isQueryStatus() {
        return subcommand != null && subcommand == MigrateSubCommand.QUERY_STATUS;
    }

    @Override
    public String toString() {
        return "[MigrationCommand:" + subcommand + "," + migrationRouteString + "," + timestamp + "]";
    }

    public int getHashKey() {
        return this.hashkey;
    }

}

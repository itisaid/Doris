/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.dataserver.migrator.event;

import java.util.List;

import com.alibaba.doris.common.MigrateTypeEnum;
import com.alibaba.doris.common.migrate.NodeMigrateStatus;
import com.alibaba.doris.common.route.MigrationRoutePair;
import com.alibaba.doris.dataserver.migrator.task.MigrationTask;

/**
 * MigrationEvent
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-25
 */
public class MigrationEvent {

    private int                      serverPort;

    private MigrateTypeEnum          migrateType;
    private List<MigrationRoutePair> migrateRoutePairs;
    private NodeMigrateStatus        migrateStatus;
    private boolean                  failed;
    private MigrationTask            migrationTask;
    private int                      progress;
    private String                   message;

    private long                     time = System.currentTimeMillis();

    public MigrationEvent() {
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public MigrateTypeEnum getMigrateType() {
        return migrateType;
    }

    public void setMigrateType(MigrateTypeEnum migrateType) {
        this.migrateType = migrateType;
    }

    public NodeMigrateStatus getMigrateStatus() {
        return migrateStatus;
    }

    public void setMigrateStatus(NodeMigrateStatus migrateStatus) {
        this.migrateStatus = migrateStatus;
    }

    public MigrationTask getMigrationTask() {
        return migrationTask;
    }

    public void setMigrationTask(MigrationTask migrationTask) {
        this.migrationTask = migrationTask;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getTime() {
        return time;
    }

    public void setMigrateRoute(List<MigrationRoutePair> migrateRoutePairs) {
        this.migrateRoutePairs = migrateRoutePairs;
    }

    public List<MigrationRoutePair> getMigrateRoutePairs() {
        return migrateRoutePairs;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    @Override
    public String toString() {
        return String.format("[MigrationEvent:migrateType:%s,failed?%s,migrateStatus:%s,progress:%d,message:%s]",
                             migrateType, failed, migrateStatus, progress, message);
    }
}

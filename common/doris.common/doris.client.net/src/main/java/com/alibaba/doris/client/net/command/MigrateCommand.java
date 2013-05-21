/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client.net.command;

import com.alibaba.doris.client.net.OperationFuture;
import com.alibaba.doris.client.net.protocol.ProtocolParser;
import com.alibaba.doris.client.net.protocol.text.MigrateProtocolParser;

/**
 * MigrateCommand
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-30
 */
public class MigrateCommand extends BaseCommand<String> {

    public static final byte[]        MIGRATE        = { 'm', 'i', 'g', 'r', 'a', 't', 'e' };
    public static final byte[]        OK             = { 'O', 'K' };
    public static final byte[]        ERROR          = { 'E', 'R', 'R', 'O', 'R' };

    protected OperationFuture<String> future;

    private String                    subCommand;
    private short                     flag;
    private long                      timestamp;
    private int                       migrateRouteBytes;
    private String                    migrateRoute;

    private String                    result;

    private ProtocolParser            protocolParser = new MigrateProtocolParser();

    public MigrateCommand(String subCommand, String migrateRoute) {
        this.subCommand = subCommand;
        this.migrateRoute = migrateRoute;

        timestamp = System.currentTimeMillis();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public ProtocolParser getProtocolParser() {
        return protocolParser;
    }

    public String getSubCommand() {
        return subCommand;
    }

    public short getFlag() {
        return flag;
    }

    public void setFlag(short flag) {
        this.flag = flag;
    }

    public int getMigrateRouteBytes() {
        return migrateRouteBytes;
    }

    public String getMigrateRoute() {
        return migrateRoute;
    }

    public void setMigrateRoute(String migrateRoute) {
        this.migrateRoute = migrateRoute;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

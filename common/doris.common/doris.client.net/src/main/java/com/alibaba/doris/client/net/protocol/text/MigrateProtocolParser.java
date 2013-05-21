/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client.net.protocol.text;

import org.jboss.netty.buffer.ChannelBuffer;

import com.alibaba.doris.client.net.command.Command;
import com.alibaba.doris.client.net.command.MigrateCommand;
import com.alibaba.doris.common.data.util.ByteUtils;

/**
 * MigrateProtocolParser
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-30
 */
public class MigrateProtocolParser extends TextProtocolParser {

    /**
     * 处理返回数据包
     * 
     * @see com.alibaba.doris.client.net.protocol.ProtocolParser#decode(com.alibaba.doris.client.net.command.Command,
     * org.jboss.netty.buffer.ChannelBuffer)
     */
    public boolean decode(Command<?> command, ChannelBuffer buffer) {
        byte[] line = readLine(buffer);

        if (line == null) {
            return false;
        }
        if (line[0] == MigrateCommand.OK[0] && line[1] == MigrateCommand.OK[1]) {
            ((MigrateCommand) command).setSuccess(true);

            int start = 0;
            int len = line.length;

            if (line[line.length - 2] == '\r') {
                len -= 1;
            }
            if (line[line.length - 1] == '\n') len -= 1;

            String errorMessage = ByteUtils.byteToString(line, start, len);
            ((MigrateCommand) command).setResult(errorMessage);
        }
        return true;
    }

    /**
     * @see com.alibaba.doris.client.net.protocol.ProtocolParser#encode(com.alibaba.doris.client.net.command.Command,
     * org.jboss.netty.buffer.ChannelBuffer)
     */
    public void encode(Command<?> command, ChannelBuffer buffer) {

        MigrateCommand mcd = (MigrateCommand) command;

        byte[] subCmdBytes = ByteUtils.stringToByte(mcd.getSubCommand());
        byte[] flagBytes = ByteUtils.stringToByte(String.valueOf(mcd.getFlag()));
        byte[] timestampBytes = ByteUtils.stringToByte(String.valueOf(mcd.getTimestamp()));
        byte[] mtBytes = encodeMigrateRoute(mcd.getMigrateRoute());

        byte[] mtLenBytes = ByteUtils.stringToByte(String.valueOf(mtBytes.length));

        assemableCommandData(buffer, MigrateCommand.MIGRATE, subCmdBytes, flagBytes, timestampBytes, mtLenBytes);
        assemableCommandData(buffer, mtBytes);
    }

    private byte[] encodeMigrateRoute(String migrateRoute) {
        // 编码迁移路由表， 需要压缩
        byte[] mtRoute = ByteUtils.stringToByte(migrateRoute);

        // TODO: gzip
        byte[] gzipMtRoute = mtRoute;
        return gzipMtRoute;
    }

}

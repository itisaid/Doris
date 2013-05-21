package com.alibaba.doris.client.net.protocol.text;

import org.apache.commons.lang.StringUtils;
import org.jboss.netty.buffer.ChannelBuffer;

import com.alibaba.doris.client.net.NetException;
import com.alibaba.doris.client.net.command.Command;
import com.alibaba.doris.client.net.command.StatCommand;
import com.alibaba.doris.common.data.util.ByteUtils;

public class StatProtocolParser extends TextProtocolParser {

    public void encode(Command<?> commandData, ChannelBuffer buffer) {
        StatCommand statCommand = (StatCommand) commandData;

        byte[] viewTypeBytes = ByteUtils.stringToByte(StringUtils.defaultIfEmpty(statCommand.getViewType(), "default"));
        byte[] namespaceBytes = ByteUtils.stringToByte(String.valueOf(statCommand.getNamespace()));

        assemableCommandData(buffer, "stats".getBytes(), viewTypeBytes, namespaceBytes);
    }

    public boolean decode(Command<?> commandData, ChannelBuffer buffer) {
        byte[] line = readLine(buffer);

        if (line == null) {
            return false;
        }

        StatCommand statCommand = (StatCommand) commandData;
        if (statCommand.getResult() == null) {
            String result = ByteUtils.byteToString(line);
            statCommand.setResult(result);
            line = readLine(buffer);
            if (line == null) {
                return false;
            }
        }

        if (!checkIsEndFlag(line)) {
            throw new NetException("Invalid byte stream. Couldn't find the end flag for stats command. ");
        }

        return true;
    }

}

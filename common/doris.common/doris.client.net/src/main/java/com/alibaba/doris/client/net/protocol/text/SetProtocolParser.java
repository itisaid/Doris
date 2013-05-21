package com.alibaba.doris.client.net.protocol.text;

import org.jboss.netty.buffer.ChannelBuffer;

import com.alibaba.doris.client.net.NetException;
import com.alibaba.doris.client.net.command.Command;
import com.alibaba.doris.client.net.command.SetCommand;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.util.ByteUtils;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class SetProtocolParser extends TextProtocolParser {

    public boolean decode(Command<?> commandData, ChannelBuffer buffer) {
        byte[] line = readLine(buffer);
        if (line == null) {
            return false;
        }

        if (line[0] == STORED[0] && line[5] == STORED[5]) {
            // success;
            ((SetCommand) commandData).setResult(true);
            return true;
        } else if (line[0] == NOT_STORED[0] && line[5] == NOT_STORED[5]) {
            // success;
            ((SetCommand) commandData).setResult(false);
            ((SetCommand) commandData).setErrorMessage("NOT_STORED");
            return true;
        }

        generateErrorMessage((SetCommand) commandData, line);
        return true;
    }

    public void encode(Command<?> commandData, ChannelBuffer buffer) {
        SetCommand set = (SetCommand) commandData;
        Value value = set.getValue();
        Key key = set.getKey();
        byte[] cmdDataBytes = encodeValue(value);
        byte[] keyBytes = key.getPhysicalKeyBytes();
        byte[] vnodeBytes = ByteUtils.stringToByte(Integer.toString(key.getVNode(), 10));
        byte[] flagBytes = ByteUtils.stringToByte(Integer.toString(value.getFlag()));
        byte[] timestampBytes = ByteUtils.stringToByte(String.valueOf(value.getTimestamp()));
        byte[] dataLenBytes = ByteUtils.stringToByte(String.valueOf(cmdDataBytes.length));

        long routeVersion = key.getRouteVersion();
        byte[] routeVersionBytes = ByteUtils.stringToByte(String.valueOf(routeVersion));
        byte[] commandBytes = SET;
        if (set.isCas()) {
            commandBytes = CAS;
        }
        assemableCommandData(buffer, commandBytes, keyBytes, flagBytes, timestampBytes, dataLenBytes,
                             routeVersionBytes, vnodeBytes);

        assemableCommandData(buffer, cmdDataBytes);
    }

    protected byte[] encodeValue(Value value) {
        Object o = value.getValue();
        if (null != o) {
            if (o instanceof byte[]) {
                return (byte[]) o;
            } else if (o instanceof String) {
                return ByteUtils.stringToByte((String) o);
            }
        }

        byte[] valueBytes = value.getValueBytes();
        if (null != valueBytes) {
            return valueBytes;
        }

        throw new NetException("Failed to convert the value object into byte array.");
    }
}

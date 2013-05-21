package com.alibaba.doris.client.net.protocol.text;

import org.jboss.netty.buffer.ChannelBuffer;

import com.alibaba.doris.client.net.command.Command;
import com.alibaba.doris.client.net.command.DeleteCommand;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.util.ByteUtils;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DeleteProtocolParser extends TextProtocolParser {

    public boolean decode(Command<?> commandData, ChannelBuffer buffer) {
        byte[] line = readLine(buffer);
        if (line == null) {
            return false;
        }

        DeleteCommand deleteCommandData = (DeleteCommand) commandData;
        if (line[0] == DELETED[0] && line[6] == DELETED[6]) {
            // success;
            deleteCommandData.setSuccess(true);
            return true;
        } else if (line[0] == NOT_FOUND[0] && line[6] == NOT_FOUND[6]) {
            deleteCommandData.setSuccess(false);
            deleteCommandData.setErrorMessage("NOT_FOUND");
            return true;
        } else if (line[0] == DELETE_FAILED[0] && line[7] == DELETE_FAILED[7]) {
            deleteCommandData.setSuccess(false);
            deleteCommandData.setErrorMessage("DELETE_FAILED");
            return true;
        }

        deleteCommandData.setSuccess(false);
        generateErrorMessage(deleteCommandData, line);
        return true;
    }

    public void encode(Command<?> commandData, ChannelBuffer buffer) {
        DeleteCommand delete = (DeleteCommand) commandData;
        Key key = delete.getKey();
        if (null != key) {
            byte[] keyBytes = key.getPhysicalKeyBytes();
            long routeVersion = key.getRouteVersion();
            byte[] vnodeBytes = ByteUtils.stringToByte(Integer.toString(key.getVNode(), 10));
            byte[] routeVersionBytes = ByteUtils.stringToByte(String.valueOf(routeVersion));
            assemableCommandData(buffer, DELETE, keyBytes, routeVersionBytes, vnodeBytes);
        }
    }

}

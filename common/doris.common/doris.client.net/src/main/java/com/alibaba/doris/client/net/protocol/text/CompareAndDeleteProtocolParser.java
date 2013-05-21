package com.alibaba.doris.client.net.protocol.text;

import org.jboss.netty.buffer.ChannelBuffer;

import com.alibaba.doris.client.net.command.Command;
import com.alibaba.doris.client.net.command.CompareAndDeleteCommand;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.util.ByteUtils;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class CompareAndDeleteProtocolParser extends DeleteProtocolParser {

    @Override
    public void encode(Command<?> commandData, ChannelBuffer buffer) {
        CompareAndDeleteCommand delete = (CompareAndDeleteCommand) commandData;
        Key key = delete.getKey();
        if (null != key) {
            byte[] keyBytes = key.getPhysicalKeyBytes();
            byte[] timestampBytes = ByteUtils.stringToByte(String.valueOf(delete.getTimestamp()));
            long routeVersion = key.getRouteVersion();
            byte[] vnodeBytes = ByteUtils.stringToByte(Integer.toString(key.getVNode(), 10));
            byte[] routeVersionBytes = ByteUtils.stringToByte(String.valueOf(routeVersion));
            assemableCommandData(buffer, CAD, keyBytes, timestampBytes, routeVersionBytes, vnodeBytes);
        }
    }

}

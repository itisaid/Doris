package com.alibaba.doris.client.net.protocol;

import org.jboss.netty.buffer.ChannelBuffer;

import com.alibaba.doris.client.net.command.Command;

/**
 * 这里不得已暂时依赖了netty中的ChannelBuffer。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface ProtocolParser {

    public void encode(Command<?> commandData, ChannelBuffer buffer);

    public boolean decode(Command<?> commandData, ChannelBuffer buffer);
}

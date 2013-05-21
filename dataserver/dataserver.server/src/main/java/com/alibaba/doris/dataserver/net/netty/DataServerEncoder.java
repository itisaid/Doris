package com.alibaba.doris.dataserver.net.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.parser.ActionParser;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DataServerEncoder extends OneToOneEncoder {

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        ActionData actionData = (ActionData) msg;
        // We allocate memory just in the first request .
        if (null == buffer) {
            buffer = ChannelBuffers.dynamicBuffer(channel.getConfig().getBufferFactory());
        } else {
            buffer.clear();
        }

        ActionParser parser = actionData.getActionType().getParser();
        if (null != parser) {
            NettyByteBufferWrapper bufferWrapper = new NettyByteBufferWrapper(buffer);
            parser.writeHead(bufferWrapper, actionData);
            parser.writeBody(bufferWrapper, actionData);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("client:" + channel.getRemoteAddress() + " server:" + channel.getLocalAddress()
                         + "Server send back command:" + actionData);
        }
        return buffer;
    }

    private ChannelBuffer       buffer;

    private static final Logger logger = LoggerFactory.getLogger(DataServerEncoder.class);
}

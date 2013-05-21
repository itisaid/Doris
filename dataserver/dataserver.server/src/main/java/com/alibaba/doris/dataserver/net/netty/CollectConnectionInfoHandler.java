package com.alibaba.doris.dataserver.net.netty;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.dataserver.ApplicationContext;
import com.alibaba.doris.dataserver.config.ModuleConstances;
import com.alibaba.doris.dataserver.net.NetworkModule;

/**
 * 本handler为单例的handler。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
@Sharable
public class CollectConnectionInfoHandler extends SimpleChannelUpstreamHandler {

    public CollectConnectionInfoHandler(ApplicationContext appContext) {
        networkModule = (NetworkModule) appContext.getModuleByName(ModuleConstances.NETWORK_MODULE);
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        activeChannels.add(e.getChannel());
        if (logger.isDebugEnabled()) {
            logger.debug("Open new connection : " + e.getChannel().getRemoteAddress());
        }
        // 如果连接数过大，停止响应新的连接请求
        if (activeChannels.size() > networkModule.getMaxConnections()) {
            Channels.close(e.getChannel());
            logger.error("Too many connections has established.");
        }
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Close connection : " + e.getChannel().getRemoteAddress());
        }
        super.channelClosed(ctx, e);
    }

    public ChannelGroup getActiveChannels() {
        return activeChannels;
    }

    private NetworkModule       networkModule;
    private ChannelGroup        activeChannels = new DefaultChannelGroup();
    private static final Logger logger         = LoggerFactory.getLogger(CollectConnectionInfoHandler.class);
}

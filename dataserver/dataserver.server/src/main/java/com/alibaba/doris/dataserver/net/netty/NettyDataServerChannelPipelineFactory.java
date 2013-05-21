package com.alibaba.doris.dataserver.net.netty;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

import com.alibaba.doris.dataserver.ApplicationContext;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class NettyDataServerChannelPipelineFactory implements ChannelPipelineFactory {

    public NettyDataServerChannelPipelineFactory(ApplicationContext appContext) {
        this.appContext = appContext;
        collectInfoHandler = new CollectConnectionInfoHandler(appContext);
    }

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();
        pipeline.addLast("decoder", new DataServerDecoder());
        pipeline.addLast("encoder", new DataServerEncoder());
        pipeline.addLast("collectInfoHandler", collectInfoHandler);
        pipeline.addLast("handler", new DataServerHandler(appContext));
        return pipeline;
    }

    public CollectConnectionInfoHandler getCollectConnectionInfoHandler() {
        return this.collectInfoHandler;
    }

    private CollectConnectionInfoHandler collectInfoHandler;
    private ApplicationContext           appContext;
}

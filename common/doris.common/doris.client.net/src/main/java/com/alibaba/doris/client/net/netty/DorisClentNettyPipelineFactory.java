package com.alibaba.doris.client.net.netty;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DorisClentNettyPipelineFactory implements ChannelPipelineFactory {

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();
        pipeline.addLast("codec", new DorisClientEncodeAndDecodeHandler());
        pipeline.addLast("handler", new DorisClientHandler());
        return pipeline;
    }

}

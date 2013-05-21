package com.alibaba.doris.client.net;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.alibaba.doris.client.net.netty.DorisClentNettyPipelineFactory;
import com.alibaba.doris.client.net.netty.NettyConnectionImpl;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ConnectionFactory {

    private ConnectionFactory() {
        init();
    }

    public void releaseResources() {
        bootstrap.releaseExternalResources();
    }

    public static ConnectionFactory getInstance() {
        return factory;
    }

    public Connection getConnection(InetSocketAddress remoteAddress) {
        return new NettyConnectionImpl(bootstrap, remoteAddress);
    }

    public void init() {
        // initialize the connection factory, such as we need create connection pools here.
        // Configure the client.
        bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
                                                                          Executors.newCachedThreadPool()));

        // Set up the pipeline factory.
        bootstrap.setPipelineFactory(new DorisClentNettyPipelineFactory());
        bootstrap.setOption("connectTimeoutMillis", CONNECT_TIME_OUT_MILLIS);
        bootstrap.setOption("tcpNoDelay", true);
    }

    ClientBootstrap                  bootstrap;
    public static final int          CONNECT_TIME_OUT_MILLIS = 10000;
    private static ConnectionFactory factory                 = new ConnectionFactory();
}

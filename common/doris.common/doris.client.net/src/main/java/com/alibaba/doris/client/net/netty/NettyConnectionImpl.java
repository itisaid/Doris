package com.alibaba.doris.client.net.netty;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.Channels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.ConnectionMetaOperation;
import com.alibaba.doris.client.net.NetException;
import com.alibaba.doris.client.net.OperationFuture;
import com.alibaba.doris.client.net.command.CheckCommand.Type;
import com.alibaba.doris.client.net.command.result.CheckResult;
import com.alibaba.doris.client.net.exception.ClientConnectionException;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class NettyConnectionImpl implements Connection {

    public NettyConnectionImpl(ClientBootstrap bootstrap, InetSocketAddress address) {
        this.bootstrap = bootstrap;
        this.address = address;
    }

    /**
     * 关闭当前连接。
     */
    public void close() throws NetException {
        if (null != channel) {
            Channels.close(channel).awaitUninterruptibly(500);
            isOpenned.set(false);
            logger.error("close connection success:" + address);
        }
    }

    /**
     * 打开并建立一个实际的连接。
     */
    public void open() throws NetException {
        if (isOpenned.compareAndSet(false, true)) {
            long start = System.currentTimeMillis();
            ChannelFuture future = bootstrap.connect(address);

            future.awaitUninterruptibly(4000);
            if (future.isSuccess()) {
                channel = future.getChannel();
                operation = channel.getPipeline().get(DorisClientHandler.class);
                logger.info("open connection success:" + address + " time:" + (System.currentTimeMillis() - start));
            } else {
                ClientConnectionException exception = new ClientConnectionException(
                                                                                    "Connecting remote server failed. The Remote DataServer:"
                                                                                            + address.toString(),
                                                                                    future.getCause());
                exception.setRemoteServerAddress(address);
                isOpenned.set(false);
                logger.info("open connection failed:" + address + " time:" + (System.currentTimeMillis() - start));
                throw exception;
            }
        } else {
            throw new ClientConnectionException(
                                                "Connecting remote server failed. The connection has openned before. Ip address:"
                                                        + address.toString());
        }
    }

    public OperationFuture<Boolean> delete(Key key) {
        if (null == key) {
            throw new IllegalArgumentException("The input keys is null.");
        }
        checkConnection();
        return operation.delete(key);
    }

    public OperationFuture<Value> get(Key key) {
        checkConnection();
        return operation.get(key);
    }

    public OperationFuture<Map<Key, Value>> gets(Set<Key> keys) {
        if (null == keys) {
            throw new IllegalArgumentException("The input keys is null.");
        }
        if (keys.size() <= 0) {
            throw new IllegalArgumentException("Couldn't find any keys,the key can't be empty. ");
        }

        checkConnection();
        return operation.gets(keys);
    }

    public OperationFuture<Boolean> put(Key key, Value value) {
        if (null == key) {
            throw new IllegalArgumentException("The input key is null.");
        }

        if (null == value) {
            throw new IllegalArgumentException("The input value is null.");
        }

        checkConnection();
        return operation.put(key, value);
    }

    public OperationFuture<Boolean> puts(Map<Key, Value> map) {
        if (null == map) {
            throw new IllegalArgumentException("The input arguments of puts couldn't be null.");
        }

        if (map.size() <= 0) {
            throw new IllegalArgumentException("Couldn't store empty map into storage.");
        }

        checkConnection();
        return operation.puts(map);
    }

    public OperationFuture<String> migrate(String subcommand, String migrateRoute) {
        checkConnection();
        if (subcommand == null) {
            throw new IllegalArgumentException("Migrate subcommand can't be emtpy.");
        }

        if (migrateRoute == null) {
            throw new IllegalArgumentException("Migrate migrateRoute can't be emtpy.");
        }
        return operation.migrate(subcommand, migrateRoute);
    }

    public OperationFuture<String> stats(String viewType, int namespace) {
        checkConnection();
        return operation.stats(viewType, namespace);
    }

    public boolean isConnected() {
        if (null != channel) {
            return channel.isConnected();
        }
        return false;
    }

    public OperationFuture<CheckResult> check(Type checkType) {
        checkConnection();
        return operation.check(checkType);
    }

    public OperationFuture<Boolean> cad(Key key, Value value) {
        checkConnection();
        return operation.cad(key, value);
    }

    public OperationFuture<Boolean> cas(Key key, Value value) {
        checkConnection();
        return operation.cas(key, value);
    }

    private void checkConnection() {
        if (null == channel) {
            throw new NetException("Connection is not established. The Remote DataServer:" + address.toString());
        }
    }

    private ClientBootstrap         bootstrap;
    private Channel                 channel;
    private ConnectionMetaOperation operation;
    private InetSocketAddress       address;
    private AtomicBoolean           isOpenned = new AtomicBoolean(false);
    private static final Logger     logger    = LoggerFactory.getLogger(NettyConnectionImpl.class);
}

package com.alibaba.doris.dataserver.migrator.connection;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.ConnectionFactory;
import com.alibaba.doris.client.net.NetException;
import com.alibaba.doris.client.net.OperationFuture;
import com.alibaba.doris.client.net.command.CheckCommand.Type;
import com.alibaba.doris.client.net.command.result.CheckResult;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;

/**
 * @author helios
 */
public class ThreadLocalConnection implements Connection {

    private Map<String, Connection> connectionHold = new ConcurrentHashMap<String, Connection>();

    private InetSocketAddress       address;

    public ThreadLocalConnection(InetSocketAddress address) {
        this.address = address;

        getConnection();
    }

    public Connection getConnection() {
        Connection c = connectionHold.get(Thread.currentThread().toString());

        if (c == null || !c.isConnected()) {
            // Ignore double check problem
            synchronized (this) {
                c = connectionHold.get(Thread.currentThread().toString());
                if (c == null || !c.isConnected()) {
                    c = makeConnection(address);
                    connectionHold.put(Thread.currentThread().toString(), c);
                }
            }
        }

        return c;
    }

    public void close() {
        for (Connection c : connectionHold.values()) {
            if (c != null && c.isConnected()) {
                try {
                    c.close();
                } catch (Exception e) {
                    // do nothing
                }
            }
        }

        connectionHold.clear();
    }

    private Connection makeConnection(InetSocketAddress remoteAddress) {
        ConnectionFactory factory = ConnectionFactory.getInstance();
        Connection c = factory.getConnection(remoteAddress);
        // Here will throw an exception when this connection couldn't be established.
        c.open();
        return c;
    }

    public void open() throws NetException {
        getConnection().open();
    }

    public boolean isConnected() {
        return getConnection().isConnected();
    }

    public OperationFuture<Boolean> put(Key key, Value value) {
        return getConnection().put(key, value);
    }

    public OperationFuture<Boolean> cas(Key key, Value value) {
        return getConnection().cas(key, value);
    }

    public OperationFuture<Boolean> puts(Map<Key, Value> map) {
        return getConnection().puts(map);
    }

    public OperationFuture<Value> get(Key key) {
        return getConnection().get(key);
    }

    public OperationFuture<Map<Key, Value>> gets(Set<Key> keys) {
        return getConnection().gets(keys);
    }

    public OperationFuture<Boolean> delete(Key key) {
        return getConnection().delete(key);
    }

    public OperationFuture<Boolean> cad(Key key, Value value) {
        return getConnection().cad(key, value);
    }

    public OperationFuture<String> migrate(String subcommand, String migrateRoute) {
        return getConnection().migrate(subcommand, migrateRoute);
    }

    public OperationFuture<String> stats(String viewType, int namespace) {
        return getConnection().stats(viewType, namespace);
    }

    public OperationFuture<CheckResult> check(Type checkType) {
        return getConnection().check(checkType);
    }
}

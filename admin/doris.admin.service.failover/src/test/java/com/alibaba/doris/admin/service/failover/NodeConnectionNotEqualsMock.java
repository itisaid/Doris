package com.alibaba.doris.admin.service.failover;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.NetException;
import com.alibaba.doris.client.net.OperationFuture;
import com.alibaba.doris.client.net.command.CheckCommand.Type;
import com.alibaba.doris.client.net.command.result.CheckResult;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.ValueImpl;

public class NodeConnectionNotEqualsMock implements Connection {

    private Map<Object, Object> storeMock = new HashMap<Object, Object>();

    String                      pid;

    NodeConnectionNotEqualsMock(String pId) {
        this.pid = pId;
        storeMock.put("dorisHeartBeatKey", "dorisHeartBeatValue");
    }

    public OperationFuture<Boolean> put(Key key, Value value) {
        storeMock.put(key.getKey(), value.getValue());
        return new OperationFuture<Boolean>() {

            public boolean cancel(boolean mayInterruptIfRunning) {
                // TODO Auto-generated method stub
                return false;
            }

            public boolean isCancelled() {
                // TODO Auto-generated method stub
                return false;
            }

            public boolean isDone() {
                // TODO Auto-generated method stub
                return false;
            }

            public Boolean get() throws InterruptedException, ExecutionException {
                // TODO Auto-generated method stub
                return Boolean.TRUE;
            }

            public Boolean get(long timeout, TimeUnit unit) throws InterruptedException,
                    ExecutionException, TimeoutException {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

    public OperationFuture<Boolean> puts(Map<Key, Value> map) {
        // TODO Auto-generated method stub
        return null;
    }

    public OperationFuture<Value> get(final Key key) {
        return new OperationFuture<Value>() {

            public boolean cancel(boolean mayInterruptIfRunning) {
                // TODO Auto-generated method stub
                return false;
            }

            public boolean isCancelled() {
                // TODO Auto-generated method stub
                return false;
            }

            public boolean isDone() {
                // TODO Auto-generated method stub
                return true;
            }

            public Value get() throws InterruptedException, ExecutionException {
                return new ValueImpl("dorisHeartBeatValue1");
            }

            public Value get(long timeout, TimeUnit unit) throws InterruptedException,
                    ExecutionException, TimeoutException {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

    public OperationFuture<Map<Key, Value>> gets(Set<Key> keys) {
        return null;
    }

    public OperationFuture<Boolean> delete(Key key) {
        return null;
    }

    public void open() throws NetException {
        System.out.println("open connection for pid : " + pid);

    }

    public void close() throws NetException {
        System.out.println("close connection for pid : " + pid);
    }

    public OperationFuture<String> migrate(String subcommand, String migrateRoute) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isConnected() {
        // TODO Auto-generated method stub
        return false;
    }

    public OperationFuture<String> stats(String viewType, int namespace) {
        // TODO Auto-generated method stub
        return null;
    }

    public OperationFuture<CheckResult> check() {
        // TODO Auto-generated method stub
        return null;
    }

    public OperationFuture<Boolean> putCas(Key key, Value value) {
        // TODO Auto-generated method stub
        return null;
    }

    public OperationFuture<Boolean> deleteCas(Key key) {
        // TODO Auto-generated method stub
        return null;
    }

    public OperationFuture<Boolean> cas(Key key, Value value) {
        // TODO Auto-generated method stub
        return null;
    }

    public OperationFuture<Boolean> cad(Key key, Value value) {
        // TODO Auto-generated method stub
        return null;
    }

    public OperationFuture<CheckResult> check(Type checkType) {
        // TODO Auto-generated method stub
        return null;
    }
}

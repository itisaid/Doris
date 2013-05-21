/*
 * Copyright(C) 1999-2010 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.dataserver.migrator.connection;

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

/**
 * LocalConnection
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-22
 */
public class MockKvConnection implements Connection {

    private String  address;

    Map<Key, Value> storage = new HashMap<Key, Value>();

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public OperationFuture<Value> get(Key key) {
        validateKey(key);

        Value v = (Value) storage.get(key);
        if (v == null) {
            v = new ValueImpl(null);
        }

        final Value returnValue = v;
        return new OperationFuture<Value>() {

            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            public Value get() throws InterruptedException, ExecutionException {
                return returnValue;
            }

            public Value get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
                                                         TimeoutException {
                return returnValue;
            }

            public boolean isCancelled() {
                return false;
            }

            public boolean isDone() {
                return false;
            }

        };
    }

    public OperationFuture<Boolean> put(Key key, Value value) {

        validateKey(key);
        storage.put(key, value);
        return null;
    }

    public OperationFuture<Boolean> delete(Key key) {

        validateKey(key);

        storage.remove(key);
        return null;
    }

    @SuppressWarnings("unchecked")
    public OperationFuture<Map<Key, Value>> gets(Set<Key> keys) {
        final Map<Key, Value> map = storage;
        return new OperationFuture<Map<Key, Value>>() {

            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            public Map<Key, Value> get() throws InterruptedException, ExecutionException {
                return map;
            }

            public Map<Key, Value> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
                                                                   TimeoutException {
                return map;
            }

            public boolean isCancelled() {
                return false;
            }

            public boolean isDone() {
                return false;
            }

        };
    }

    public OperationFuture<Boolean> puts(Map<Key, Value> map) {
        storage.putAll(map);
        return null;
    }

    public OperationFuture<String> migrate(String subcommand, String migrateRoute) {
        return null;
    }

    /**
     * @param key
     */
    private void validateKey(Key key) {
        if (key.getNamespace() == 0) {
            throw new IllegalArgumentException("Namespace is missing of KeyImpl object:" + key.getKey());
        }
    }

    public Map<Key, Value> getStorage() {
        return storage;
    }

    public void open() throws NetException {
    }

    public void close() throws NetException {
    }

    public boolean isConnected() {
        return true;
    }

    public OperationFuture<String> stats(String viewType, int namespace) {
        return null;
    }

    public OperationFuture<Boolean> deleteCas(Key key) {
        return null;
    }

    public OperationFuture<Boolean> putCas(Key key, Value value) {
        return null;
    }

    public OperationFuture<Boolean> deleteCas(Key key, Value value) {
        return null;
    }

    public OperationFuture<Boolean> cas(Key key, Value value) {
        return null;
    }

    public OperationFuture<Boolean> cad(Key key, Value value) {
        return null;
    }

    public OperationFuture<CheckResult> check(Type checkType) {
        return null;
    }

}

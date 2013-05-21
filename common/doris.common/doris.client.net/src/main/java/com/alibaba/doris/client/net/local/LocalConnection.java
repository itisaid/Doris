/*
 * Copyright(C) 1999-2010 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client.net.local;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
public class LocalConnection implements Connection {

    Map<Key, Value> storage = new HashMap<Key, Value>();

    public OperationFuture<Value> get(Key key) {
        validateKey(key);

        Value v = (Value) storage.get(key);

        if (v == null) {
            v = new ValueImpl(null, System.currentTimeMillis());
        }

        final Value returnValue = v;

        return new LocalOperationFuture<Value>(returnValue);
    }

    public OperationFuture<Boolean> put(Key key, Value value) {

        validateKey(key);
        storage.put(key, value);

        OperationFuture<Boolean> future = new LocalOperationFuture<Boolean>(true);
        return future;
    }

    /**
     * 
     */
    public OperationFuture<Boolean> delete(Key key) {

        validateKey(key);
        storage.remove(key);

        OperationFuture<Boolean> future = new LocalOperationFuture<Boolean>(true);
        return future;
    }

    public OperationFuture<Map<Key, Value>> gets(Set<Key> keys) {
        final Map<Key, Value> map = storage;
        OperationFuture<Map<Key, Value>> future = new LocalOperationFuture<Map<Key, Value>>(map);
        return future;
    }

    public OperationFuture<Boolean> puts(Map<Key, Value> map) {
        storage.putAll(map);
        OperationFuture<Boolean> future = new LocalOperationFuture<Boolean>(true);
        return future;
    }

    public OperationFuture<String> migrate(String subcommand, String migrateRoute) {
        String result = "OK " + subcommand;
        OperationFuture<String> future = new LocalOperationFuture<String>(result);
        return future;
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

    public OperationFuture<String> stats(String viewType, int namespace) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isConnected() {
        return true;
    }

    public OperationFuture<CheckResult> check(Type checkType) {
        CheckResult checkResult = new CheckResult() {

            public boolean isSuccess() {
                return true;
            }

            public String getMessage() {
                return "OK";
            }
        };
        OperationFuture<CheckResult> future = new LocalOperationFuture<CheckResult>(checkResult);
        return future;
    }

    public void setStorage(Map<Key, Value> storage) {
        this.storage = storage;
    }

    public OperationFuture<Boolean> cas(Key key, Value value) {
        OperationFuture<Boolean> future = new LocalOperationFuture<Boolean>(true);
        return future;
    }

    public OperationFuture<Boolean> cad(Key key, Value value) {
        OperationFuture<Boolean> future = new LocalOperationFuture<Boolean>(true);
        return future;
    }
}

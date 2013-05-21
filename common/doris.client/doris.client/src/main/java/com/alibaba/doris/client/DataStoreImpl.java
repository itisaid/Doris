/*
 * Copyright(C) 1999-2010 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.doris.client.operation.Operation;
import com.alibaba.doris.client.operation.OperationData;
import com.alibaba.doris.client.validate.KeyValidator;
import com.alibaba.doris.client.validate.Validator;
import com.alibaba.doris.common.Namespace;
import com.alibaba.doris.common.data.Value;

/**
 * DataStoreImpl
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-21
 */
public class DataStoreImpl implements DataStore {

    public static final int  MAX_NAMESPACE_LEN = 64;

    private Namespace        namespace;
    private DataStoreFactory dataStoreFactory;

    private Validator        keyValidator      = new KeyValidator();

    public DataStoreImpl(Namespace namespace) {
        this.namespace = namespace;

        validateNamespace();
    }

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public void setDataStoreFactory(DataStoreFactory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
    }

    public DataStoreFactory getDataStoreFactory() {
        return dataStoreFactory;
    }

    protected void validateNamespace() {
        if (namespace.getName().length() > MAX_NAMESPACE_LEN) {
            throw new IllegalArgumentException("Namespace name is too long! MAX LEN is " + MAX_NAMESPACE_LEN
                                               + ", actual is " + namespace);
        } else {

            String nsId = String.valueOf(namespace.getId());

            if (nsId.trim().length() > MAX_NAMESPACE_LEN) {
                throw new IllegalArgumentException("Invalid namespace id, MAX LEN is " + MAX_NAMESPACE_LEN
                                                   + ", actual is  '" + namespace.getId());
            }
        }
    }

    /**
     * put.
     */
    public boolean put(Object key, Object value) throws DorisClientException {
        keyValidator.validate(key);

        Operation operation = getOperation("put");

        List<Object> args = new ArrayList<Object>();
        args.add(key);
        args.add(value);

        OperationData operationData = new OperationData(operation, namespace, args);
        operation.execute(operationData);

        if (operationData.getResult() != null) return ((Boolean) operationData.getResult()).booleanValue();
        else return false;
    }

    /**
     * puts
     */
    public void puts(Map<? extends Object, ? extends Object> map) throws DorisClientException {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException("The input map can't be empty!");
        }

        if (map.size() == 1) {
            Entry<? extends Object, ? extends Object> entry = map.entrySet().iterator().next();
            put(entry.getKey(), entry.getValue());
            return;
        }

        Map<Object, Object> inputMap = new HashMap<Object, Object>();
        for (Entry<? extends Object, ? extends Object> e : map.entrySet()) {
            keyValidator.validate(e.getKey());
            inputMap.put(e.getKey(), e.getValue());
        }

        Operation operation = getOperation("puts");
        List<Object> args = new ArrayList<Object>(1);
        args.add(inputMap);

        OperationData operationData = new OperationData(operation, namespace, args);
        operation.execute(operationData);
    }

    public Object get(Object key) throws DorisClientException {
        if (key == null) {
            throw new IllegalArgumentException("key musn't be null!");
        }
        Operation operation = getOperation("get");

        List<Object> args = new ArrayList<Object>(1);
        args.add(key);
        OperationData operationData = new OperationData(operation, namespace, args);
        operation.execute(operationData);
        return operationData.getResult();
    }

    public Map<Object, Object> getMap(List<? extends Object> keys) throws DorisClientException {
        if (keys == null || keys.isEmpty()) {
            throw new IllegalArgumentException("The input keys can't be empty!");
        }

        List<Object> values = gets(keys);
        Map<Object, Object> map = new HashMap<Object, Object>();
        int size = keys.size();
        for (int i = 0; i < size; i++) {
            map.put(keys.get(i), values.get(i));
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public List<Object> gets(List<? extends Object> keys) throws DorisClientException {
        if (keys == null || keys.isEmpty()) {
            throw new IllegalArgumentException("The input keys can't be empty!");
        }

        if (keys.size() == 1) {
            List<Object> l = new ArrayList<Object>();
            l.add(get(keys.get(0)));
            return l;
        }
        for (Object o : keys) {
            keyValidator.validate(o);
        }
        Operation operation = getOperation("gets");

        List<Object> args = new ArrayList<Object>(1);
        args.add(keys);

        OperationData operationData = new OperationData(operation, namespace, args);
        operation.execute(operationData);
        List<Value> valueList = (List<Value>) operationData.getResult();
        List<Object> resultList = new ArrayList();
        for (Value value : valueList) {
            resultList.add(value.getValue());
        }
        if (resultList == null || resultList.isEmpty() || resultList.size() != keys.size()) {
            throw new DorisClientException("Unknow fatal exception.");
        }
        return resultList;
    }

    public boolean delete(Object key) throws DorisClientException {
        if (key == null) {
            throw new IllegalArgumentException("key musn't be null!");
        }

        Operation operation = getOperation("delete");

        List<Object> args = new ArrayList<Object>(1);
        args.add(key);
        OperationData operationData = new OperationData(operation, namespace, args);
        operation.execute(operationData);
        if (operationData.getResult() != null) {
            return ((Boolean) operationData.getResult()).booleanValue();
        } else {
            return false;
        }
    }

    private Operation getOperation(String opName) {
        Operation operation = dataStoreFactory.getOperationFactory().getOperation(opName);
        operation.setDataSourceManager(dataStoreFactory.getDataSourceManager());
        operation.setFailoverHandlerFactory(dataStoreFactory.getCallbackHandlerFactory());
        return operation;
    }

    public byte[] getBytes(Object key) throws DorisClientException {
        Object c = this.get(key);
        byte[] o = (byte[]) c;
        return o;
    }

    public boolean putBytes(Object key, byte[] value) throws DorisClientException {
        keyValidator.validate(key);
        return this.put(key, value);
    }
}

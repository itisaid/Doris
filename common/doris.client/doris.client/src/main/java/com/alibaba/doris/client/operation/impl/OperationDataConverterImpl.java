/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client.operation.impl;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.doris.client.cn.OperationDataConverter;
import com.alibaba.doris.client.operation.OperationData;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.KeyFactory;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.common.route.VirtualRouter;

/**
 * OperationDataConverterImpl
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-5
 */
public class OperationDataConverterImpl implements OperationDataConverter {

    private VirtualRouter virtualRouter;

    /**
     * buildKey
     * 
     * @see com.alibaba.doris.client.cn.OperationDataConverter#buildKey(com.alibaba.doris.client.operation.OperationData)
     */
    public Key buildKey(OperationData operationData, long routeVersion) {
        validNamespace(operationData);

        Object appKey = operationData.getArgs().get(0);
        String appKeyStr = String.valueOf(appKey);
        Key key = KeyFactory.createKey(operationData.getNamespace().getId(), appKeyStr, routeVersion, Key.DEFAULT_VNODE);

        int vnode = virtualRouter.findVirtualNode(key.getPhysicalKey());
        key.setVNode(vnode);
        return key;
    }

    /**
     * 验证 Namespace.
     * 
     * @param operationData
     */
    private void validNamespace(OperationData operationData) {
        if (operationData.getNamespace() == null || operationData.getNamespace().getName().trim().length() == 0
            || operationData.getNamespace().getId() == 0) {
            Object appKey = operationData.getArgs().get(0);
            throw new IllegalArgumentException("Namespace is missing of operationData: key=" + appKey);
        }
    }

    /*
     * @see
     * com.alibaba.doris.client.cn.OperationDataConverter#buildValue(com.alibaba.doris.client.operation.OperationData)
     */
    public Value buildValue(OperationData operationData) {
        validNamespace(operationData);
        byte[] appValue = (byte[]) operationData.getArgs().get(1);
        Value value = new ValueImpl(appValue, System.currentTimeMillis());
        return value;
    }

    /**
     * @see com.alibaba.doris.client.cn.OperationDataConverter#unbuildKey(com.alibaba.doris.client.operation.OperationData)
     */
    public Object unbuildKey(OperationData operationData) {
        validNamespace(operationData);
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * @see
     * com.alibaba.doris.client.cn.OperationDataConverter#unbuildValue(com.alibaba.doris.client.operation.OperationData)
     */
    public Object unbuildValue(Value value) {
        return value.getValueBytes();
    }

    public Key buildKeys(OperationData operationData, long routeVersion) {
        validNamespace(operationData);
        // TODO Auto-generated method stub
        return null;
    }

    public Value buildValues(OperationData operationData) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object unbuildKeys(OperationData operationData) {
        validNamespace(operationData);
        // TODO Auto-generated method stub
        return null;
    }

    public Object unbuildValues(List<Value> values) {

        List<Object> objList = new ArrayList<Object>(values.size());
        for (Value v : values) {
            objList.add(v.getValue());
        }
        return objList;
    }

    public VirtualRouter getVirtualRouter() {
        return virtualRouter;
    }

    public void setVirtualRouter(VirtualRouter virtualRouter) {
        this.virtualRouter = virtualRouter;
    }
}

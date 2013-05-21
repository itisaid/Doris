/*
 * Copyright(C) 1999-2010 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client.interceptor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.client.operation.OperationData;
import com.alibaba.doris.common.Namespace;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.serialize.Serializer;
import com.alibaba.doris.dproxy.AbstractInterceptor;
import com.alibaba.doris.dproxy.InvokeInfo;

/**
 * @author frank
 */
public class ListDeserializeInterceptor extends AbstractInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ListDeserializeInterceptor.class);

    public void after(InvokeInfo info) throws Throwable {
        OperationData operationData = (OperationData) info.getArgs()[0];
        Object result = operationData.getResult();
        if (result == null) {
            return;
        }

        if (!(result instanceof List)) {
            throw new IllegalArgumentException("Gotten value to be deserialized must be Value type.");
        }

        Namespace namespace = operationData.getNamespace();

        String serializeModeClass = namespace.getSerializeMode();
        String deserializeTargetClassString = namespace.getClassName();
        Serializer serializer = null;

        if (serializeModeClass != null && serializeModeClass.trim().length() > 0) {
            SerializerManager serializerManager = SerializerManager.getInstance();
            serializer = serializerManager.getSerializer(serializeModeClass);

            if (serializer == null) {
                throw new IllegalArgumentException("Unkown  serialize mode or  serialize mode class "
                                                   + serializeModeClass);
            }
            if (logger.isDebugEnabled()) {
                logger.debug(namespace.getName() + ", Serializer " + serializer + ".  DeserializeTarget: "
                             + deserializeTargetClassString);
            }
        } else {
            throw new IllegalArgumentException("Unset  serialize mode.  Namespace: " + namespace.getName());
        }

        List<Object> resultList = (List<Object>) result;
        for (Object o : resultList) {
            if (!(o instanceof Value)) {
                throw new IllegalArgumentException("Gotten value to be deserialized must be Value type.");
            }

            Value value = (Value) o;
            byte[] bytes = (byte[]) value.getValueBytes();

            if (bytes == null) {
                value.setValue(null);
                continue;// Should we use break or continue here?
            }

            Object deValue = serializer.deserialize(bytes, deserializeTargetClassString);
            value.setValue(deValue);
        }
    }
}

package com.alibaba.doris.client.interceptor;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.doris.client.operation.OperationData;
import com.alibaba.doris.common.Namespace;
import com.alibaba.doris.common.serialize.Serializer;
import com.alibaba.doris.dproxy.AbstractInterceptor;
import com.alibaba.doris.dproxy.InvokeInfo;

/**
 * @author frank
 */
public class MapSerializeInterceptor extends AbstractInterceptor {

    public void before(InvokeInfo info) throws Throwable {
        OperationData operationData = (OperationData) info.getArgs()[0];

        List<Object> args = operationData.getArgs();

        Map<Object, Object> map = (Map<Object, Object>) args.get(0);
        Namespace namespace = operationData.getNamespace();
        String serializeModeClass = namespace.getSerializeMode();
        String deserializeTargetClassString = namespace.getClassName();

        Serializer serializer = null;
        if (serializeModeClass != null && serializeModeClass.trim().length() > 0) {
            SerializerManager serializerManager = SerializerManager.getInstance();
            serializer = serializerManager.getSerializer(serializeModeClass);
        } else {
            throw new IllegalArgumentException("Unknown selialize mode or selializer.");
        }

        for (Entry<Object, Object> e : map.entrySet()) {
            byte[] serializedValue = null;
            serializedValue = serializer.serialize(e.getValue(), deserializeTargetClassString);
            map.put(e.getKey(), serializedValue);
        }
    }
}

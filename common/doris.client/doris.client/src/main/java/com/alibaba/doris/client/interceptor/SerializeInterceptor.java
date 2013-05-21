package com.alibaba.doris.client.interceptor;

import java.util.List;

import com.alibaba.doris.client.operation.OperationData;
import com.alibaba.doris.common.Namespace;
import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.common.serialize.JsonSerializer;
import com.alibaba.doris.common.serialize.Serializer;
import com.alibaba.doris.common.serialize.StringSerializer;
import com.alibaba.doris.dproxy.AbstractInterceptor;
import com.alibaba.doris.dproxy.InvokeInfo;

/**
 * e
 * SerializeInterceptor
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-29
 */
public class SerializeInterceptor extends AbstractInterceptor {
	
	public void before(InvokeInfo info) throws Throwable {
		OperationData operationData = (OperationData) info.getArgs()[0];
		
		List<Object> args =  operationData.getArgs();
		
		Object value = args.get(1);
		
		Namespace namespace = operationData.getNamespace();		
		String serializeModeClass = namespace.getSerializeMode(); 		
		String deserializeTargetClassString = namespace.getClassName();
		
		byte[] serializedValue = null;
		if( serializeModeClass != null && serializeModeClass.trim().length() > 0 ) {
			
			SerializerManager serializerManager = SerializerManager.getInstance();			
			Serializer serializer =  serializerManager.getSerializer( serializeModeClass );			
			serializedValue = serializer.serialize( value, deserializeTargetClassString);
		}else  {
			throw new IllegalArgumentException("Unknown selialize mode or selializer.");
		}
		args.set(1,serializedValue);
	}
}

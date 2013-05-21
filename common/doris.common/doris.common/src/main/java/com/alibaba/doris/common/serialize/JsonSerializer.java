/*
Copyright(C) 2010-2011 Alibaba Group Holding Limited
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.alibaba.doris.common.serialize;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 * JsonSerializer
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-6-7
 */
public class JsonSerializer implements Serializer {
	
	private static final Logger logger = LoggerFactory.getLogger( JsonSerializer.class );
	
	final private Map<String,Class<?>> targetClassMap = new HashMap<String, Class<?>>();
	
	private Serializer stringSerializer = new StringSerializer();
	
	/**
	 * Serialize.
	 */
	public  byte[] serialize(Object o, Object arg) {
		String svalue =  JSON.toJSONString( o );
		
		return stringSerializer.serialize( svalue, null);
	}
	
	/**
	 * Deserialize
	 */
	public Object deserialize(byte[] data, Object deSerializeTarget) {
		
		String stringResult =(String) stringSerializer.deserialize(data , null);
		
		String deserializeTargetClassString = (String) deSerializeTarget;
		Class<?> deserializeTargetClass = null;
		
		if( deserializeTargetClassString != null && deserializeTargetClassString.trim().length() > 0 ) {
			
			deserializeTargetClass = targetClassMap.get( deserializeTargetClassString );
			if( deserializeTargetClass == null ) {
				 try {
					deserializeTargetClass = Thread.currentThread().getContextClassLoader().loadClass( deserializeTargetClassString );
					
					targetClassMap.put(deserializeTargetClassString , deserializeTargetClass);
				} catch (ClassNotFoundException e) {
					logger.error("Fail to load deserializeTargetClass "  + deserializeTargetClassString );
				}
			}
			
			if( deserializeTargetClass == null ) {
				throw new IllegalArgumentException("DeserializeTargetClass can't be null for JsonSerializer.");
			}
			if( logger.isDebugEnabled()) {
				logger.debug("JsonSerializer.  DeserializeTargetClass:" + deserializeTargetClass);
			}
			
			Object deValue =  null;
			if( stringResult.startsWith("[")) {
				deValue =JSON.parseArray( stringResult , deserializeTargetClass); 
			}else {
				deValue = JSON.parseObject( stringResult ,deserializeTargetClass );
			}
			return deValue;
		}else {			
			throw new IllegalArgumentException("Fail to deserialize. Unknown deserializeTargetClass ");
		}
	}
}

/*
Copyright(C) 2010 Alibaba Group Holding Limited
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
package com.alibaba.doris.client.interceptor;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.doris.common.serialize.Serializer;

/**
 * @author Raymond He ( He Kun), raymond.he.kk@gmail.com
 * @since 1.0
 * 2011-7-4
 */
public class SerializerManager {
	
	static final private SerializerManager manager = new SerializerManager();
	
	final private Map<String,Serializer> serializerMap = new HashMap<String, Serializer>();
	
	public static SerializerManager getInstance() {
		return manager;
	}
	
	/**
	 * 获取一个序列化器
	 * @param claszz
	 * @return
	 */
	public Serializer getSerializer( String  claszz) {
		Serializer serializer =  serializerMap.get( claszz );
		if( serializer == null ) {
			Class<?> serializerClass;
			try {
				serializerClass = Thread.currentThread().getContextClassLoader().loadClass( claszz );
				serializer = (Serializer) serializerClass.newInstance();
			} catch (Exception e) {
				throw new IllegalArgumentException("Fail to load  serializerClass: " + claszz);
			}
			serializerMap.put( claszz ,  serializer );
		}
		
		return serializer;
	}
	
	public void removeSerializer(String claszz) {
		  serializerMap.remove( claszz );
	}
}

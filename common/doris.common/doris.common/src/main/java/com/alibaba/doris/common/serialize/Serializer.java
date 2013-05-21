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


/**
 * Serializer
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-6-7
 */
public interface Serializer {
	
	public static final byte BYTE_NULL = 0x00000000;
	public static final byte[] BYTES_NULL = new byte[]{ 0x00000000 };
	
	byte[] serialize(Object  o, Object arg) ;
	
	Object deserialize(byte[] o, Object deserializeTarget);
}

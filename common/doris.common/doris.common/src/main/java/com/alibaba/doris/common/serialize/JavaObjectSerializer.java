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
package com.alibaba.doris.common.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Raymond He ( He Kun), raymond.he.kk@gmail.com
 * @since 1.0
 * 2011-7-6
 */
public class JavaObjectSerializer implements Serializer {

	/**
	 * @see com.alibaba.doris.common.serialize.Serializer#deserialize(byte[], java.lang.Object)
	 */
	public Object deserialize(byte[] bvalue, Object deserializeTarget) {
		if( bvalue == null) 
			return null;
		
		ByteArrayInputStream bais = new ByteArrayInputStream( bvalue );
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object obj = ois.readObject();
			return obj;
			
		} catch (Exception e) {
			throw new RuntimeException("Fail to deserialize object, byte value:" + bvalue +", cause: " + e, e);
		}finally {
			try {
				bais.close();  } catch (IOException e) {	}
		}
	}

	/**
	 * 
	 * @see com.alibaba.doris.common.serialize.Serializer#serialize(java.lang.Object, java.lang.Object)
	 */
	public byte[] serialize(Object o, Object arg) {
		
		byte[] bvalue = null;
		if( o == null ) {
			bvalue = new byte[]{ Byte.valueOf( (byte) 0 )};
			return bvalue;
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject( o );
			
			bvalue = baos.toByteArray();
			
			return bvalue;
		} catch (IOException e) {
			throw new RuntimeException("Fail to serialize object: " + o +", cause: " + e, e);
		}finally {
			try {
				baos.close();  } catch (IOException e) {	}
		}
	}

}

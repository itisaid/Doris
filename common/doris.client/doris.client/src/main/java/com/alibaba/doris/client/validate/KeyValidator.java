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
package com.alibaba.doris.client.validate;


/**
 * @author Raymond He ( He Kun), raymond.he.kk@gmail.com
 * @since 1.0
 * 2011-6-23
 */
public class KeyValidator implements Validator {

	private static final int MAX_KEY_LEN = 255;
	/**
	 * @see com.alibaba.doris.client.validate.Validator#validate(java.lang.Object)
	 */
	public void validate(Object key ) {
		
		 if(key == null) {
			 throw new IllegalArgumentException( "Key musn't be null!");
		 }
		 
		String sKey = null;
		if( key instanceof String) {
			sKey = (String)key;
		}else {
			sKey = String.valueOf( key ).trim() ;
		}
		
		 if( sKey.length() ==0 ) {
			 throw new IllegalArgumentException( "Key musn't be empty!");
		 }
		 
		 if(  sKey.length() > MAX_KEY_LEN ) {
			  throw new IllegalArgumentException("Invalid key, max length " + MAX_KEY_LEN  +"! " +  key );
		 }
	}
}

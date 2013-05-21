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
package com.alibaba.doris.common.operation;

import org.apache.commons.lang.StringUtils;


/**
 * OperationPolicy
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-9
 */
public class OperationPolicy {
	
	private static final String _Default_Policy = "2,2,1";
	
	private String namespace;
	private String policyString = _Default_Policy;
	
	private int copyCount;
	private int writeCount;
	private int readCount;
	
	public OperationPolicy(String namespace,String policyString) {
		this.namespace = namespace;
		this.policyString = policyString;
		
		parse( policyString );
	}
	
	public String getNamespace() {
		return namespace;
	}

	public int getCopyCount() {
		return copyCount;
	}


	public int getWriteCount() {
		return writeCount;
	}

	public int getReadCount() {
		return readCount;
	}
	
	public String getPolicy() {
		return policyString;
	}
	
	protected void parse(String policyString) {
		if( StringUtils.isBlank(policyString)) {
			throw new IllegalArgumentException("Invalid namespace policy cant' be null" ); 
		}
		
		try {
			String[] policyArray = StringUtils.split(policyString,",");
			
			int copyCount = Integer.valueOf(policyArray[0]);
			int writeCount = Integer.valueOf(policyArray[1]);
			int readCount = Integer.valueOf(policyArray[2]);
			
			this.copyCount = copyCount;
			this.writeCount = writeCount;
			this.readCount = readCount;	
		}catch(Exception e ) {
			throw new IllegalArgumentException("Invalid namespace policy string:'" + policyString +"'. Right format such as :'2,2,1'" , e);  
		}
	}
}

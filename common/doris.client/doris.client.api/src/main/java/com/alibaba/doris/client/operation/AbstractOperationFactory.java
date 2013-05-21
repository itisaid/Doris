/*
Copyright(C) 1999-2010 Alibaba Group Holding Limited
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
package com.alibaba.doris.client.operation;

import java.util.HashMap;
import java.util.Map;


/**
 * OperationFactory
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-22
 */
public abstract  class AbstractOperationFactory implements OperationFactory {
	
	protected Map<String,Operation> operationsMap = new HashMap<String, Operation>();

	public void addOperation(String opName, Operation operation) {
		operationsMap.put(opName, operation);
	}
	
	public Operation getOperation(String opName) {
		return operationsMap.get(opName);
	}
	
	public Operation removeOperation(String opName) {
		return operationsMap.remove(opName);
	}
	
	public Map<String, Operation> getOperations() {
		return operationsMap;
	}
}

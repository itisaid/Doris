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
package com.alibaba.doris.client.operation.failover.impl;

import java.util.List;

import com.alibaba.doris.client.AccessException;
import com.alibaba.doris.client.net.OperationFuture;
import com.alibaba.doris.client.operation.DataSourceOpFuture;
import com.alibaba.doris.client.operation.ExecutionStatus;
import com.alibaba.doris.client.operation.OperationData;
import com.alibaba.doris.client.operation.failover.LogicCallback;
import com.alibaba.doris.client.operation.result.DataSourceOpResult;

/**
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0
 * 2011-7-11
 */
public class DefaultLogicCallback implements LogicCallback {
	
	protected ExecutionStatus executionStatus = new ExecutionStatus();
	protected OperationFuture<Boolean> future;
	protected OperationData operationData;
	protected List<DataSourceOpResult> dataSourceOpResults;
	protected List<DataSourceOpFuture> dataSourceOpFutures;
	
	public DefaultLogicCallback( OperationData operationData) {
		this.operationData = operationData;
	}
	
	public OperationData getOperationData() {
		return operationData;
	}
	
	public ExecutionStatus getExecutionStatus() {
		return executionStatus;
	}
	
	public OperationFuture<?> getOperationFuture() {
		return future;
	}
	
	public void setOperationData(OperationData operationData) {
		this.operationData = operationData;
	}
	
	public List<DataSourceOpResult> getDataSourceOpResults() {
		return dataSourceOpResults;
	}
	
	public List<DataSourceOpFuture> getDataSourceOpFutures() {
		return dataSourceOpFutures;
	}
	public LogicCallback  execute() throws AccessException {
		
		return this;
	}
}

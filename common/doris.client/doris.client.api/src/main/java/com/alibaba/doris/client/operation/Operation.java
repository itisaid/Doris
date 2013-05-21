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

import java.util.List;

import com.alibaba.doris.client.AccessException;
import com.alibaba.doris.client.DataSourceManager;
import com.alibaba.doris.client.cn.OperationDataConverter;
import com.alibaba.doris.client.net.DataSource;
import com.alibaba.doris.client.operation.failover.CallbackHandlerFactory;
import com.alibaba.doris.client.operation.failover.PeerCallback;
import com.alibaba.doris.client.operation.result.DataSourceOpResult;
import com.alibaba.doris.common.operation.OperationEnum;

/**
 * Operation
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-22
 */
public interface Operation {
	
	 String getName();
	 
    OperationEnum getOperationType(OperationData operationData);
	 
	 void setDataSourceManager(DataSourceManager dataSourceManager);
	 
	 DataSourceManager getDataSourceManager();
	 
	 void execute(OperationData operationData);	
	 
	 List<List<DataSourceOpResult>> doLogicExecute(List<OperationDataSourceGroup> operationDataSourceGroups, List<OperationData> operationDatas) throws AccessException;
	 	 
	 PeerCallback doPeerExecute(DataSource dataSource,OperationData operationData) throws AccessException;

	 void setFailoverHandlerFactory(CallbackHandlerFactory callbackHandlerFactory) ;

	void setOperationDataConverter(OperationDataConverter operationDataConverter);
}

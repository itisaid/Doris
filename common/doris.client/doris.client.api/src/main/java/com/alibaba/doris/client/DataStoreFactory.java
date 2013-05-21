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
package com.alibaba.doris.client;

import com.alibaba.doris.client.operation.OperationFactory;
import com.alibaba.doris.client.operation.failover.CallbackHandlerFactory;
import com.alibaba.doris.common.NamespaceManager;

/**
 * DataStoreFactory
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-21
 */
public interface DataStoreFactory  {
	
	public static final String _config_server_url = "config.server.url";
	public static final String _dataStoreFactory_class = "dataStoreFactory.class";
	public static final String _datasource_class = "datasource.class";
	
	public DataStore getDataStore(String namespace) ;
	
	public NamespaceManager getNamespaceManager();
	
	public void setNamespaceManager(NamespaceManager namespaceManager);
	
	public void setOperationFactory(OperationFactory operationFactory) ;
	
	public OperationFactory getOperationFactory() ;
	
	public void setDataSourceManager(DataSourceManager dataSourceManager) ;
	
	public DataSourceManager getDataSourceManager() ;
	
	public void setCallbackHandlerFactory(CallbackHandlerFactory callbackHandlerFactory) ;
	
	public CallbackHandlerFactory getCallbackHandlerFactory() ;
	
	public void close();
}

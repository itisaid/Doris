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

import com.alibaba.doris.client.operation.OperationDataSourceGroup;
import com.alibaba.doris.common.config.ConfigManager;
import com.alibaba.doris.common.config.Configurable;
import com.alibaba.doris.common.operation.OperationEnum;
import com.alibaba.doris.common.route.DorisRouterException;

/**
 * DataSourceManager
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-22
 */
public interface DataSourceManager  extends Configurable {
	
	public DataSourceRouter getDataSourceRouter();
	
	public void setDataSourceRouter(DataSourceRouter dataSourceRouter);
	
	public OperationDataSourceGroup getOperationDataSourceGroup( OperationEnum operationEnum,int count,Object key) throws DorisRouterException; 
	
	public void refresh();

	public ConfigManager getConfigManager();

	public void close();
}

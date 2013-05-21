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
package com.alibaba.doris.client;

import java.util.List;
import java.util.Map;

import com.alibaba.doris.client.net.DataSource;
import com.alibaba.doris.client.operation.OperationDataSourceGroup;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.config.Configurable;
import com.alibaba.doris.common.configer.RouteTableConfiger;
import com.alibaba.doris.common.event.RouteConfigListener;
import com.alibaba.doris.common.operation.OperationEnum;
import com.alibaba.doris.common.route.DorisRouterException;
import com.alibaba.doris.common.route.RouteStrategy;
import com.alibaba.doris.common.route.VirtualRouter;


/**
 * DataSourceRouter
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-5
 */
public interface DataSourceRouter extends  Configurable, RouteConfigListener {
	
	void setDataSourceClass(Class<? extends DataSource> dataSourceClass);
	
	void setNodeConfigerClass(Class<? extends RouteTableConfiger> nodeConfigerClass);
	
	void setNodeRouterClass(Class<? extends RouteStrategy> routeStrategyClass);
	
	RouteTableConfiger getRouteTableConfiger();
	
	RouteStrategy getRouteStrategy();
	
	Map<String,List<DataSource>>  getAllDataSources();
	
	StoreNode  getStoreNodeOf(DataSource dataSource);
	
	DataSource getDataSourceOf(StoreNode storeNode );
	
	OperationDataSourceGroup getOperationDataSourceGroup(OperationEnum operationEnum,int count,String key) throws DorisRouterException;

	void refresh() throws DataSourceException;

	public void setVirtualRouter(VirtualRouter virtualRouter);

	public VirtualRouter getVirtualRouter();
}

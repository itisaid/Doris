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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.client.net.DataSource;
import com.alibaba.doris.client.operation.OperationDataSourceGroup;
import com.alibaba.doris.common.config.ConfigManager;
import com.alibaba.doris.common.configer.RouteTableConfiger;
import com.alibaba.doris.common.operation.OperationEnum;
import com.alibaba.doris.common.route.DorisRouterException;
import com.alibaba.doris.common.route.RouteStrategy;
import com.alibaba.doris.common.route.VirtualRouter;

/**
 * DataSourceManagerImpl
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-22
 */
public class DataSourceManagerImpl implements DataSourceManager {
	
	 private static final Logger              logger              = LoggerFactory.getLogger( DataSourceManagerImpl.class );
	 
	private DataSourceRouter dataSourceRouter;	
	public ConfigManager configManager;
	
	public DataSourceManagerImpl() {
		
	}
	
	public void setDataSourceRouter(DataSourceRouter dataSourceRouter) {
		this.dataSourceRouter = dataSourceRouter;
	}

	public DataSourceRouter getDataSourceRouter() {
		return dataSourceRouter;
	}
	
	public OperationDataSourceGroup getOperationDataSourceGroup( OperationEnum operationEnum,int count,Object key) throws DorisRouterException {
		String stringKey = String.valueOf( key);
		OperationDataSourceGroup operationDataSourceGroup = dataSourceRouter.getOperationDataSourceGroup( operationEnum, count,stringKey);
		return operationDataSourceGroup;
	}
	
	/**
	 * Init config after setting values.
	 */
	@SuppressWarnings("unchecked")
	public void initConfig() {		
		
		Properties configProperties = configManager.getProperties();
		
		String dataSourceClassName = configProperties.getProperty("datasource.class");
		String nodeConfigerClassName = configProperties.getProperty("node.configer.class");
		String nodeRouterClassName = configProperties.getProperty("node.route.strategy.class");
		String virtualRouterClassName = configProperties.getProperty("node.route.virtualRouter.class");
		
		if(dataSourceClassName ==null || dataSourceClassName.trim().length() == 0 )
			throw new IllegalArgumentException("property not configed:datasource.class");
		
		dataSourceRouter = new DataSourceRouterImpl();
		
		try {
			Class<DataSource> dataSourceClass = (Class<DataSource>) Thread.currentThread().getContextClassLoader().loadClass( dataSourceClassName);
			Class<RouteTableConfiger> nodeConfigerClass = (Class<RouteTableConfiger>) Class.forName(nodeConfigerClassName);
			Class<RouteStrategy> nodeStrategyClass = (Class<RouteStrategy>) Class.forName(nodeRouterClassName);
			Class<VirtualRouter> virtualRouterClass = (Class<VirtualRouter>) Class.forName( virtualRouterClassName );
			
			dataSourceRouter.setDataSourceClass( dataSourceClass);
			dataSourceRouter.setNodeConfigerClass( nodeConfigerClass );
			dataSourceRouter.setNodeRouterClass( nodeStrategyClass );
			
			dataSourceRouter.setConfigManager(configManager);
			
			VirtualRouter virtualRouter = virtualRouterClass.newInstance();
			dataSourceRouter.setVirtualRouter(virtualRouter);
			
			dataSourceRouter.initConfig();
			
		} catch (Exception e) {
			throw new DorisClientException("Config error." + e ,e );
		}
	}
	
	public void refresh() {
		try {
			dataSourceRouter.refresh();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}		
	}
	
	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;		
	}
	
	public ConfigManager getConfigManager() {
		return configManager;
	}
	
	public void close() {
		
		Map<String, List<DataSource>> allDataSources = dataSourceRouter.getAllDataSources() ;
		
		if(logger.isInfoEnabled()) {
			logger.info("Try to close all datasources.");
		}
		int count = 0;
		Iterator<String> seqKeys = allDataSources.keySet().iterator();
		while( seqKeys.hasNext()) {
			String seqKey = seqKeys.next();
			
			List<DataSource> dataSources = allDataSources.get( seqKey );
			
			for (DataSource dataSource : dataSources ) {
				dataSource.close();
				
				count++;
				
				if(logger.isInfoEnabled()) {
					logger.info("Close datasource: " + dataSource);
				}
			}
		}
		
		if(logger.isInfoEnabled()) {
			logger.info("Finish close all datasources. Total count: " + count);
		}
	}
}

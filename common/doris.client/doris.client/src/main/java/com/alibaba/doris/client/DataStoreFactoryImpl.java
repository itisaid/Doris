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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.client.operation.OperationFactory;
import com.alibaba.doris.client.operation.failover.CallbackHandlerFactory;
import com.alibaba.doris.client.operation.failover.impl.DefaultCallbackHandlerFactory;
import com.alibaba.doris.client.operation.impl.DefaultKvOperationFactory;
import com.alibaba.doris.common.Namespace;
import com.alibaba.doris.common.NamespaceManager;
import com.alibaba.doris.common.adminservice.AdminServiceFactory;
import com.alibaba.doris.common.adminservice.UserAuthService;
import com.alibaba.doris.common.config.ConfigException;
import com.alibaba.doris.common.config.ConfigManager;
import com.alibaba.doris.common.config.Configurable;
import com.alibaba.doris.common.config.NamespaceManagerImpl;

/**
 * DataStoreFactory
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-21
 */

public class DataStoreFactoryImpl implements DataStoreFactory , Configurable {
	
	private static final Logger logger = LoggerFactory.getLogger( DataStoreFactoryImpl.class );
	
	private static final String _Default_Config = "doris-client.properties";

	private static final String _UserName = "doris.username";
	private static final String _Password = "doris.password";
	
	protected UserAuthService  userAuthService ;
	
	protected NamespaceManager namespaceManager;	
	protected DataSourceManager dataSourceManager;
	
	protected OperationFactory operationFactory;
	
	protected CallbackHandlerFactory callbackHandlerFactory;

	protected String configLocation;
	protected Properties configProperties;
	
	protected Object configManagerLock = new Object();
	protected ConfigManager configManager;
	
	//store all dataStores
	protected Map<String,DataStore> dataStoreMap ;
	
	public DataStoreFactoryImpl() {
		this( _Default_Config );
	}
	
	public DataStoreFactoryImpl(String configLocation) {
		this.configLocation = configLocation;
		initConfig();
	}

	/*
	 * 提供利用Properties初始化的方式
	 */
	public DataStoreFactoryImpl(Properties configProperties) {
		this.configProperties = configProperties;
		initConfig();
	}

	public DataStore getDataStore(String namespace) {
		DataStore dataStore = dataStoreMap.get( namespace );
		if( dataStore == null) {
			throw new DorisClientException("Doiris namespace not found: '" + namespace +"'.");
		}
		return dataStore;
	}
	

	public OperationFactory getOperationFactory() {
		return operationFactory;
	}

	public void setOperationFactory(OperationFactory operationFactory) {
		this.operationFactory = operationFactory;
	}

	public DataSourceManager getDataSourceManager() {
		return dataSourceManager;
	}
	
	
	public void setDataSourceManager(DataSourceManager dataSourceManager) {
		this.dataSourceManager = dataSourceManager;
	}
	
	public NamespaceManager getNamespaceManager() {
		return this.namespaceManager;
	}
	
	public CallbackHandlerFactory getCallbackHandlerFactory() {
		return callbackHandlerFactory;
	}

	public void setCallbackHandlerFactory(
			CallbackHandlerFactory callbackHandlerFactory) {
		this.callbackHandlerFactory = callbackHandlerFactory;
	}

	
	public void setConfigLocation(String configLocation) {
		this.configLocation = configLocation;
	}
	
	/**
	 * Init config by configLocation. Such as config.properties.
	 */
	public void initConfig() {
		
		initOperationFactory();		
		
		initCallbackHandlerFactory();
		
		initConfigManager();
		
		initUserAuth();
		
		initNamespace();
		
		initDataStore();		
		
		initDataSourceManager();
	}



	/**
	 * 
	 */
	protected void initCallbackHandlerFactory() {
		callbackHandlerFactory = new DefaultCallbackHandlerFactory();
	}

	/**
	 * 
	 */
	protected void initOperationFactory() {
		operationFactory  = new DefaultKvOperationFactory();
	}

	
	/**
	 * Init DataSourceManager 
	 */
	protected void initDataSourceManager() {
		dataSourceManager = new DataSourceManagerImpl();
		dataSourceManager.setConfigManager(configManager);
		dataSourceManager.initConfig();
	}
	
	/**
	 * Init config manager.
	 */
	protected void initConfigManager()  {
		try {
			
			synchronized (configManagerLock) {
				if( configManager == null) {
					configManager = new ClientConfigManager();
					
					configManager.setConfigLocation(configLocation );
					configManager.setConfigProperties(configProperties);
					configManager.initConfig();
				}	
			}
			
		} catch (ConfigException e) {
			throw e;
		}
	}
	
	protected void initUserAuthService() {
		userAuthService = AdminServiceFactory.getUserAuthService();
	}
	
	protected void initUserAuth() {
		
		initUserAuthService();
		
		String userName = configManager.getProperties().getProperty(  _UserName );
		String password = configManager.getProperties().getProperty( _Password  );
		
		if( logger.isInfoEnabled()) {
			logger.info(String.format( "Doris client auth: username:%s, password:%s" , userName,password ));
		}
		
		if( StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
			String msg = String.format("Username or password is missing: %s, %s" , userName,password  );
			throw new DorisClientException(msg );
		}
		int right = userAuthService.getUserAuth(userName, password);
		
		if( logger.isInfoEnabled()) {
			logger.info(String.format( "Returned doris client priviledge: %d" ,right ));
		}
		
		if( right < 0 ) {
			String msg = String.format("Insufficient doris client priviledge. username:%s, password:%s" , userName,password  );
			throw new DorisClientException(msg );
		}
	}
	
	/**
	 * Init all namespaces by config. In Doris it's remote config.
	 */
	protected void initNamespace() {
		
		namespaceManager = new NamespaceManagerImpl();		
		namespaceManager.setConfigManager(configManager);
		namespaceManager.initConfig();
	}
	
	/**
	 * Init local dataStore mapping by namespaces.
	 */
	protected void initDataStore() {
		
		Map<String,Namespace> namespaceMap = namespaceManager.getNamespaces();
		
		dataStoreMap = new HashMap<String, DataStore>();
		
		for(Map.Entry<String, Namespace> entry: namespaceMap.entrySet()) {
			Namespace namespace = entry.getValue();
			
			DataStore dataStore = new DataStoreImpl( namespace );
			dataStore.setDataStoreFactory(this);
			
			dataStoreMap.put(dataStore.getNamespace().getName() , dataStore);
		}
	}
	
	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;		
	}
	
	public void setNamespaceManager(NamespaceManager namespaceManager) {
		this.namespaceManager = namespaceManager;
		
	}
	
	public void close() {
		dataSourceManager.close();
	}
}

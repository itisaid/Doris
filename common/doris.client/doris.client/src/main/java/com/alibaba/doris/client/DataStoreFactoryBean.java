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


/**
 * DataStoreFactoryBean.
 * A factory bean class for use with Spring. It create datastoreFactory instance by config.
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-21
 */
public class DataStoreFactoryBean {
	
	private String configLocation;
	private DataStoreFactory dataStoreFactory;
	
	public void setConfigLocation(String configLocation) {
		this.configLocation = configLocation;
	}
	
	public String getConfigLocation() {
		return configLocation;
	}
	
	public Object getObject() {
		return dataStoreFactory;
	}
	
	public void initConfig() {
		this.dataStoreFactory = new DataStoreFactoryImpl(configLocation);
	}
}

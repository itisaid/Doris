/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.config.ConfigManagerImpl;
import com.alibaba.doris.common.util.PropertiesLoadUtil;

/**
 * ClientConfigManager
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-7-28
 */
public class ClientConfigManager extends ConfigManagerImpl  {
	
	private static final Logger              logger              = LoggerFactory.getLogger( DataSourceManagerImpl.class );
	 
	public static final String _Default_Client_Config = "default-doris-client.properties";
	
	public Properties loadDefaultConfig() {
		
		Properties defaultConfigProperties0 = PropertiesLoadUtil.loadProperties( _Default_Client_Config );
		
		if( defaultConfigProperties0 == null || defaultConfigProperties0.size() ==0) {
			if( logger.isDebugEnabled() ) {
				logger.debug("Default doris client config not found 'default-config-client.properties', use Application config instead.");
			}
		}else {
			if( logger.isDebugEnabled() ) {
				logger.debug("Default doris client config found 'default-config-client.properties', will be merged to Application config.");
			}
		}
		return defaultConfigProperties0;
	}
}

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
package com.alibaba.doris.common.config;

/**
 * ConfigUtil
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-16
 */
public class ConfigUtil {
	
	/**
	 * setConfigManager
	 * @param o
	 * @param configManager
	 */
	public static void setConfigManager(Object o,ConfigManager configManager) {
		
		if( o instanceof Configurable) {
			Configurable configurable = (Configurable)o;
			configurable.setConfigManager(configManager);				
		}
	}
	
	/**
	 * initConfig
	 * @param o
	 */
	public static void initConfig(Object o) {
		
		if( o instanceof Configurable) {
			Configurable configurable = (Configurable)o;
			configurable.initConfig();	
		}
	}
}

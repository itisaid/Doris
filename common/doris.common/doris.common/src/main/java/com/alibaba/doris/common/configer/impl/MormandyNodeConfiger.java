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
package com.alibaba.doris.common.configer.impl;

import java.util.List;

import com.alibaba.doris.common.config.ConfigManager;
import com.alibaba.doris.common.configer.RouteTableConfiger;
import com.alibaba.doris.common.event.RouteConfigListener;
import com.alibaba.doris.common.route.RouteTable;

/**
 * MockRouteTableConfiger.<br/>
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-25
 */
public class MormandyNodeConfiger implements RouteTableConfiger {
	
	private RouteTable routeTable;
	
	private List<RouteConfigListener> routeConfigListeners;
	private String configLocation;
	
	public void setConfigServerUrl(String configServerURL) {
		this.configLocation = configServerURL;
	}
	
	public String getConfigListenerName() {
		return "RouteTable";
	}
	
	public void onConfigChange(String configContent) {
		
	}

	public RouteTable getRouteTable() {
		return routeTable;
	}
	
	public RouteTable getRouteTable(boolean t) {
		return null;
	}
	
	
	public void initConfig() {
	}
	
	public void setConfigManager(ConfigManager configManager) {
		// TODO Auto-generated method stub
		
	}
	
	public void addConfigListener(RouteConfigListener routeConfigListener) {
		routeConfigListeners.add( routeConfigListener );	
	}
	
	public List<RouteConfigListener> getConfigListeners() {
		return routeConfigListeners;
	}

    public Long getConfigVersion() {
        // TODO Auto-generated method stub
        return null;
    }
}

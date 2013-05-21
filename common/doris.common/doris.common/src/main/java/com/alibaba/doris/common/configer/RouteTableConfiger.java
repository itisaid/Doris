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
package com.alibaba.doris.common.configer;

import java.util.List;

import com.alibaba.doris.common.config.ConfigListener;
import com.alibaba.doris.common.config.Configurable;
import com.alibaba.doris.common.event.RouteConfigListener;
import com.alibaba.doris.common.route.RouteTable;

/**
 * MockRouteTableConfiger.<br/>
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-25
 */
public interface RouteTableConfiger extends ConfigListener,Configurable  {
	
	/**
	 * Get current route table.
	 * @return
	 */
	RouteTable getRouteTable();
	
	/**
	 * Get the latest route table. For remote configer, it need to get the latest one from config server.
	 * @param t
	 * @return
	 */
	RouteTable getRouteTable(boolean t);
	/**
	 * Add a config listener. When config changes, RouteTableConfiger will notify all the listeners.e
	 * @param routeConfigListener
	 */
	void addConfigListener(RouteConfigListener routeConfigListener);
	
	/**
	 * Get current listeners.
	 * @return
	 */
	public List<RouteConfigListener> getConfigListeners();
	
	
}

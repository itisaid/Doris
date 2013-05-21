/*
Copyright(C) 1999-2010 Alibaba Group Holding Limited
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or ied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.alibaba.doris.common.event;

import com.alibaba.doris.common.route.RouteTable;


/**
 * RouteConfigChangeEvent
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-25
 */
public class RouteConfigChangeEvent {
	
	private RouteTable routeTable;
	
	public RouteTable getRouteTable() {
		return routeTable;
	}
	
	public void setRouteTable(RouteTable routeTable) {
		this.routeTable = routeTable;
	}
}

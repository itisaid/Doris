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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.StoreNodeSequenceEnum;
import com.alibaba.doris.common.config.ConfigManager;
import com.alibaba.doris.common.configer.RouteTableConfiger;
import com.alibaba.doris.common.event.RouteConfigChangeEvent;
import com.alibaba.doris.common.event.RouteConfigListener;
import com.alibaba.doris.common.route.RouteTable;
import com.alibaba.doris.common.route.RouteTableImpl;

/**
 * MockRouteTableConfiger.<br/>
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-25
 */
public class MockRouteTableConfiger implements RouteTableConfiger {
	
	private Long configVersion = new Long(0);
	private RouteTable routeTable;
	
	private List<RouteConfigListener> routeConfigListeners = new ArrayList<RouteConfigListener>(3);
	private String configLocation;
	
	public void setConfigServerUrl(String configServerURL) {
		this.configLocation = configServerURL;
	}
	
	public MockRouteTableConfiger() {
	}
	
	public String getConfigListenerName() {
		return "routeConfig";
	}
	
	public RouteTable getRouteTable() {
		return routeTable;
	}
	
	
	public RouteTable getRouteTable(boolean t) {
		return routeTable;
	}
	
	public void addConfigListener(RouteConfigListener routeConfigListener) {
		routeConfigListeners.add( routeConfigListener );	
	}
	
	public List<RouteConfigListener> getConfigListeners() {
		return routeConfigListeners;
	}
	
	public void setConfigLocation(String configLocation) {
		this.configLocation = configLocation;
	}
	
	public void initConfig() {
		//get NodeList config from AdminServer by configLocation.
		refresh();
	}
	
	public void onConfigChange(String configContent) {
				
	}
	
	public boolean refresh() {
		
		routeTable = new RouteTableImpl();
		routeTable.setVersion( 1 );
		List<StoreNode> nodeList1 = new ArrayList<StoreNode>();
		
		//Seq 1
		for (int i = 0; i < 4; i++) {
			StoreNode storeNode = new StoreNode();
			storeNode.setSequence(  StoreNodeSequenceEnum.NORMAL_SEQUENCE_1 );
			storeNode.setLogicId(i);
			storeNode.setPhId("node" + i);
			storeNode.setIp("192.168.0." + i);
			storeNode.setPort(9000);
			
			nodeList1.add(storeNode);
		}
		
		//Seq 2
		List<StoreNode> nodeList2 = new ArrayList<StoreNode>();
		for (int i = 0; i < 4; i++) {
			StoreNode storeNode = new StoreNode();
			storeNode.setSequence(  StoreNodeSequenceEnum.NORMAL_SEQUENCE_2 );
			storeNode.setLogicId(i);
			storeNode.setPhId("node" + i);
			storeNode.setIp("192.168.1." + i);
			storeNode.setPort(9000);
			
			nodeList2.add(storeNode);
		}
		
		List<List<StoreNode>> mainSeqNodes = new ArrayList<List<StoreNode>>();
		mainSeqNodes.add( nodeList1 );
		mainSeqNodes.add( nodeList2 );
		
		routeTable.setMainStoreNodeList( mainSeqNodes );
		
		fireConfigChangeEvent();
		
		return true;
	}

	/**
	 * 
	 */
	private void fireConfigChangeEvent() {
		for (RouteConfigListener routeConfigListener: routeConfigListeners ) {
			RouteConfigChangeEvent routeConfigChangeEvent = new RouteConfigChangeEvent();

			routeConfigChangeEvent.setRouteTable( routeTable );
			routeConfigListener.onConfigChange( routeConfigChangeEvent );
		}
	}
	
	public void setConfigManager(ConfigManager configManager) {
		configManager.addConfigListener(this);
	}

	public void setConfigVersion(Long configVersion) {
		this.configVersion = configVersion;
	}
	
    public Long getConfigVersion() {
        return configVersion++;
    }
}

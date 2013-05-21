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
package com.alibaba.doris.common.configer.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import com.alibaba.doris.algorithm.ConsistentHashRouteAlglorithm;
import com.alibaba.doris.algorithm.KetamaHashFunction;
import com.alibaba.doris.algorithm.RouteAlgorithm;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.config.ConfigManager;
import com.alibaba.doris.common.event.RouteConfigChangeEvent;
import com.alibaba.doris.common.operation.OperationEnum;
import com.alibaba.doris.common.route.DorisRouterException;
import com.alibaba.doris.common.route.RouteStrategy;
import com.alibaba.doris.common.route.RouteTable;

/**
 * NodeRouteStrategyImpl
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-5
 */
public class MockRouteStrategyImpl implements RouteStrategy {
	
	private static final Random random = new Random();
	
	public RouteAlgorithm routeAlgorithm;
	private RouteTable routeTable;
	private ConfigManager configManager;
	private Properties configProperties;
	
	public void setRouteTable(RouteTable routeTable) {
		this.routeTable = routeTable; 		
	}
	
	public RouteTable getRouteTable() {
        return this.routeTable;
    }

    public List<StoreNode> findNodes(OperationEnum operationEnum, int count, String key) {
		
		Integer nodeIndex = routeAlgorithm.getNodeByKey(key);	
		
		List<StoreNode> nodesByKey = new ArrayList<StoreNode>(count);
		
		if( operationEnum == OperationEnum.WRITE) {
			nodesByKey = findWriteNodes( count, nodeIndex);
		}else {
			nodesByKey = findReadNodes(count, nodeIndex);
		}
		return nodesByKey;
	}

	/**
	 * @param operationEnum
	 * @param count
	 * @param nodeIndex
	 * @param nodesByKey
	 */
	protected List<StoreNode> findWriteNodes(int count, Integer nodeIndex) {
			
		List<StoreNode> nodesByKey = new ArrayList<StoreNode>(count);
		for (int i = 0; i < routeTable.getMainStoreNodeList().size() && i < count; i++) {
			
			List<StoreNode> seqNodes = routeTable.getMainStoreNodeList().get(i);
			StoreNode storeNode = seqNodes.get( nodeIndex);
			nodesByKey.add(  storeNode );
		}
		
		return nodesByKey;
	}
	
	/**
	 * @param operationEnum
	 * @param count
	 * @param nodeIndex
	 * @param nodesByKey
	 */
	protected List<StoreNode> findReadNodes(int count, Integer nodeIndex) {
			
		List<StoreNode> nodesByKey = new ArrayList<StoreNode>(count);
		
		int seqCount = routeTable.getMainStoreNodeList().size();
		if( seqCount > 1 && count == 1 ) {
			
			int targetIndex = random.nextInt( seqCount );
			
			StoreNode storeNode = routeTable.getMainStoreNodeList().get(  targetIndex ).get( nodeIndex) ;
			nodesByKey.add( storeNode ) ;
			
		}else {
			
			nodesByKey = findWriteNodes(seqCount, nodeIndex);
		}		
		return nodesByKey;
	}
	
	public void setRouteAlgorithm(RouteAlgorithm routeAlgorithm) {
		this.routeAlgorithm = routeAlgorithm;
	}
	
	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;		
	}
	
	@SuppressWarnings("unchecked")
	public void initConfig() {
		if( configManager == null)
			throw new IllegalArgumentException("Route strategy's property configManager  not set.");
		
		Properties configPropeties = configManager.getProperties();
		
		Collection<Integer> intNodes = new ArrayList<Integer>();
		int replacas = 200;
		
		int seqCount = routeTable.getMainStoreNodeList().size();
		
		for (StoreNode storeNode : routeTable.getMainStoreNodeList().get(0)) {
			intNodes.add( Integer.valueOf( storeNode.getLogicId()) );
		}
		
		if( configProperties != null) {
			String routeAlgorithmClassName = configProperties.getProperty( RouteAlgorithm._Route_algorithm_class ); 
			
			Class<? extends RouteAlgorithm> routeAlgorithmClass = null;
			if( routeAlgorithmClassName != null) {
				try {
					routeAlgorithmClass = (Class<? extends RouteAlgorithm>) Thread.currentThread().getContextClassLoader().loadClass(routeAlgorithmClassName);
					routeAlgorithm = routeAlgorithmClass.newInstance();
					
				} catch (Exception e) {
					throw new IllegalArgumentException("RouteAlglorithm class not found or not implements " + RouteAlgorithm.class.getName() +". '" + routeAlgorithmClassName +"'." , e);
				}
			}
		}		
		
		if(routeAlgorithm == null)
			routeAlgorithm = new ConsistentHashRouteAlglorithm( new KetamaHashFunction(),replacas ,intNodes); 
		
		ConsistentHashRouteAlglorithm chash = (ConsistentHashRouteAlglorithm)routeAlgorithm;
		chash.setConfigProperties(configProperties);
		chash.setNodeCount( intNodes.size() );		
		chash.setNumberOfReplicas( 200 );
		
		chash.init();
	}	
	
	/**
	 * Readd nodes when StoreNode config changes. 
	 */
	public void onConfigChange(RouteConfigChangeEvent event) {		
		this.routeTable = event.getRouteTable();
		initConfig();
	}

    public StoreNode findFailoverNode(OperationEnum type, int copyCount, String key, StoreNode sn)
                                                                                                  throws DorisRouterException {
        // TODO Auto-generated method stub
        return null;
    }
}

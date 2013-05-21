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
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import com.alibaba.doris.algorithm.ConsistentHashRouteAlglorithm;
import com.alibaba.doris.algorithm.KetamaHashFunction;
import com.alibaba.doris.algorithm.RouteAlgorithm;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.operation.OperationEnum;
import com.alibaba.doris.common.route.DorisRouterException;
import com.alibaba.doris.common.route.RouteStrategy;
import com.alibaba.doris.common.route.RouteTable;
import com.alibaba.doris.common.route.RouteTableImpl;

/**
 * NodeRouterImplTest
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-6
 */
public class RouteStrategyImplTest extends TestCase {
	
	/**
	 * @throws java.lang.Exception
	 */
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.alibaba.doris.common.configer.impl.NodeRouteStrategyImpl#getNodesByKey(java.lang.Object, int)}.
	 * @throws DorisRouterException 
	 */
	public void testGetNodesByKey() throws DorisRouterException {
		
		RouteTable routeTable = createMockRouteTable();
		
		RouteStrategy routeStrategy = new MockRouteStrategyImpl();
		
		int replacas = 200;
		List<Integer> initNodes = new ArrayList<Integer>();
		List<StoreNode> storeNodes = new ArrayList<StoreNode>();
		
		for (int i = 0; i < 10; i++) {
			StoreNode storeNode = new StoreNode();
			storeNode.setLogicId(i);
			storeNode.setPhId("StoreNode" +i);
			storeNode.setIp("10.20.1.12" + i);
			storeNode.setPort(9000);
			
			storeNodes.add(storeNode);
			initNodes.add(Integer.valueOf(i));
		}
		RouteAlgorithm routeAlgorithm = new ConsistentHashRouteAlglorithm( new KetamaHashFunction(),replacas ,initNodes);
		routeStrategy.setRouteAlgorithm(routeAlgorithm);
		
		routeStrategy.setRouteTable(routeTable);
		
		List<StoreNode> resultNodes1 = routeStrategy.findNodes(OperationEnum.WRITE, 1, "key1");
		
		List<StoreNode> resultNodes2 = routeStrategy.findNodes( OperationEnum.WRITE, 2, "key1");
		
		List<StoreNode> resultNodes3 = routeStrategy.findNodes( OperationEnum.WRITE,3,"key1");
		
		System.out.println("result node1 " + resultNodes1);
		System.out.println("result node2 " + resultNodes2);
		System.out.println("result node3 " + resultNodes3);
	}

	/**
	 * @return
	 */
	private RouteTable createMockRouteTable() {
		RouteTable routeTable = new RouteTableImpl();
		
		List<List<StoreNode>> mainStoreNodeList = new ArrayList<List<StoreNode>>();
		List<StoreNode> seq1NodeList = new ArrayList<StoreNode>();
		List<StoreNode> seq2NodeList = new ArrayList<StoreNode>();
		
		for (int i = 0; i < 10; i++) {
			StoreNode storeNode = new StoreNode();
			storeNode.setLogicId(i);

//			storeNode.setPhId("node" + i);
//			
//			storeNode.setIp("192.168.0." + i);

			storeNode.setPhId("node1." + i);
			storeNode.setIp("192.168.1." + i);

			storeNode.setPort(9000 + i);
			
			seq1NodeList.add(storeNode);
		}
		
		for (int i = 0; i < 10; i++) {
			StoreNode storeNode = new StoreNode();
			storeNode.setLogicId(i);
			storeNode.setPhId("node2." + i);
			storeNode.setIp("192.168.2." + i);
			storeNode.setPort(9000 + i);
			
			seq2NodeList.add(storeNode);
		}
		
		mainStoreNodeList.add( seq1NodeList );
		mainStoreNodeList.add( seq2NodeList );
		List<List<StoreNode>> mainList = new ArrayList<List<StoreNode>>();
//		List<StoreNode> main1 = new ArrayList<StoreNode>();
//		main1.add(nodeList.get(0));
//		main1.add(nodeList.get(1));
//		main1.add(nodeList.get(2));
//		main1.add(nodeList.get(3));
//		mainList.add(main1);
		
//<<<<<<< .mine
//		List<StoreNode> main2 = new ArrayList<StoreNode>();
//		main2.add(nodeList.get(4));
//		main2.add(nodeList.get(5));
//		main2.add(nodeList.get(6));
//		main2.add(nodeList.get(7));
//        mainList.add(main2);
//=======
//		routeTable.setNodeList( seq1NodeList);
		routeTable.setMainStoreNodeList( mainStoreNodeList );
//>>>>>>> .r100351
		
//        List<StoreNode> backup = new ArrayList<StoreNode>();
//        backup.add(nodeList.get(8));
//        backup.add(nodeList.get(9));
        
		routeTable.setMainStoreNodeList(mainList);
		//routeTable.setBackupStoreNodeList(backup);
		
		return routeTable;
	}
	
	public static void main(String[] args) {
		TestRunner.run( RouteStrategyImplTest.class);
	}
}

package com.alibaba.doris.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.doris.client.DataSourceException;
import com.alibaba.doris.client.DataStoreFactory;
import com.alibaba.doris.client.DataStoreFactoryImpl;
import com.alibaba.doris.common.Namespace;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.operation.OperationEnum;
import com.alibaba.doris.common.route.DorisRouterException;
import com.alibaba.doris.common.route.RouteTable;

public class KeyMaker {

	static String configUrl = "doris-client.properties";
	static DataStoreFactory dataStoreFactory = new DataStoreFactoryImpl(
			configUrl);

	public static List<String> makeKey(int number, int sequenceNum,
			String physicalId, String prefix) {
		int keyNum = 0, increaseNum = 0;// 满足条件的key个数,自增的数字
		List<String> keysList = new ArrayList<String>();// 满足条件的key列表
		String key = "";
		List<StoreNode> storeNodeList = new ArrayList<StoreNode>();
		while (keyNum < number) {
			key = prefix + increaseNum;
			try {
				storeNodeList = dataStoreFactory.getDataSourceManager()
						.getDataSourceRouter().getRouteStrategy()
						.findNodes(OperationEnum.READ, sequenceNum, key);
				for (StoreNode sn : storeNodeList) {
					if (physicalId.equals(sn.getPhId())) {
						keyNum++;
						keysList.add(key);
					}
				}
			} catch (DorisRouterException e) {
				e.printStackTrace();
				break;
			}
			increaseNum++;
		}
		return keysList;

	}

	public static void main(String[] args) throws DataSourceException {
	/*	Map<String, Namespace> map = dataStoreFactory.getNamespaceManager()
				.getNamespaces();
		Set<String> set = map.keySet();
		for (String namespaceKey : set) {
			System.out.println(namespaceKey);
			System.out.println(map.get(namespaceKey));
		}*/
		RouteTable routeTable = dataStoreFactory.getDataSourceManager()
				.getDataSourceRouter().getRouteTableConfiger().getRouteTable();
		List<StoreNode> noList = routeTable.getNodeList();
		String firstphId = "";
		for (StoreNode storeNode : noList) {
			firstphId = storeNode.getPhId();
			System.out.println("phId:" + storeNode.getPhId());
			System.out.println("LogicId:" + storeNode.getLogicId());
			System.out.println("Sequence:" + storeNode.getSequence());
		}
		System.out.println("FirstPhId:" + firstphId);
		List<String> a = KeyMaker.makeKey(10, 2, firstphId, "a");
		for (String key : a) {
			System.out.println(key);
		}

	}
}

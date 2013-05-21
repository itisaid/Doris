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
package com.alibaba.doris.client.operation;

import java.util.List;

import com.alibaba.doris.client.net.DataSource;
import com.alibaba.doris.common.StoreNode;

/**
 * OperationDataSourceGroup
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-22
 */
public class OperationDataSourceGroup {
	
	private Object key;
	
	private List<DataSource> dataSources ;	
	private List<StoreNode> storeNodes;
	
	public OperationDataSourceGroup(Object key,List<DataSource> dataSources,List<StoreNode> storeNodes) {
		this.key= key;
		this.dataSources = dataSources;
		this.storeNodes = storeNodes;
	}
	public void setDataSources(List<DataSource> dataSources) {
		this.dataSources = dataSources;
	}
	
	public List<DataSource> getDataSources() {
		return dataSources;
	}
	
	public void setKey(Object key) {
		this.key = key;
	}
	
	public Object getKey() {
		return key;
	}
	public List<StoreNode> getNodes() {
		return storeNodes;
	}
	public void setNodes(List<StoreNode> storeNodes) {
		this.storeNodes = storeNodes;
	}	
}

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
package com.alibaba.doris.common.route;

import java.util.List;

import com.alibaba.doris.common.StoreNode;

/**
 * RouteTable. 路由表
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-13
 */
public interface RouteTable {
	
	long getVersion();
	
	void setVersion(long version);
	
	void setNodeList(List<StoreNode> nodes);
	
	List<StoreNode> getNodeList();
	
	public List<List<StoreNode>> getMainStoreNodeList();
	
	public void setMainStoreNodeList(List<List<StoreNode>> nodes);
	
	public List<StoreNode> getBackupStoreNodeList();
	
	public void setBackupStoreNodeList(List<StoreNode> nodes);
	
	public StoreNode getStoreNode(int seqNo, int logicId);
}

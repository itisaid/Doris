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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.StoreNodeSequenceEnum;

/**
 * RouteTableImpl
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-13
 */
public class RouteTableImpl implements RouteTable {

	private long version;

	private List<StoreNode> nodes;
	private List<List<StoreNode>> mainStoreNodeList;
	private List<StoreNode> backupStoreNodeList;

	public List<List<StoreNode>> getMainStoreNodeList() {
		return mainStoreNodeList;
	}

	public void setMainStoreNodeList(List<List<StoreNode>> mainStoreNodeList) {
		this.mainStoreNodeList = mainStoreNodeList;
	}

	public List<StoreNode> getBackupStoreNodeList() {
		return backupStoreNodeList;
	}

	public void setBackupStoreNodeList(List<StoreNode> backupStoreNodeList) {
		this.backupStoreNodeList = backupStoreNodeList;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public List<StoreNode> getNodeList() {
		List<StoreNode> nodes = new ArrayList<StoreNode>();

		if (mainStoreNodeList != null)
			for (int i = 0; i < mainStoreNodeList.size(); i++) {
				nodes.addAll(mainStoreNodeList.get(i));
			}

		if (backupStoreNodeList != null)
			nodes.addAll(backupStoreNodeList);
		return nodes;
	}

	/**
	 * 将1，2，3序列的节点合并为一个集合
	 */
	public void setNodeList(List<StoreNode> nodes) {
		this.nodes = nodes;
	}

	/**
	 * 根据序列号和逻辑编号获取StoreNode
	 */
	public StoreNode getStoreNode(int seqNo, int logicId) {
		StoreNode storeNode = null;
		List<StoreNode> storeNodes = getSeqNodesBySeqNo(seqNo);
		if (logicId < storeNodes.size()) {
			storeNode = storeNodes.get(logicId);
		}

		return storeNode;
	}

	/**
	 * @param seqNomainStoreNodeList
	 * @return
	 */
	private List<StoreNode> getSeqNodesBySeqNo(int seqNo) {
		for (List<StoreNode> snList : mainStoreNodeList) {
			if (!snList.isEmpty()) {
				StoreNode sn = snList.get(0);
				if (sn.getSequence().getValue() == seqNo) {
					return snList;
				}
			}
		}
		if (seqNo == StoreNodeSequenceEnum.TEMP_SEQUENCE.getValue()) {
			return backupStoreNodeList;
		}
		return null;
	}

}

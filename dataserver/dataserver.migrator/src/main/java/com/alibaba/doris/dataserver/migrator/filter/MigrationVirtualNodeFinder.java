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
package com.alibaba.doris.dataserver.migrator.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.doris.common.route.MigrationRoutePair;

/**
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0
 * 2011-7-14
 */
public class MigrationVirtualNodeFinder {
	
	private  List<MigrationRoutePair> migrationRoutePairs;
	private Map<Integer, String> index;
	
	public MigrationVirtualNodeFinder(List<MigrationRoutePair> migrationRoutePairs) {
		this.migrationRoutePairs = migrationRoutePairs;		
		
		buildIndex();
	}

	/**
	 * 检查某个虚拟节点是否要迁移. 
	 * @param vnode
	 * @return
	 */
	public String getTargetNodeOfVirtualNode(int vnode) {
		return index.get( vnode );
	}
	
	private void buildIndex() {
		index = new HashMap<Integer, String>( (int)( migrationRoutePairs.size() * 1.33 ) );
		for ( MigrationRoutePair pair : migrationRoutePairs) {
			index.put( pair.getVnode(), pair.getTargetPhysicalId());
		}
	}
}

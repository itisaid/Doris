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
package com.alibaba.doris.algorithm.mirgate;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.doris.algorithm.vpm.VpmMapping;

/**
 * MigrationRouter.
 * 
 * 迁移路由计算器(基于虚拟节点算法)<p/>
 * 
 * 根据物理节点个数 PNodeCount, 创建两份路由映射表，
 * 对比两份路由表，判断旧路由表中哪些虚拟节点映射到新的路由表。
 * 
 * 数据类型: List<List<MigrationPair>> 
 * 格式:     [vnode:source-target]:
 * 例如:  [[3:0-3], [9:1-3], [11:2-3]]
 * 表示 虚拟节点 3， 由物理节点 0 迁移到物理节点 3. 以此类推。
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-18
 */
public class MigrationRouter {
	
	private List<List<Integer>> vpmMapping;
	private List<List<Integer>> vpmMapping2;
	
	private int vNodes;
	int oldPNodeCount;
	int newPNodeCount;
	
	private List<List<MigrationPair>> allNodeMigrationPairs;
	
	public MigrationRouter(int vNodes,	int oldPNodeCount, int newPNodeCount) {
		this.vNodes = vNodes;
		this.oldPNodeCount = oldPNodeCount;
		this.newPNodeCount = newPNodeCount;
		
		vpmMapping = VpmMapping.makeP2VMapping( oldPNodeCount, vNodes );
	    vpmMapping2 = VpmMapping.makeP2VMapping( newPNodeCount ,vNodes);
	    
	    computeDeltas();
	}
	
	public int getNewPNodeCount() {
		return newPNodeCount;
	}
	
	public int getOldPNodeCount() {
		return oldPNodeCount;
	}
	/**
	 * 
	 * @see com.alibaba.doris.common.router.migrate.MigrationRouter#getMigrationDeltaByPNode(int, int, int, int)
	 */
	public List<MigrationPair> getMigrationPairOfPNode(int pNodeNo) {
		 return allNodeMigrationPairs.get(pNodeNo);
	}
	
	public List<List<MigrationPair>> getAllNodeMigrationPairs() {
		return allNodeMigrationPairs;
	}
	
	/**
	 * 
	 * @see com.alibaba.doris.common.router.migrate.MigrationRouter#getMigrationDeltas(int, int, int)
	 */
	private List<List<MigrationPair>>  computeDeltas() {
	    
	    allNodeMigrationPairs = new ArrayList<List<MigrationPair>>();
	    for (int i = 0; i < vpmMapping.size(); i++) {
	    	List<MigrationPair>  pair = computeDeltasByPNode( i);
	    	
	    	allNodeMigrationPairs.add( pair );
		}
	   
		return allNodeMigrationPairs;
	}

	/**
	 * @param vpmMapping
	 * @param vpmMapping2
	 */
	protected List<MigrationPair>  computeDeltasByPNode(int pNo) {
	    
	    List<Integer> p2vMapping = vpmMapping.get( pNo );
        List<Integer> p2vMapping2 = vpmMapping2.get( pNo );
        
        
        List<Integer> deltaVNodes = new ArrayList<Integer>();
        for (Integer v1: p2vMapping) {
			
        	if( !p2vMapping2.contains( v1 ))
        		deltaVNodes.add( v1 );
		}
        
        List<MigrationPair> mPairs = new ArrayList<MigrationPair>( deltaVNodes.size());
        
        for( Integer vdelta: deltaVNodes) {
        	
        	for (int i = 0 ; i< vpmMapping2.size() ; i++) {
        		
        		List<Integer> pNodes = vpmMapping2.get(i);
        		for (Integer vNode: pNodes ) {            		
            		if( vdelta.equals( vNode)) {
            			MigrationPair pair = new MigrationPair(vdelta,pNo, i);
            			mPairs.add(pair);
            		}
            	}
        	}
        }        
        return mPairs;
	}

}

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
package com.alibaba.doris.algorithm.vpm;

import java.util.List;

import com.alibaba.doris.algorithm.vpm.VpmMapping;

/**
 * PNode2VNodeMapping
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-18
 */
public class PNode2VNodeMapping {
	
	final private int vNodes;
	private int PNodeCount;
	private List<List<Integer>> vpmMapping;
	
	
	public PNode2VNodeMapping(int vNodes, int pNodeCount) {
		this.vNodes = vNodes;
		this.PNodeCount = pNodeCount;
		
		vpmMapping = VpmMapping.makeP2VMapping( pNodeCount, vNodes );
	}
	
	public List<Integer> getVNodes(int pNode) {
		return vpmMapping.get( pNode );
	}
	
	public int getPNodeCount() {
		return PNodeCount;
	}
	
	public List<List<Integer>> getVpmMapping() {
		return vpmMapping;
	}
}

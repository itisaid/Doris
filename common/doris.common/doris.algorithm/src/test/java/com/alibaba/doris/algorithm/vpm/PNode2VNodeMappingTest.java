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

import junit.framework.Assert;

import org.junit.Test;

/**
 * PNode2VNodeMappingTest
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-18
 */
public class PNode2VNodeMappingTest {

	/**
	 * Test method for {@link com.alibaba.doris.algorithm.vpm.PNode2VNodeMapping#getVNodes(int)}.
	 */
	@Test
	public void testGetVNodes() {
		
		PNode2VNodeMapping mapping = new PNode2VNodeMapping(12, 3);
		
		System.out.println( "vNodes: of (12,3): " + mapping);
		
		List<Integer> vNodes = mapping.getVNodes(1);
		
		System.out.println( "vNodes: of (12,3:1): " + vNodes );
		  
		Assert.assertEquals(6, vNodes.get(0).intValue());
		Assert.assertEquals(7, vNodes.get(1).intValue());
		Assert.assertEquals(8, vNodes.get(2).intValue());
		Assert.assertEquals(9, vNodes.get(3).intValue());
	}

}

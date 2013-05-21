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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.alibaba.doris.common.route.MigrationRoutePair;

/**
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0
 * 2011-7-14
 */
public class MigrationVirtualNodeFinderTest {

	
	/**
	 * Test method for {@link com.alibaba.doris.dataserver.migrator.filter.MigrationVirtualNodeFinder#getTargetNodeOfVirtualNode(int)}.
	 */
	@Test
	public void testGetTargetNodeOfVirtualNode() {
		
		  List<MigrationRoutePair> migrationRoutePairs = new ArrayList<MigrationRoutePair>();
		  
		  for (int i = 0; i < 1000 ; i++) {
			  MigrationRoutePair pair = new MigrationRoutePair();
			  pair.setVnode( i );
			  pair.setTargetPhysicalId("127.0.0.1");
			  
			  migrationRoutePairs.add( pair );
		 }
		  
		MigrationVirtualNodeFinder finder = new MigrationVirtualNodeFinder(migrationRoutePairs);
		
		String targetNode1 = finder.getTargetNodeOfVirtualNode( 100 );
		assertNotNull("targetNode1 found", targetNode1);
		
		String targetNode2 = finder.getTargetNodeOfVirtualNode( 1000 + 1 );
		assertNull("targetNode2 not found ", targetNode2);
	}
}

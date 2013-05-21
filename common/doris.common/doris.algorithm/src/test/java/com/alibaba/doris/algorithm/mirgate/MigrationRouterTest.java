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

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

/**
 * MigrationRouterTest
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-18
 */
public class MigrationRouterTest {

	/**
	 * Test method for {@link com.alibaba.doris.algorithm.mirgate.MigrationRouter#getMigrationPairOfPNode(int)}.
	 */
	@Test
	public void testGetMigrationPairOfPNode() {
		MigrationRouter migrationRouter = new MigrationRouter(10000, 3, 4);
		System.out.println(migrationRouter.getMigrationPairOfPNode(0));
		//delta pairs[[3:0-3], [9:1-3], [11:2-3]]
		
		List<List<MigrationPair>> allNodeMigrationPairs = migrationRouter.getAllNodeMigrationPairs();
		
		Assert.assertTrue( allNodeMigrationPairs.size() == 3);
		
		List<MigrationPair> pairs = allNodeMigrationPairs.get( 0 );
		
		for (MigrationPair pair : pairs) {
			if(pair.getVnode() == 3) {
				Assert.assertEquals(0, pair.getSource().intValue());
				Assert.assertEquals(3, pair.getTarget().intValue());
			}
			
			if(pair.getVnode() == 9) {
				Assert.assertEquals(1, pair.getSource().intValue());
				Assert.assertEquals(3, pair.getTarget().intValue());
			}
			
			if(pair.getVnode() == 11) {
				Assert.assertEquals(2, pair.getSource().intValue());
				Assert.assertEquals(3, pair.getTarget().intValue());
			}
		}
	}

}

/*
Copyright(C) 2010 Alibaba Group Holding Limited
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
package com.alibaba.doris.dataserver.migrator.task;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.alibaba.doris.common.route.MigrationRoutePair;

/**
 * @author Raymond He ( He Kun), raymond.he.kk@gmail.com
 * @since 1.0
 * 2011-6-23
 */
public class ProgressComputerTest {

	/**
	 * Test method for {@link com.alibaba.doris.dataserver.migrator.task.ProgressComputer#getProgress(java.lang.String)}.
	 */
	@Test
	public void testGetProgress() {
		
		List<MigrationRoutePair> migrationRoutePairs = new ArrayList<MigrationRoutePair>();
		
		int t = 3, v = 5 ;
		for (int i = 0; i < t; i++) {
			for (int j = 0; j < v; j++) {
				MigrationRoutePair pair = new MigrationRoutePair(j ,  "t"+i );
				migrationRoutePairs.add( pair ); 
			}
		}
		ProgressComputer progressComputer = new ProgressComputer(migrationRoutePairs);
		
		progressComputer.completeOneVNode( "t0", 0);		
		int progress = progressComputer.getProgressOfTarget("t0");		
		assertEquals("Progress=20", 20,progress);
		
		progressComputer.completeOneVNode( "t0",1);		
		progress = progressComputer.getProgressOfTarget("t0");		
		assertEquals("Progress=40", 40,progress);
		
		progressComputer.completeOneVNode( "t0",2);		
		progress = progressComputer.getProgressOfTarget("t0");		
		assertEquals("Progress=60", 60,progress);
		
		progressComputer.completeOneVNode( "t0",3);		
		progress = progressComputer.getProgressOfTarget("t0");		
		assertEquals("Progress=80", 80,progress);
		
		progressComputer.completeOneVNode( "t0",4);		
		progress = progressComputer.getProgressOfTarget("t0");		
		assertEquals("Progress=100", 100,progress);
	}
	
	/**
	 * Test method for {@link com.alibaba.doris.dataserver.migrator.task.ProgressComputer#getProgress(java.lang.String)}.
	 */
	@Test
	public void testGetGrossProgress() {
		
		List<MigrationRoutePair> migrationRoutePairs = new ArrayList<MigrationRoutePair>();
		
		int t = 1, v = 10000 ;
		for (int i = 0; i < t; i++) {
			for (int j = 0; j < v; j++) {
				MigrationRoutePair pair = new MigrationRoutePair(j ,  "t"+i );
				migrationRoutePairs.add( pair ); 
			}
		}
		ProgressComputer progressComputer = new ProgressComputer(migrationRoutePairs);
		
		int lastProgress = -1;
		for (int i = 0; i < v ; i++) {
			boolean need = progressComputer.completeOneVNode( "t0", 0);		
			
			int progress = progressComputer.getGrossProgress();
			
//			System.out.println("progress:" + progress +",  " + need +", " + progressComputer.getFinishCount());
			lastProgress = progress;
		}
		
		assertEquals("Progress=100", 100,lastProgress);
	}
}

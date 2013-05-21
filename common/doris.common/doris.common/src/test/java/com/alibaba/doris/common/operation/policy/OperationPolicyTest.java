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
package com.alibaba.doris.common.operation.policy;

import junit.framework.Assert;

import org.junit.Test;

import com.alibaba.doris.common.operation.OperationPolicy;

/**
 * OperationPolicyTest
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-17
 */
public class OperationPolicyTest {

	/**
	 * Test method for {@link com.alibaba.doris.common.operation.OperationPolicy#OperationPolicy(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testOperationPolicy() {
		String policyString = "2,2,1";
		String namespace = "Product";
		
		OperationPolicy policy = new OperationPolicy(namespace, policyString);
		
		Assert.assertEquals( namespace , policy.getNamespace()); 
		Assert.assertEquals( 2 , policy.getCopyCount());
		Assert.assertEquals( 2 , policy.getWriteCount());
		Assert.assertEquals( 1 , policy.getReadCount());		
	}
	
	@Test
	public void testEmptyOperationPolicy() {
		String policyString = null;
		String namespace = "Product";
		
		try {
			OperationPolicy policy = new OperationPolicy(namespace, policyString);
			
			Assert.fail("Empty policy stirng should fail to parse!");
		}catch(Throwable t) {
			
		}
	}
	
	@Test
	public void testBlankOperationPolicy() {
		String policyString = " ";
		String namespace = "Product";
		
		try {
			OperationPolicy policy = new OperationPolicy(namespace, policyString);
			
			Assert.fail("Blank policy stirng should fail to parse!");
		}catch(Throwable t) {
			
		}
	}
	
	@Test
	public void testInvalidOperationPolicy() {
		String policyString = "AB,C ";
		String namespace = "Product";
		
		try {
			OperationPolicy policy = new OperationPolicy(namespace, policyString);
			
			Assert.fail("Invalid policy stirng should fail to parse!");
		}catch(Throwable t) {
		}
	}
}

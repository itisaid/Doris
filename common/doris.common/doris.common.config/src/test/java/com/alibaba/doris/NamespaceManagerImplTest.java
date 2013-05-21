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
package com.alibaba.doris;

import org.junit.Test;

/**
 * NamespaceManagerImplTest
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-17
 */
public class NamespaceManagerImplTest {
	
	protected String namespaceJsonString = "[{\"compress\":true,\"compressThreshold\":2048,\"compressType\":\"gzip\",\"name\":\"Product\",\"policy\":\"2,2,1\"},{\"compress\":true,\"compressThreshold\":2048,\"compressType\":\"gzip\",\"name\":\"Order\",\"policy\":\"2,2,1\"}]";

	/**
	 * Test method for {@link com.alibaba.doris.common.NamespaceManagerImpl#initConfig()}.
	 */
	@Test
	public void testInitConfig() {
	    //XXU reopen this case later
//		NamespaceManager manager = new NamespaceManagerImpl();
//		
//		ConfigManager configManager = new MockConfigManager() {
//			
//			public String getConfig() {
//				return namespaceJsonString;
//			}
//		};
//		
//		manager.setConfigManager(configManager);
//		manager.initConfig();
//		
//		Namespace namespace = manager.getNamespace("Product");
//		
//		Assert.assertNotNull( namespace); 
//		Assert.assertEquals("Product", namespace.getName());
//		
//		Assert.assertEquals( 2048, namespace.getCompressThreshold() );
		
	}

}

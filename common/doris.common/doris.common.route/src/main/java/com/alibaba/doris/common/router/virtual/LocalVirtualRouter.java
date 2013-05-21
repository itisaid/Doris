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
package com.alibaba.doris.common.router.virtual;

import com.alibaba.doris.algorithm.RouteAlgorithm;
import com.alibaba.doris.algorithm.vpm.VpmRouterAlgorithm;
import com.alibaba.doris.common.route.VirtualRouter;

/**
 * LocalVirtualRouter
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-6-16
 */
public class LocalVirtualRouter implements VirtualRouter {
	
	protected int virtualNum = 100;  //default.
	
	protected RouteAlgorithm algorithm;
	
	public LocalVirtualRouter () {
		this( 10 );
	}
	
	public LocalVirtualRouter(int virtualNum) {
		this.virtualNum = virtualNum;
		algorithm = new VpmRouterAlgorithm(1, virtualNum);
	}
	
	public RouteAlgorithm getAlgorithm() {
		return algorithm;
	}
	
	public int getVirtualNum() {
		return virtualNum;
	}
	public int findVirtualNode(String key) {
		return algorithm.getVirtualByKey(key);
	}
}

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
package com.alibaba.doris.common.event.impl;

import com.alibaba.doris.common.event.RouteConfigChangeEvent;
import com.alibaba.doris.common.event.RouteConfigListener;


/**
 * DefaultConfigListener
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-25
 */
public class DefaultConfigListener implements RouteConfigListener {
	
	/* (non-Javadoc)
	 * @see com.alibaba.doris.framework.operation.event.ConfigListener#onConfigChange(com.alibaba.doris.framework.operation.event.ConfigChangeEvent)
	 */
	public void onConfigChange(RouteConfigChangeEvent event) {
		
	}

}

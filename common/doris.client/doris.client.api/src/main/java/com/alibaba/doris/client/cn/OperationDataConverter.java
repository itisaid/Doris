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
package com.alibaba.doris.client.cn;

import java.util.List;

import com.alibaba.doris.client.operation.OperationData;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.route.VirtualRouter;

/**
 * OperationDataConverter.
 * 
 * Convert between app key and communication key.
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-5
 */
public interface OperationDataConverter {
	
	Key buildKey( OperationData operationData, long routeVersion );
	
	Value buildValue( OperationData operationData );
	
	Key buildKeys( OperationData operationData , long routeVersion);
	
	Value buildValues( OperationData operationData );
	
	Object unbuildKey( OperationData operationData );
	
	Object unbuildValue( Value value );
	
	Object unbuildKeys( OperationData operationData );
	
	Object unbuildValues( List<Value> values);

	void setVirtualRouter(VirtualRouter virtualRouter);
	
	VirtualRouter getVirtualRouter();
}

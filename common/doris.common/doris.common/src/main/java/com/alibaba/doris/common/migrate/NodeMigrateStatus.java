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
package com.alibaba.doris.common.migrate;

/**
 * NodeMigrateStatus.
 * <p/>
 * 迁移状态枚举.( DataServer 视角)。有三个枚举值:<p/>
 * <li> 正常 - 0 - 未发生迁移
 * <li> 迁移中 - 1- 正在执行迁移, 
 * <li> 节点迁移完毕 - 2 - 在一个集群迁移任务中，本Node迁移执行完毕。
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-25
 */
public enum NodeMigrateStatus {
	
	NORMAL(0),
	
	MIGRATING(1),
	
	MIGRATE_NODE_FINISHED(2),
	
	MIGRATE_ALL_FINISHED(3),
	
	DATACLEANING(4),
	
	DATACLEAN_FINISH(5),
	
	CANCELLING( -1 ),
	
	CANCELLED( -2 );	
	
	private int value;
	
	private NodeMigrateStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}	
}

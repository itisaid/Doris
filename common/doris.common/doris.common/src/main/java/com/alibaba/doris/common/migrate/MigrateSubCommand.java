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

import java.util.HashMap;
import java.util.Map;

import com.alibaba.doris.common.MigrateTypeEnum;

/**
 * MigrateSubCommand
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-27
 */
public enum MigrateSubCommand {
	
	/**
	 * migrate subcommand
	 */
	EXPANSION_START("expansion_start"),
	
	TEMP_FAILOVER_START("temp_failover_start"),
	
	FOREVER_FAILOVER_START("forever_failover_start"),
	
	/**
	 * mock 测试专用
	 */
	MOCK_MIGRATE_START("mock_migrate_start"),
	
	/**
	 * all_finished subcommand
	 */
	EXPANSION_ALL_FINISHED("expansion_all_finished"),
	
	TEMP_FAILOVER_ALL_FINISHED("temp_failover_all_finished"),
	
	FOREVER_FAILOVER_ALL_FINISHED("forever_failover_all_finished"),
	
	/**
	 * cancel subcommand
	 */
	EXPANSION_CANCEL("expansion_cancel") ,
	
	TEMP_FAILOVER_CANCEL("temp_failover_cancel"),
	
	FOREVER_FAILOVER_CANCEL("forever_failover_cancel"),
	
	/**
	 * 查询节点的迁移执行状态
	 */
	QUERY_STATUS("query_status"),
	
	START("start"),
	
	ALL_FINISHED("all_finished"),
	
	CANCEL("cancel"),
	
	DATACLEAN("dataclean"); 
	
	private String value;
	
	private static Map<String,MigrateSubCommand> subCommandMap = new HashMap<String,MigrateSubCommand>();
	
	static  {
		subCommandMap.put(EXPANSION_START.getValue(), EXPANSION_START);
		subCommandMap.put(TEMP_FAILOVER_START.getValue(), TEMP_FAILOVER_START);
		subCommandMap.put(FOREVER_FAILOVER_START.getValue(), FOREVER_FAILOVER_START);
		
		subCommandMap.put(EXPANSION_ALL_FINISHED.getValue(), EXPANSION_ALL_FINISHED);
		subCommandMap.put(TEMP_FAILOVER_ALL_FINISHED.getValue(), TEMP_FAILOVER_ALL_FINISHED);
		subCommandMap.put(FOREVER_FAILOVER_ALL_FINISHED.getValue(), FOREVER_FAILOVER_ALL_FINISHED);
		
		subCommandMap.put(EXPANSION_CANCEL.getValue(), EXPANSION_CANCEL);
		subCommandMap.put(TEMP_FAILOVER_CANCEL.getValue(), TEMP_FAILOVER_CANCEL);
		subCommandMap.put(FOREVER_FAILOVER_CANCEL.getValue(), FOREVER_FAILOVER_CANCEL);
		
		subCommandMap.put(QUERY_STATUS.getValue(), QUERY_STATUS);
		
		subCommandMap.put( DATACLEAN.getValue(), DATACLEAN );
	}
	
	private MigrateSubCommand(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	/**
	 * 根据子命令判断它属于的迁移类型
	 * @param subCommand
	 * @return
	 */
	public MigrateTypeEnum getMigrateType() {
		if( value.startsWith("expansion")) {
			return MigrateTypeEnum.EXPANSION;
		}else if( value.startsWith("forever_failover")) {
			return MigrateTypeEnum.FOREVER_FAILOVER;
		}else {
			return MigrateTypeEnum.TEMP_FAILOVER;
		}
	}
	
	public static MigrateSubCommand enumValueOf(String strCommand) {
		if(strCommand != null) {
			return subCommandMap.get( strCommand.toLowerCase() );
		}else {
			return null;
		}
	}
}

package com.alibaba.doris.common;
/**
 * 迁移类型
 * @author frank
 *
 */
public enum MigrateTypeEnum {

    TEMP_FAILOVER("temp_failover",0), 
    
    FOREVER_FAILOVER("forever_failover",1), 
    
    EXPANSION("expansion",2),
    
    DATACLEAN("dataclean", 9),
    
    NONE("none",-1);
    
    private String value;
	private int priority;

    private MigrateTypeEnum(String value,int priority) {
        this.value = value;
        this.priority = priority;
    }

    public String getValue() {
        return value;
    }
    
    public int getPriority() {
		return priority;
	}
}

package com.alibaba.doris.common;

/**
 * 迁移状态
 * 
 * @author frank
 */
public enum MigrateStatusEnum {
    /**
     * 迁移完成
     */
    FINISH("finish"),
    /**
     * 迁移中
     */
    MIGERATING("migrating"),
    /**
     * 迁移准备，admin尚没有向node server发迁移命令
     */
    PREPARE("prepare"),
    /**
     * 命令发送失败，data server没有执行迁移命令
     */
    COMMAND_FAIL("command fail"),

    /**
     * 迁移过程发生错误
     */
    MIGERATE_ERROR("migrate error"),
    /**
     * 配置变更，迁移结果生效
     */
    EFFECT("effect") ,
    
    /**
     * 数据清理
     */
	DATACLEANING("datacleaning"),
	
	/**
	 * 数据清理完毕
	 */
	DATACLEAN_FINISH("dataclean_finish"),
	
	/**
	 * 数据清理失败
	 */
	DATACLEAN_ERROR("dataclean_error");

    private String value;

    private MigrateStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MigrateStatusEnum getEnum(String value) {
        for (MigrateStatusEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}

package com.alibaba.doris.common;

/**
 * Data Server和Admin Server专用的节点状态 NodeStatus
 * 
 * @since 1.0 2011-5-18
 */
public enum NodeStatus {

    /**
     * 正常状态
     */
    OK(1),

    /**
     * 临时失效，如发布、网络不稳定等情况下
     */
    TEMP_FAILED(2),

    /**
     * 临时失效恢复，从临时失效状态恢复正常
     */
    TEMP_FAILED_RESOLVE(3),

    /**
     * 永久失效，如机器故障等
     */
    FOROVER_FAILED(4),

    /**
     * 永久失效恢复，如备用机切换上去
     */
    FOROVER_FAILED_RESOLVE(5),

    /**
     * 迁移中
     */
    MIGRATING(6),

    /**
     * @deprecated
     */
    INCRE_MIGERATE_PREPARE(7),

    /**
     * @deprecated
     */
    INCRE_MIGERATE_START(8);

    private int value;

    private NodeStatus(int value) {
        this.value = value;
    }

    /** 返回常量字符串 */
    public int getValue() {
        return value;
    }

    /** 根据字符串得到Enum类型，区分大小写，如果没有匹配成功则返回null */
    public static NodeStatus getTypeByValue(int value) {
        for (NodeStatus type : values()) {
            if (type.getValue()== value) {
                return type;
            }
        }
        return null;
    }
}

package com.alibaba.doris.common;

/**
 * 路由专用Node状态
 * 
 * @project :Doris
 * @author : len.liu
 * @datetime : 2011-5-20 上午10:31:00
 * @version :0.1
 * @Modification:
 */
public enum NodeRouteStatus {

	NO_ROUTE(0),
    /**
     * 正常状态
     */
    OK(1),

    /**
     * 临时失效，如发布、网络不稳定等情况下
     */
    TEMP_FAILED(2);

    private int value;

    private NodeRouteStatus(int value) {
        this.value = value;
    }

    /** 返回常量字符串 */
    public int getValue() {
        return value;
    }

    /** 根据字符串得到Enum类型，区分大小写，如果没有匹配成功则返回null */
    public static NodeRouteStatus getTypeByValue(int value) {
        for (NodeRouteStatus type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return NO_ROUTE;
    }

}

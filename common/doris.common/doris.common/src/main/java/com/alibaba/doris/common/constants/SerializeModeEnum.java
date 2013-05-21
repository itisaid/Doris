package com.alibaba.doris.common.constants;

/**
 * @project :Doris
 * @author : len.liu
 * @datetime : 2011-5-23 下午10:09:31
 * @version :0.1
 * @Modification:
 */
public enum SerializeModeEnum {
    JSON("json");

    private String value;

    private SerializeModeEnum(String value) {
        this.value = value;
    }

    /** 返回常量字符串 */
    public String getValue() {
        return value;
    }

    /** 根据字符串得到Enum类型，区分大小写，如果没有匹配成功则返回null */
    public static SerializeModeEnum getTypeByValue(int value) {
        for (SerializeModeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}

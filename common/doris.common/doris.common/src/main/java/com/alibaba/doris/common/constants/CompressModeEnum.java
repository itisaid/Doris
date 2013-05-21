package com.alibaba.doris.common.constants;

/**
 * @project :Doris
 * @author : len.liu
 * @datetime : 2011-5-23 下午10:09:41
 * @version :0.1
 * @Modification:
 */
public enum CompressModeEnum {
    ZIP("zip");

    private String value;

    private CompressModeEnum(String value) {
        this.value = value;
    }

    /** 返回常量字符串 */
    public String getValue() {
        return value;
    }

    /** 根据字符串得到Enum类型，区分大小写，如果没有匹配成功则返回null */
    public static CompressModeEnum getTypeByValue(int value) {
        for (CompressModeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}

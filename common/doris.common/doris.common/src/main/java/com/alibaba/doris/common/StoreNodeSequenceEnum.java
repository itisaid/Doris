package com.alibaba.doris.common;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * 定义Doris集群中的序列类型
 * 
 * @project :Doris
 * @author : len.liu
 * @datetime : 2011-4-27 下午06:58:01
 * @version : 0.1
 * @Modification:
 */
public enum StoreNodeSequenceEnum {

    /**
     * 正常序列：进行数据存储的正常节点（sequence编号1,2,3...,8）
     */
    NORMAL_SEQUENCE_1(1, "Sequence 1", true), NORMAL_SEQUENCE_2(2, "Sequence 2", true), NORMAL_SEQUENCE_3(3,
                                                                                                          "Sequence 3",
                                                                                                          false),
    NORMAL_SEQUENCE_4(4, "Sequence 4", false),

    /**
     * 临时序列：当有正常节点临时失效的时候，数据写到临时节点（历史上也别称作增量节点、备份节点，sequence编号0）
     */
    TEMP_SEQUENCE(0, "临时序列", true),

    /**
     * 备用序列：当有节点永久失效的时候，使用备用节点替代该永久失效节点（sequence编号9）
     */
    STANDBY_SEQUENCE(9, "备用序列", true),

    /**
     * 待用序列：序列中为新加入的节点，待分配
     */
    UNUSE_SEQUENCE(-1, "待用序列", true);

    private int     value;

    private String  name;

    private boolean isValid;

    private StoreNodeSequenceEnum(int value, String name, boolean isValid) {
        this.value = value;
        this.name = name;
        this.isValid = isValid;
    }

    /** 返回常量字符串 */
    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public boolean isValid() {
        return isValid;
    }

    /** 根据字符串得到Enum类型，区分大小写，如果没有匹配成功则返回null */
    public static StoreNodeSequenceEnum getTypeByValue(int value) {
        for (StoreNodeSequenceEnum type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }

    public static boolean isNormalSequence(StoreNodeSequenceEnum sequence) {
        return !StoreNodeSequenceEnum.UNUSE_SEQUENCE.equals(sequence)
               && !StoreNodeSequenceEnum.TEMP_SEQUENCE.equals(sequence)
               && !StoreNodeSequenceEnum.STANDBY_SEQUENCE.equals(sequence);
    }

    public static boolean isNormalSequence(String sequence) {
        if (StringUtils.isBlank(sequence) || !NumberUtils.isNumber(sequence)) {
            return false;
        }
        StoreNodeSequenceEnum sequenceEnum = StoreNodeSequenceEnum.getTypeByValue(NumberUtils.toInt(sequence));
        if (sequenceEnum == null) return false;
        return isNormalSequence(sequenceEnum);
    }

}

package com.alibaba.doris.admin.web.configer.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.doris.admin.service.expansion.processor.ExpansionMigrateProcessor;
import com.alibaba.doris.common.StoreNodeSequenceEnum;

/**
 * @project :Doris
 * @author : len.liu
 * @datetime : 2011-5-26 下午11:26:53
 * @version :0.1
 * @Modification:
 */
public class SequenceUtil {

    static ExpansionMigrateProcessor expansionMigrateProcessor = ExpansionMigrateProcessor.getInstance();

    public static Map<Integer, String> getNoMigrateSequenceMap() {

        Map<Integer, String> sequenceMap = new LinkedHashMap<Integer, String>();
        for (StoreNodeSequenceEnum sequenceEnum : StoreNodeSequenceEnum.values()) {
            // 检查所有序列，如果某个序列正在迁移中，则屏蔽显示(场景为显示新增Node的迁移序列)
            boolean isMigrating = expansionMigrateProcessor.isMigrating(sequenceEnum);
            if (!isMigrating && sequenceEnum.isValid()) {
                sequenceMap.put(sequenceEnum.getValue(), sequenceEnum.getName());
            }
        }
        return sequenceMap;
    }

    public static Map<Integer, String> getAllSequenceMap() {

        Map<Integer, String> sequenceMap = new LinkedHashMap<Integer, String>();
        for (StoreNodeSequenceEnum sequenceEnum : StoreNodeSequenceEnum.values()) {
            sequenceMap.put(sequenceEnum.getValue(), sequenceEnum.getName());
        }
        return sequenceMap;
    }

    public static Map<Integer, Boolean> getMigrateSequenceMap() {
        Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
        ExpansionMigrateProcessor expansionMigrateProcessor = ExpansionMigrateProcessor.getInstance();
        for (StoreNodeSequenceEnum sequence : StoreNodeSequenceEnum.values()) {
            map.put(sequence.getValue(), expansionMigrateProcessor.isMigrating(sequence));
        }
        return map;

    }

}

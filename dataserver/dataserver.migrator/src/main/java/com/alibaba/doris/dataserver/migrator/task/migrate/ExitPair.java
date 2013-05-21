package com.alibaba.doris.dataserver.migrator.task.migrate;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;

/**
 * 退出线程标记。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ExitPair implements Pair {

    public Key getKey() {
        return null;
    }

    public Value getValue() {
        return null;
    }

}

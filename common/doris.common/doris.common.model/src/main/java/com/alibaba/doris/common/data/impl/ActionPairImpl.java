package com.alibaba.doris.common.data.impl;

import com.alibaba.doris.common.data.ActionPair;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ActionPairImpl extends PairImpl implements ActionPair {

    public ActionPairImpl(Type type, Key key, Value value) {
        super(key, value);
        this.type = type;
    }

    public Type getActionType() {
        return type;
    }

    private Type type;
}

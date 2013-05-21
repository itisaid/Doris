package com.alibaba.doris.common.data;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface ActionPair extends Pair {

    public Type getActionType();

    public enum Type {
        SET, DELETE
    }
}

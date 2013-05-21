package com.alibaba.doris.dataserver.action.data;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class CompareAndSetActionData extends CommonActionData {

    public CompareAndSetActionData() {
        super(BaseActionType.CAS);
    }

    @Override
    public boolean isCas() {
        return true;
    }
}

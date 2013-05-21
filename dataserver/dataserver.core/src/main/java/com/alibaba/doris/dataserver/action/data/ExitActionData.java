package com.alibaba.doris.dataserver.action.data;

import com.alibaba.doris.dataserver.action.ActionType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ExitActionData implements ActionData {

    public ActionType getActionType() {
        return BaseActionType.EXIT;
    }

    @Override
    public String toString() {
        return getActionType().toString();
    }

}

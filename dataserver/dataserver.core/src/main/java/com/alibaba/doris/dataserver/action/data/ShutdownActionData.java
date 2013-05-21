package com.alibaba.doris.dataserver.action.data;

import com.alibaba.doris.dataserver.action.ActionType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ShutdownActionData implements ActionData {

    public ActionType getActionType() {
        return BaseActionType.SHUTDOWN;
    }

    @Override
    public String toString() {
        return BaseActionType.SHUTDOWN.toString();
    }

}

package com.alibaba.doris.dataserver.action.data;

import com.alibaba.doris.dataserver.action.ActionType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class SimpleActionData extends BaseActionData {

    public SimpleActionData(ActionType actionType) {
        this.actionType = actionType;
    }

    public ActionType getActionType() {
        return actionType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append(actionType.toString());
        sb.append(" {");
        super.generateInfomation(sb);
        sb.append("}");
        return sb.toString();
    }

    private ActionType actionType;
}

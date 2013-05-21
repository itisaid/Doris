package com.alibaba.doris.dataserver.action.data;

import com.alibaba.doris.dataserver.action.ActionType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class CheckActionData implements ActionData {

    public ActionType getActionType() {
        return BaseActionType.CHECK;
    }

    public CheckType getCheckType() {
        return checkType;
    }

    public void setCheckType(CheckType checkType) {
        this.checkType = checkType;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(64);

        sb.append(BaseActionType.CHECK);

        sb.append("{");
        sb.append("[status=");
        sb.append(isSuccess);
        sb.append("]");

        if (null != message) {
            sb.append("[message=");
            sb.append(message);
            sb.append("]");
        }

        sb.append("}");
        return sb.toString();
    }

    private boolean   isSuccess;
    private String    message;
    private CheckType checkType;

    public enum CheckType {
        CHECK_STANDBY_NODE("check_standby_node"),
        CHECK_NORMAL_NODE("check_normal_node"),
        CHECK_TEMP_NODE("check_temp_node");

        private CheckType(String type) {
            this.type = type;
        }

        public static CheckType valueOfType(String type) {
            if (CHECK_NORMAL_NODE.type.equals(type)) {
                return CHECK_NORMAL_NODE;
            }

            if (CHECK_TEMP_NODE.type.equals(type)) {
                return CHECK_TEMP_NODE;
            }
            
            if (CHECK_STANDBY_NODE.type.equals(type)) {
                return CHECK_STANDBY_NODE;
            }

            throw new IllegalArgumentException("Unknown checkType:" + type);
        }

        private String type;
    }
}

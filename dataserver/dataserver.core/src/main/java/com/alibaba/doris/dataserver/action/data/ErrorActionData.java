package com.alibaba.doris.dataserver.action.data;

import com.alibaba.doris.dataserver.action.ActionType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ErrorActionData implements ActionData {

    public ErrorActionData(int code) {
        this.code = code;
    }

    public ActionType getActionType() {
        return BaseActionType.ERROR;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        sb.append(getActionType().toString());
        sb.append(" {");
        switch (getCode()) {
            /*
             * case 0: sb.append("CLIENT_ERROR"); break; case 1: sb.append("SERVER_ERROR"); break; case 2:
             * sb.append("UNKNOWN_COMMAND"); break;
             */
            case 3:
                sb.append("VERSION_OUT_OF_DATE");
                break;
            default:
                break;
        }

        if (getCode() > 0) {
            sb.append("[Code=");
            sb.append(getCode());
            sb.append("]");
        }

        if (null != errorMessage) {
            sb.append("[Error message=");
            sb.append(errorMessage);
            sb.append("]");
        }

        sb.append("}");
        return sb.toString();
    }

    private int             code;
    private String          errorMessage;
    public static final int CLIENT_ERROR        = 0;
    public static final int SERVER_ERROR        = 1;
    public static final int UNKNOWN_COMMAND     = 2;
    public static final int VERSION_OUT_OF_DATE = 3;
}

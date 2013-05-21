package com.alibaba.doris.dataserver.action.data;

import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.dataserver.action.ActionType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class CommonActionData extends SupportBodyActionData {

    public CommonActionData(ActionType actionType) {
        this.actionType = actionType;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public short getFlag() {
        return flag;
    }

    public void setFlag(short flag) {
        this.flag = flag;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isNoreply() {
        return noreply;
    }

    public void setNoreply(boolean noreply) {
        this.noreply = noreply;
    }

    @Override
    public String toString() {
        if (null != value) {
            byte[] bodyBytes = value.getValueBytes();
            this.setBodyByteArray(bodyBytes);
            this.setBodyBytes(bodyBytes.length);
        }

        StringBuilder sb = new StringBuilder(128);
        sb.append(actionType.toString());
        sb.append(" {");

        super.generateInfomation(sb);

        if (flag > 0) {
            sb.append("[Flag=");
            sb.append(flag);
            sb.append("]");
        }

        if (timestamp > 0) {
            sb.append("[Timestamp=");
            sb.append(timestamp);
            sb.append("]");
        }

        if (noreply) {
            sb.append("[noreply]");
        }

        sb.append("}");
        return sb.toString();
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    private short      flag;
    private long       timestamp;
    private boolean    noreply = false;
    private ActionType actionType;
    private Value      value;
}

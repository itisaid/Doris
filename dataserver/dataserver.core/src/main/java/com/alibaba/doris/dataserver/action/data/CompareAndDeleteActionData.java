package com.alibaba.doris.dataserver.action.data;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class CompareAndDeleteActionData extends SimpleActionData {

    public CompareAndDeleteActionData() {
        super(BaseActionType.CAD);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean isCas() {
        return true;
    }

    public boolean isDeleteFailed() {
        return isDeleteFailed;
    }

    public void setDeleteFailed(boolean isDeleteFailed) {
        this.isDeleteFailed = isDeleteFailed;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append(getActionType().toString());
        sb.append(" {");
        super.generateInfomation(sb);

        sb.append("[Timestamp=");
        sb.append(timestamp);
        sb.append("]");

        sb.append("[isDeleteFailed=");
        sb.append(isDeleteFailed);
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }

    private long    timestamp;
    private boolean isDeleteFailed = false;
}

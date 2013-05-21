package com.alibaba.doris.dataserver.action.data;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public abstract class SupportBodyActionData extends BaseActionData {

    public byte[] getBodyByteArray() {
        return bodyByteArray;
    }

    public void setBodyByteArray(byte[] bodyByteArray) {
        this.bodyByteArray = bodyByteArray;
    }

    public int getBodyBytes() {
        return bodyBytes;
    }

    public void setBodyBytes(int bodyBytes) {
        this.bodyBytes = bodyBytes;
    }

    public boolean isNeedReadBody() {
        return needReadBody;
    }

    public void setNeedReadBody(boolean needReadBody) {
        this.needReadBody = needReadBody;
    }

    protected void generateInfomation(StringBuilder sb) {
        super.generateInfomation(sb);
        sb.append("[NeedReadBody=");
        sb.append(needReadBody);
        sb.append("]");

        sb.append("[BodyBytes=");
        sb.append(bodyBytes);
        sb.append("]");
    }

    private byte[]  bodyByteArray;
    private int     bodyBytes;
    private boolean needReadBody = false;
}

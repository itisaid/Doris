package com.alibaba.doris.dataserver.action.data;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.util.ByteUtils;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public abstract class BaseActionData implements ActionData {

    public byte[] getKeyBytes() {
        return keyBytes;
    }

    public void setKeyBytes(byte[] keyBytes) {
        this.keyBytes = keyBytes;
    }

    public int getVnode() {
        return vnode;
    }

    public void setVnode(int vnode) {
        this.vnode = vnode;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public long getRouteVersion() {
        return routeVersion;
    }

    public void setRouteVersion(long routeVersion) {
        this.routeVersion = routeVersion;
    }

    public boolean isCas() {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        generateInfomation(sb);
        return sb.toString();
    }

    protected void generateInfomation(StringBuilder sb) {
        if (null != getKeyBytes()) {
            sb.append("[Key=");
            sb.append(ByteUtils.byteToString(getKeyBytes()));
            sb.append("]");
        }

        long rVersion = getRouteVersion();
        if (rVersion > 0) {
            sb.append("[RouteVersion=");
            sb.append(rVersion);
            sb.append("]");
        }

        sb.append("[vnode=");
        sb.append(vnode);
        sb.append("]");

        sb.append("[isSuccess=");
        sb.append(isSuccess);
        sb.append("]");
    }

    private byte[]           keyBytes;
    private int              vnode                 = Key.DEFAULT_VNODE;
    private boolean          isSuccess             = false;
    private long             routeVersion          = INVALID_ROUTE_VERSION;
    public static final long INVALID_ROUTE_VERSION = -1;
}

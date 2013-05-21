package com.alibaba.doris.dataserver.action.parser;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.common.route.VirtualRouter;
import com.alibaba.doris.common.router.virtual.VirtualRouterImpl;
import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.data.BaseActionData;
import com.alibaba.doris.dataserver.action.data.SimpleActionData;
import com.alibaba.doris.dataserver.net.ByteBufferWrapper;
import com.alibaba.doris.dataserver.net.protocol.text.ProtocolConstant;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public abstract class BaseActionParser implements ActionParser {

    protected String parseNextField(byte[] line, int[] startPos) {
        int pos = startPos[0];

        if (pos >= line.length) {
            return null;
        }

        while (line[pos] != ProtocolConstant.SPACE && (line[pos] != ProtocolConstant.CRLF[0])) {
            pos++;
        }

        if (pos == startPos[0]) {
            if (line[pos] == ProtocolConstant.SPACE) {
                startPos[0]++;
            } else if (line[pos] == ProtocolConstant.CRLF[0]) {
                startPos[0] += 2;
            } else if (line[pos] == ProtocolConstant.CRLF[1]) {
                startPos[0]++;
            }
            return null;
        }

        int skipLen = 0;
        if (line[pos] == ProtocolConstant.SPACE) {
            // skip ' '
            pos++;
            skipLen = 1;
        } else if (line[pos] == ProtocolConstant.CRLF[0]) {
            // skip \r\n
            pos += 2;
            skipLen = 2;
        } else if (line[pos] == ProtocolConstant.CRLF[1]) {
            // skip \n
            pos++;
            skipLen = 1;
        }

        String value = ByteUtils.byteToString(line, startPos[0], (pos - startPos[0] - skipLen));
        startPos[0] = pos;
        return value;
    }

    public void readRouteVersion(BaseActionData actionData, byte[] header, int[] startPos) {
        if ((startPos[0] + ProtocolConstant.CRLF.length) < header.length) {
            String routeVersion = parseNextField(header, startPos);
            if (null != routeVersion) {
                actionData.setRouteVersion(Long.valueOf(routeVersion));
            }
        }
    }

    protected void readKeyBytes(BaseActionData actionData, byte[] header, int[] pos) {
        int startPos = pos[0];
        skipNextField(header, pos);
        byte[] keyBytes = new byte[pos[0] - startPos - 1];
        System.arraycopy(header, startPos, keyBytes, 0, keyBytes.length);
        actionData.setKeyBytes(keyBytes);
    }

    protected void readVnode(BaseActionData actionData, byte[] header, int[] pos) {
        if (SimpleActionData.INVALID_ROUTE_VERSION != actionData.getRouteVersion()) {
            String vnodeStr = parseNextField(header, pos);
            if (null != vnodeStr) {
                actionData.setVnode(Integer.valueOf(vnodeStr));
            }
        }

        if (Key.DEFAULT_VNODE == actionData.getVnode()) {
            // Client没有传vnode过来，需要Server重新计算Vnode
            int vnode = router.findVirtualNode(ByteUtils.byteToString(actionData.getKeyBytes()));
            actionData.setVnode(vnode);
        }
    }

    public void writeBody(ByteBufferWrapper buffer, ActionData actionData) {
        ;
    }

    public void readBody(byte[] body, ActionData actionData) {
        ;
    }

    /**
     * 为了提升性能，new String会消耗大量CPU资源。
     * 
     * @param line
     * @param startPos
     */
    protected void skipNextField(byte[] line, int[] startPos) {
        int pos = startPos[0];

        while (line[pos] != ProtocolConstant.SPACE && line[pos] != ProtocolConstant.CRLF[0]) {
            pos++;
        }
        // skip ' '
        pos++;

        startPos[0] = pos;
    }

    private static final VirtualRouter router = VirtualRouterImpl.getInstance();
}

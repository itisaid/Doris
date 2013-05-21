package com.alibaba.doris.dataserver.action.parser;

import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.data.BaseActionType;
import com.alibaba.doris.dataserver.action.data.CompareAndDeleteActionData;
import com.alibaba.doris.dataserver.action.data.SimpleActionData;
import com.alibaba.doris.dataserver.net.ByteBufferWrapper;
import com.alibaba.doris.dataserver.net.protocol.text.ProtocolConstant;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DeleteActionParser extends BaseActionParser {

    public void writeHead(ByteBufferWrapper buffer, ActionData actionData) {
        SimpleActionData md = (SimpleActionData) actionData;
        if (md.isSuccess()) {
            buffer.writeBytes(ProtocolConstant.DELETED);
            buffer.writeBytes(ProtocolConstant.CRLF);
            return;
        } else {
            if (md instanceof CompareAndDeleteActionData) {
                if (((CompareAndDeleteActionData) md).isDeleteFailed()) {
                    buffer.writeBytes(ProtocolConstant.DELETE_FAILED);
                    buffer.writeBytes(ProtocolConstant.CRLF);
                    return;
                }
            }
            buffer.writeBytes(ProtocolConstant.NOT_FOUND);
            buffer.writeBytes(ProtocolConstant.CRLF);
        }
    }

    public ActionData readHead(byte[] header, int startPos) {
        SimpleActionData ad = new SimpleActionData(BaseActionType.DELETE);
        int[] pos = { startPos };
        readKeyBytes(ad, header, pos);
        readRouteVersion(ad, header, pos);
        readVnode(ad, header, pos);
        return ad;
    }

}

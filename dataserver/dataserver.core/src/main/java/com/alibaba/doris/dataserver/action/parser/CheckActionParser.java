package com.alibaba.doris.dataserver.action.parser;

import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.data.CheckActionData;
import com.alibaba.doris.dataserver.action.data.CheckActionData.CheckType;
import com.alibaba.doris.dataserver.net.ByteBufferWrapper;
import com.alibaba.doris.dataserver.net.protocol.text.ProtocolConstant;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class CheckActionParser extends BaseActionParser {

    public ActionData readHead(byte[] header, int startPos) {
        CheckActionData data = new CheckActionData();

        // Whether it is the end of command or not.
        if (header.length > startPos && header[startPos] != ProtocolConstant.CRLF[1]) {
            int[] pos = { startPos };
            // decode the field of check message.
            String checkType = parseNextField(header, pos);
            if (null != checkType) {
                data.setCheckType(CheckType.valueOfType(checkType));
            }
        }

        return data;
    }

    public void writeHead(ByteBufferWrapper buffer, ActionData actionData) {
        CheckActionData ad = (CheckActionData) actionData;
        if (ad.isSuccess()) {
            buffer.writeBytes(TRUE);
            buffer.writeBytes(ProtocolConstant.CRLF);
            return;
        }

        buffer.writeBytes(FALSE);
        String message = ad.getMessage();
        buffer.writeByte(ProtocolConstant.SPACE);
        if (null != message) {
            buffer.writeBytes(ByteUtils.stringToByte(message));
        } else {
            buffer.writeBytes(NO_MESSAGE);
        }
        buffer.writeBytes(ProtocolConstant.CRLF);
    }

    private static final byte[] TRUE       = new byte[] { 't', 'r', 'u', 'e' };
    private static final byte[] FALSE      = new byte[] { 'f', 'a', 'l', 's', 'e' };
    private static final byte[] NO_MESSAGE = new byte[] { 'N', 'O', '_', 'M', 'E', 'S', 'S', 'A', 'G', 'E' };
}

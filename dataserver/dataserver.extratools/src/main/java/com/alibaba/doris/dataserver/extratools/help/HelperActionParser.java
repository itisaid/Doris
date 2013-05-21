package com.alibaba.doris.dataserver.extratools.help;

import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.parser.BaseActionParser;
import com.alibaba.doris.dataserver.net.ByteBufferWrapper;
import com.alibaba.doris.dataserver.net.protocol.text.ProtocolConstant;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class HelperActionParser extends BaseActionParser {

    public ActionData readHead(byte[] header, int startPos) {
        int[] pos = new int[] { startPos };

        String next = parseNextField(header, pos);
        if (null != next) {
            return new HelperActionData(next);
        }

        return new HelperActionData();
    }

    public void writeHead(ByteBufferWrapper buffer, ActionData actionData) {
        HelperActionData helperData = (HelperActionData) actionData;

        String message = helperData.getMessage();
        if (null != message) {
            buffer.writeBytes(ByteUtils.stringToByte(message));
        }

        buffer.writeBytes(ProtocolConstant.CRLF);
    }

}

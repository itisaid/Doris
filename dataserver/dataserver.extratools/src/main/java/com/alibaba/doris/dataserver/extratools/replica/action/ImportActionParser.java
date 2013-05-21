package com.alibaba.doris.dataserver.extratools.replica.action;

import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.net.ByteBufferWrapper;
import com.alibaba.doris.dataserver.net.protocol.text.ProtocolConstant;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ImportActionParser extends ExportActionParser {

    @Override
    protected ActionData generateActionData() {
        return new ImportActionData();
    }

    public void writeHead(ByteBufferWrapper buffer, ActionData actionData) {
        ImportActionData exportActionData = (ImportActionData) actionData;

        String message = exportActionData.getMessage();
        if (null != message) {
            buffer.writeBytes(ByteUtils.stringToByte(message));
        }

        buffer.writeBytes(ProtocolConstant.CRLF);
    }

}

package com.alibaba.doris.dataserver.action.parser;

import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.data.ErrorActionData;
import com.alibaba.doris.dataserver.net.ByteBufferWrapper;
import com.alibaba.doris.dataserver.net.protocol.text.ProtocolConstant;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ErrorActionParser extends BaseActionParser {

    public void writeHead(ByteBufferWrapper buffer, ActionData actionData) {
        ErrorActionData md = (ErrorActionData) actionData;
        switch (md.getCode()) {
            case ErrorActionData.CLIENT_ERROR: {
                buffer.writeBytes(ProtocolConstant.CLIENT_ERROR);
                String errorMessage = md.getErrorMessage();
                buffer.writeByte(ProtocolConstant.SPACE);
                if (null != errorMessage) {
                    buffer.writeBytes(ByteUtils.stringToByte(errorMessage));
                } else {
                    buffer.writeBytes(ProtocolConstant.ERROR);
                }
                buffer.writeBytes(ProtocolConstant.CRLF);
                break;
            }
            case ErrorActionData.VERSION_OUT_OF_DATE: {
                buffer.writeBytes(ProtocolConstant.VERSION_OUT_OF_DATE);
                buffer.writeByte(ProtocolConstant.SPACE);
                String errorMessage = md.getErrorMessage();
                if (null != errorMessage) {
                    buffer.writeBytes(ByteUtils.stringToByte(errorMessage));
                }
                buffer.writeBytes(ProtocolConstant.CRLF);
                break;
            }
            case ErrorActionData.SERVER_ERROR: {
                buffer.writeBytes(ProtocolConstant.SERVER_ERROR);
                String errorMessage = md.getErrorMessage();
                buffer.writeByte(ProtocolConstant.SPACE);
                if (null != errorMessage) {
                    buffer.writeBytes(ByteUtils.stringToByte(errorMessage));
                } else {
                    buffer.writeBytes(ProtocolConstant.ERROR);
                }
                buffer.writeBytes(ProtocolConstant.CRLF);
                break;
            }
            case ErrorActionData.UNKNOWN_COMMAND: {
                buffer.writeBytes(ProtocolConstant.ERROR);
                buffer.writeByte(ProtocolConstant.SPACE);
                if (md.getErrorMessage() != null) {
                    buffer.writeBytes(ByteUtils.stringToByte(md.getErrorMessage()));
                }
                buffer.writeBytes(ProtocolConstant.CRLF);
                break;
            }
            default: {
                buffer.writeBytes(ProtocolConstant.ERROR);
                buffer.writeBytes(ProtocolConstant.CRLF);
            }
        }
    }

    public ActionData readHead(byte[] header, int startPos) {
        // Error action data是内部产生的数据，无需从命令行解析数据。此方法永远不会被调用。
        return null;
    }

}

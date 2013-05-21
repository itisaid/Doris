package com.alibaba.doris.dataserver.action.parser;

import com.alibaba.doris.common.data.ByteWrapper;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.ByteWrapperValueImpl;
import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.data.BaseActionType;
import com.alibaba.doris.dataserver.action.data.CommonActionData;
import com.alibaba.doris.dataserver.net.ByteBufferWrapper;
import com.alibaba.doris.dataserver.net.protocol.text.ProtocolConstant;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class GetActionParser extends BaseActionParser {

    public void writeHead(ByteBufferWrapper buffer, ActionData actionData) {
        CommonActionData md = (CommonActionData) actionData;
        if (md.isSuccess()) {
            Value value = md.getValue();

            buffer.writeBytes(ProtocolConstant.VALUE);
            buffer.writeByte(ProtocolConstant.SPACE);
            buffer.writeBytes(md.getKeyBytes());
            buffer.writeByte(ProtocolConstant.SPACE);
            buffer.writeBytes(ByteUtils.stringToByte(Short.toString(value.getFlag())));
            buffer.writeByte(ProtocolConstant.SPACE);
            buffer.writeBytes(ByteUtils.stringToByte(Integer.toString((md.getVnode()))));
            buffer.writeByte(ProtocolConstant.SPACE);
            buffer.writeBytes(ByteUtils.stringToByte(Long.toString(value.getTimestamp())));
            buffer.writeByte(ProtocolConstant.SPACE);

            if (value instanceof ByteWrapperValueImpl) {
                ByteWrapperValueImpl wrapperedValue = (ByteWrapperValueImpl) value;
                ByteWrapper byteWrapper = wrapperedValue.getValueByteWrapper();
                buffer.writeBytes(ByteUtils.stringToByte(Integer.toString(byteWrapper.getLen())));
                buffer.writeBytes(ProtocolConstant.CRLF);
                buffer.writeBytes(byteWrapper.getBytes(), byteWrapper.getStartPos(), byteWrapper.getLen());
                buffer.writeBytes(ProtocolConstant.CRLF);
            } else {
                byte[] valueBytes = value.getValueBytes();
                buffer.writeBytes(ByteUtils.stringToByte(Integer.toString(valueBytes.length)));
                buffer.writeBytes(ProtocolConstant.CRLF);
                buffer.writeBytes(valueBytes);
                buffer.writeBytes(ProtocolConstant.CRLF);
            }

            buffer.writeBytes(ProtocolConstant.END);
            buffer.writeBytes(ProtocolConstant.CRLF);
        } else {
            buffer.writeBytes(ProtocolConstant.END);
            buffer.writeBytes(ProtocolConstant.CRLF);
        }
    }

    public ActionData readHead(byte[] header, int startPos) {
        CommonActionData md = new CommonActionData(BaseActionType.GET);
        int[] pos = { startPos };
        readKeyBytes(md, header, pos);
        readRouteVersion(md, header, pos);
        readVnode(md, header, pos);
        return md;
    }

}

package com.alibaba.doris.dataserver.action.parser;

import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.data.BaseActionType;
import com.alibaba.doris.dataserver.action.data.CommonActionData;
import com.alibaba.doris.dataserver.action.data.SupportBodyActionData;
import com.alibaba.doris.dataserver.net.ByteBufferWrapper;
import com.alibaba.doris.dataserver.net.protocol.text.ProtocolConstant;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class SetActionParser extends BaseActionParser {

    public void writeHead(ByteBufferWrapper buffer, ActionData actionData) {
        CommonActionData md = (CommonActionData) actionData;
        if (md.isSuccess()) {
            buffer.writeBytes(ProtocolConstant.STORED);
            buffer.writeBytes(ProtocolConstant.CRLF);
        } else {
            buffer.writeBytes(ProtocolConstant.NOT_STORED);
            buffer.writeBytes(ProtocolConstant.CRLF);
        }
    }

    @Override
    public void readBody(byte[] body, ActionData actionData) {
        SupportBodyActionData bodyActionData = (SupportBodyActionData) actionData;
        bodyActionData.setBodyByteArray(body);
    }

    protected CommonActionData generateActionData() {
        return new CommonActionData(BaseActionType.SET);
    }

    public ActionData readHead(byte[] header, int startPos) {
        CommonActionData md = generateActionData();
        int[] pos = { startPos };
        readKeyBytes(md, header, pos);
        md.setFlag(Short.valueOf(parseNextField(header, pos)));
        md.setTimestamp(Long.valueOf(parseNextField(header, pos)));
        md.setBodyBytes(Integer.valueOf(parseNextField(header, pos)));
        readRouteVersion(md, header, pos);
        readVnode(md, header, pos);
        md.setNeedReadBody(true);
        return md;
    }

}

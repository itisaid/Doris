package com.alibaba.doris.dataserver.extratools.replica.action;

import org.apache.commons.beanutils.BeanUtils;

import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.parser.BaseActionParser;
import com.alibaba.doris.dataserver.net.ByteBufferWrapper;
import com.alibaba.doris.dataserver.net.protocol.ProtocolParseExcetion;
import com.alibaba.doris.dataserver.net.protocol.text.ProtocolConstant;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ExportActionParser extends BaseActionParser {

    public ActionData readHead(byte[] header, int startPos) {
        ActionData actionData = generateActionData();
        int[] pos = new int[] { startPos };

        String itemString = null;
        while (((itemString = parseNextField(header, pos)) != null)) {
            String[] item = parseItem(itemString);
            setActionProperties(actionData, item[0], item[1]);
        }

        return actionData;
    }

    protected ActionData generateActionData() {
        return new ExportActionData();
    }

    private String[] parseItem(String item) {
        String[] items = item.split("=");
        if (items.length != 2) {
            throw new ProtocolParseExcetion("Invalid command arguments :" + item + ". (eg: namespace=1)");
        }
        return items;
    }

    private void setActionProperties(ActionData actionData, String propName, String propValue) {
        try {
            BeanUtils.setProperty(actionData, propName, propValue);
        } catch (Exception e) {
            throw new ProtocolParseExcetion(e);
        }
    }

    public void writeHead(ByteBufferWrapper buffer, ActionData actionData) {
        ExportActionData exportActionData = (ExportActionData) actionData;

        String message = exportActionData.getMessage();
        if (null != message) {
            buffer.writeBytes(ByteUtils.stringToByte(message));
        }

        buffer.writeBytes(ProtocolConstant.CRLF);
    }

}

package com.alibaba.doris.dataserver.action.parser;

import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.data.ShutdownActionData;
import com.alibaba.doris.dataserver.net.ByteBufferWrapper;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ShutdownActionParser extends BaseActionParser {

    public ActionData readHead(byte[] header, int startPos) {
        return new ShutdownActionData();
    }

    public void writeHead(ByteBufferWrapper buffer, ActionData actionData) {
        ;
    }

}

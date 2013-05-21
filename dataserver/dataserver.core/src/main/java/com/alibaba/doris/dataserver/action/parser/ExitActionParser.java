package com.alibaba.doris.dataserver.action.parser;

import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.data.ExitActionData;
import com.alibaba.doris.dataserver.net.ByteBufferWrapper;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ExitActionParser extends BaseActionParser {

    public void writeHead(ByteBufferWrapper buffer, ActionData actionData) {
        ;
    }

    public ActionData readHead(byte[] header, int startPos) {
        return new ExitActionData();
    }

}

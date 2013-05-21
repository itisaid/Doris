package com.alibaba.doris.dataserver.action.parser;

import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.net.ByteBufferWrapper;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface ActionParser {

    public ActionData readHead(byte[] header, int startPos);

    public void readBody(byte[] body, ActionData actionData);

    public void writeHead(ByteBufferWrapper buffer, ActionData actionData);

    public void writeBody(ByteBufferWrapper buffer, ActionData actionData);
}

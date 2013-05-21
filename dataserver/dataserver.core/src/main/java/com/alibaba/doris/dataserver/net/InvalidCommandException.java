package com.alibaba.doris.dataserver.net;

import com.alibaba.doris.dataserver.net.protocol.ProtocolParseExcetion;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class InvalidCommandException extends ProtocolParseExcetion {

    private static final long serialVersionUID = 5401136214306874129L;

    public InvalidCommandException() {

    }

    public InvalidCommandException(String msg) {
        super(msg);
    }

    public InvalidCommandException(String msg, Throwable t) {
        super(msg, t);
    }

}

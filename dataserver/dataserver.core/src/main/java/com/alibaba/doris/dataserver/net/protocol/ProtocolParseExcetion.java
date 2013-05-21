package com.alibaba.doris.dataserver.net.protocol;

import com.alibaba.doris.dataserver.DataServerException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ProtocolParseExcetion extends DataServerException {

    private static final long serialVersionUID = 4026154996188001354L;

    /**
     * Creates a new exception.
     */
    public ProtocolParseExcetion() {
        super();
    }

    /**
     * Creates a new exception.
     */
    public ProtocolParseExcetion(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public ProtocolParseExcetion(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public ProtocolParseExcetion(Throwable cause) {
        super(cause);
    }
}

package com.alibaba.doris.client.net.exception;

import com.alibaba.doris.client.net.NetException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class InvalidCommandException extends NetException {

    private static final long serialVersionUID = 4519183750305605444L;

    public InvalidCommandException() {

    }

    /**
     * @param message
     */
    public InvalidCommandException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public InvalidCommandException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}

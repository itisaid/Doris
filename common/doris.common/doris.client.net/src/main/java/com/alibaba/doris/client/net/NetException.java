package com.alibaba.doris.client.net;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class NetException extends RuntimeException {

    private static final long serialVersionUID = 1496974586218389057L;

    public NetException() {

    }

    /**
     * @param message
     */
    public NetException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public NetException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public NetException(String message, Throwable cause) {
        super(message, cause);
    }
}

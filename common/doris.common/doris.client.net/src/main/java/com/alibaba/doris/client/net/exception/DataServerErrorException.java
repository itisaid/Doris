package com.alibaba.doris.client.net.exception;

import com.alibaba.doris.client.net.NetException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DataServerErrorException extends NetException {

    private static final long serialVersionUID = -2011735495303253179L;

    public DataServerErrorException() {

    }

    /**
     * @param message
     */
    public DataServerErrorException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public DataServerErrorException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public DataServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}

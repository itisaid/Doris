package com.alibaba.doris.dataserver.store.log.db;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class WriteWindowClosedException extends RuntimeException {

    private static final long serialVersionUID = 569879787116439036L;

    /**
     * Creates a new exception.
     */
    public WriteWindowClosedException() {
        super();
    }

    /**
     * Creates a new exception.
     */
    public WriteWindowClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public WriteWindowClosedException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public WriteWindowClosedException(Throwable cause) {
        super(cause);
    }
}

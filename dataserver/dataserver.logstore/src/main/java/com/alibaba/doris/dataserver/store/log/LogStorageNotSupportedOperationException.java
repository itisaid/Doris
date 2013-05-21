package com.alibaba.doris.dataserver.store.log;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class LogStorageNotSupportedOperationException extends LogStorageException {

    private static final long serialVersionUID = 3593915613337859696L;

    /**
     * Creates a new exception.
     */
    public LogStorageNotSupportedOperationException() {
        super();
    }

    /**
     * Creates a new exception.
     */
    public LogStorageNotSupportedOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public LogStorageNotSupportedOperationException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public LogStorageNotSupportedOperationException(Throwable cause) {
        super(cause);
    }
}

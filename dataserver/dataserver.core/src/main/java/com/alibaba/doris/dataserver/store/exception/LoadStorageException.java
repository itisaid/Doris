package com.alibaba.doris.dataserver.store.exception;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class LoadStorageException extends StorageException {

    private static final long serialVersionUID = 1686201353083180849L;

    /**
     * Creates a new exception.
     */
    public LoadStorageException() {
        super();
    }

    /**
     * Creates a new exception.
     */
    public LoadStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public LoadStorageException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public LoadStorageException(Throwable cause) {
        super(cause);
    }
}

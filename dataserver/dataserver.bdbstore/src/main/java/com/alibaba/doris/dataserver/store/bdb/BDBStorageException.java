package com.alibaba.doris.dataserver.store.bdb;

import com.alibaba.doris.dataserver.store.exception.StorageException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class BDBStorageException extends StorageException {

    private static final long serialVersionUID = -8750585063046256696L;

    /**
     * Creates a new exception.
     */
    public BDBStorageException() {
        super();
    }

    /**
     * Creates a new exception.
     */
    public BDBStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public BDBStorageException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public BDBStorageException(Throwable cause) {
        super(cause);
    }
}

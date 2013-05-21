package com.alibaba.doris.dataserver.store.log;

import com.alibaba.doris.dataserver.store.exception.StorageException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class LogStorageException extends StorageException {

    private static final long serialVersionUID = -1968981771129627604L;

    /**
     * Creates a new exception.
     */
    public LogStorageException() {
        super();
    }

    /**
     * Creates a new exception.
     */
    public LogStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public LogStorageException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public LogStorageException(Throwable cause) {
        super(cause);
    }
}

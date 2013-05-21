package com.alibaba.doris.dataserver.store.mysql;

import com.alibaba.doris.dataserver.store.exception.StorageException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class MysqlStorageException extends StorageException {

    private static final long serialVersionUID = -2037848662895578197L;

    /**
     * Creates a new exception.
     */
    public MysqlStorageException() {
        super();
    }

    /**
     * Creates a new exception.
     */
    public MysqlStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public MysqlStorageException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public MysqlStorageException(Throwable cause) {
        super(cause);
    }
}

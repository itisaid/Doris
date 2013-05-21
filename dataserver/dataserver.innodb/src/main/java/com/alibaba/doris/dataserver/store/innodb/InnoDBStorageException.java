package com.alibaba.doris.dataserver.store.innodb;

import com.alibaba.doris.dataserver.store.exception.StorageException;

/*
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class InnoDBStorageException extends StorageException {

    private static final long serialVersionUID = 4444855231318983231L;

    /**
     * Creates a new exception.
     */
    public InnoDBStorageException() {
        super();
    }

    /**
     * Creates a new exception.
     */
    public InnoDBStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public InnoDBStorageException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public InnoDBStorageException(Throwable cause) {
        super(cause);
    }
}

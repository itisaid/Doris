package com.alibaba.doris.dataserver.store.exception;

import com.alibaba.doris.dataserver.DataServerException;

/**
 * 存储层异常的顶级类。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class StorageException extends DataServerException {

    private static final long serialVersionUID = 5969495517958662769L;

    /**
     * Creates a new exception.
     */
    public StorageException() {
        super();
    }

    /**
     * Creates a new exception.
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public StorageException(Throwable cause) {
        super(cause);
    }
}

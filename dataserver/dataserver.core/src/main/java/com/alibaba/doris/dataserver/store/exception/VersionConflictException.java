package com.alibaba.doris.dataserver.store.exception;

/**
 * 版本号冲突异常，但存入的数据版本号和存储中现有数据的版本号不一致时，系统会抛出该异常。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class VersionConflictException extends StorageException {

    private static final long serialVersionUID = 6945184440480756728L;

    /**
     * Creates a new exception.
     */
    public VersionConflictException() {
        super();
    }

    /**
     * Creates a new exception.
     */
    public VersionConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public VersionConflictException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public VersionConflictException(Throwable cause) {
        super(cause);
    }
}

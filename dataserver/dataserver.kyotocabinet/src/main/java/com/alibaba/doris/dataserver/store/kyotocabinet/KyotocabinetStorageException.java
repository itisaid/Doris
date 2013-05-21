package com.alibaba.doris.dataserver.store.kyotocabinet;

import com.alibaba.doris.dataserver.store.exception.StorageException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class KyotocabinetStorageException extends StorageException {

    private static final long serialVersionUID = -9091602585504073055L;

    /**
     * Creates a new exception.
     */
    public KyotocabinetStorageException() {
        super();
    }

    /**
     * Creates a new exception.
     */
    public KyotocabinetStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public KyotocabinetStorageException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public KyotocabinetStorageException(Throwable cause) {
        super(cause);
    }
}

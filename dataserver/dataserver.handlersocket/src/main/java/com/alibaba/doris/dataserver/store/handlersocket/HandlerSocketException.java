package com.alibaba.doris.dataserver.store.handlersocket;

import com.alibaba.doris.dataserver.store.exception.StorageException;

public class HandlerSocketException extends StorageException {

    private static final long serialVersionUID = -8750585063046256696L;

    /**
     * Creates a new exception.
     */
    public HandlerSocketException() {
        super();
    }

    /**
     * Creates a new exception.
     */
    public HandlerSocketException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public HandlerSocketException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public HandlerSocketException(Throwable cause) {
        super(cause);
    }
}

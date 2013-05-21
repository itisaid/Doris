package com.alibaba.doris.dataserver.extratools.replica;

import com.alibaba.doris.dataserver.DataServerException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ReplicaException extends DataServerException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception.
     */
    public ReplicaException() {
        super();
    }

    /**
     * Creates a new exception.
     */
    public ReplicaException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public ReplicaException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public ReplicaException(Throwable cause) {
        super(cause);
    }
}

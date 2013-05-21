package com.alibaba.doris.dataserver.migrator.filter;

import com.alibaba.doris.dataserver.DataServerException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ProxyOperationException extends DataServerException {

    private static final long serialVersionUID = 6132582371654839701L;

    /**
     * Creates a new exception.
     */
    public ProxyOperationException() {
        super();
    }

    /**
     * Creates a new exception.
     */
    public ProxyOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public ProxyOperationException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public ProxyOperationException(Throwable cause) {
        super(cause);
    }
}

package com.alibaba.doris.dataserver;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DataServerException extends RuntimeException {

    private static final long serialVersionUID = -1582335656751975937L;

    /**
     * Creates a new exception.
     */
    public DataServerException() {
        super();
    }

    /**
     * Creates a new exception.
     */
    public DataServerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public DataServerException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public DataServerException(Throwable cause) {
        super(cause);
    }
}

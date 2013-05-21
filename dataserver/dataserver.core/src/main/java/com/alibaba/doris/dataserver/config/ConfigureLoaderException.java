package com.alibaba.doris.dataserver.config;

import com.alibaba.doris.dataserver.DataServerException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ConfigureLoaderException extends DataServerException {

    private static final long serialVersionUID = 2663079511687037250L;

    /**
     * Creates a new exception.
     */
    public ConfigureLoaderException() {
        super();
    }

    /**
     * Creates a new exception.
     */
    public ConfigureLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public ConfigureLoaderException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public ConfigureLoaderException(Throwable cause) {
        super(cause);
    }
}

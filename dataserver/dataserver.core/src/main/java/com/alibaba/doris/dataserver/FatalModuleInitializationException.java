package com.alibaba.doris.dataserver;

/**
 * 初始化Module出现致命错误的异常，如果初始化过程中Module抛出该异常，<br>
 * 此时DataServer将无法启动。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class FatalModuleInitializationException extends DataServerException {

    private static final long serialVersionUID = -8581954691420264827L;

    /**
     * Creates a new exception.
     */
    public FatalModuleInitializationException() {
        super();
    }

    /**
     * Creates a new exception.
     */
    public FatalModuleInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public FatalModuleInitializationException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public FatalModuleInitializationException(Throwable cause) {
        super(cause);
    }
}

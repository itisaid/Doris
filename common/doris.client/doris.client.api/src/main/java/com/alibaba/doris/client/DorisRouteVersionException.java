package com.alibaba.doris.client;

/**
 * Doris路由版本异常
 * 
 * @author frank
 */
public class DorisRouteVersionException extends AccessException {

    private static final long serialVersionUID = 1904274069223034480L;

    public DorisRouteVersionException() {
        super();
    }

    public DorisRouteVersionException(String message) {
        super(message);
    }

    public DorisRouteVersionException(String message, Throwable t) {
        super(message, t);
    }
}

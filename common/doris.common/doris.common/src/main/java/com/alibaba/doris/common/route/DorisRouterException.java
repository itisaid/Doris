package com.alibaba.doris.common.route;

/**
 * Doris 路由器异常
 * 
 * @author frank
 */
public class DorisRouterException extends Exception {

    private static final long serialVersionUID = -6107089180585916432L;

    public DorisRouterException() {
        super();
    }

    public DorisRouterException(String message) {
        super(message);
    }

    public DorisRouterException(String message, Throwable t) {
        super(message, t);
    }
}

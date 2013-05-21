package com.alibaba.doris.client.net.exception;

import com.alibaba.doris.client.net.NetException;

/**
 * 当Client的Version过期时，会抛出本异常。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class RouteVersionOutOfDateException extends NetException {

    private static final long serialVersionUID = -5921487718180747217L;

    public RouteVersionOutOfDateException() {

    }

    /**
     * @param message
     */
    public RouteVersionOutOfDateException(String message, String newRouteTable) {
        super(message);
        this.newRouteTable = newRouteTable;
    }

    /**
     * @param cause
     */
    public RouteVersionOutOfDateException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public RouteVersionOutOfDateException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getNewRouteTable() {
        return newRouteTable;
    }

    private String newRouteTable;
}

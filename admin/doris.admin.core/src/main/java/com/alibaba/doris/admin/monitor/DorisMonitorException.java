package com.alibaba.doris.admin.monitor;

public class DorisMonitorException extends Exception {

    private static final long serialVersionUID = 1L;

    public DorisMonitorException() {
        super();
    }

    public DorisMonitorException(String message) {
        super(message);
    }

    public DorisMonitorException(String message, Throwable e) {
        super(message, e);
    }
}

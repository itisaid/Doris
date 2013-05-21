package com.alibaba.doris.common.config;

/*
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ClientConfiguration {

    public long getTimeoutOfOperation() {
        return timeoutOfOperation;
    }

    public void setTimeoutOfOperation(long timeoutOfOperation) {
        this.timeoutOfOperation = timeoutOfOperation;
    }

    private long        timeoutOfOperation = DEFAULT_TIME_OUT; // ms
    private static long DEFAULT_TIME_OUT   = 10000;           // the default value is 10 seconds
}

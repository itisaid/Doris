package com.alibaba.doris.dataserver.monitor.support;

public class UpTime {

    private final static long SERVER_START = System.currentTimeMillis();

    public static void init() {
        //do nothing just init the class
    }

    /**
     * 取得系统的启动时间
     */
    public static int getUpTimeInSeconds() {
        return (int) ((System.currentTimeMillis() - SERVER_START) / 1000);
    }

}

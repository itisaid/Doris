package com.alibaba.doris.common;

/**
 * 性能报告
 * 
 * @author helios
 */
public class PrefReportUnit {

    /**
     * 操作名
     */
    private String actionName;

    /**
     * 命名空间 为了避免key的重复
     */
    private String nameSpace;

    /**
     * 操作数
     */
    private long   totalOperation;

    /**
     * 最小延迟
     */
    private int    minLatency;

    /**
     * 最大延迟
     */
    private int    maxLatency;

    /**
     * 总的延迟时间
     */
    private long   totalLatency;

    /**
     * 总的字节数
     */
    private long   totalBytes;

    /**
     * 最大并发数
     */
    private int    maxConcurrencyLevel;

    /**
     * 当前并发数
     */
    private int    currentConcurrencyLevel;

    /**
     * 统计开始时间totalBytes
     */
    private long   timeStart;

    /**
     * 时间片
     */
    private long   timeUsed;

    /**
     * 80%的响应
     */
    private int    the80thLatency;

    /**
     * 95%的响应
     */
    private int    the95thLatency;

    /**
     * 99%的响应时间
     */
    private int    the99thLatency;

    /**
     * @return
     */
    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public long getTotalOperation() {
        return totalOperation;
    }

    public void setTotalOperation(long totalOperation) {
        this.totalOperation = totalOperation;
    }

    public int getMinLatency() {
        return minLatency;
    }

    public void setMinLatency(int minLatency) {
        this.minLatency = minLatency;
    }

    public int getMaxLatency() {
        return maxLatency;
    }

    public void setMaxLatency(int maxLatency) {
        this.maxLatency = maxLatency;
    }

    public long getTotalLatency() {
        return totalLatency;
    }

    public void setTotalLatency(long totalLatency) {
        this.totalLatency = totalLatency;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public int getMaxConcurrencyLevel() {
        return maxConcurrencyLevel;
    }

    public void setMaxConcurrencyLevel(int maxConcurrencyLevel) {
        this.maxConcurrencyLevel = maxConcurrencyLevel;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeUsed() {
        return timeUsed;
    }

    public void setTimeUsed(long timeUsed) {
        this.timeUsed = timeUsed;
    }

    public int getThe80thLatency() {
        return the80thLatency;
    }

    public void setThe80thLatency(int the80thLatency) {
        this.the80thLatency = the80thLatency;
    }

    public int getThe95thLatency() {
        return the95thLatency;
    }

    public void setThe95thLatency(int the95thLatency) {
        this.the95thLatency = the95thLatency;
    }

    public int getThe99thLatency() {
        return the99thLatency;
    }

    public void setThe99thLatency(int the99thLatency) {
        this.the99thLatency = the99thLatency;
    }

    public int getCurrentConcurrencyLevel() {
        return currentConcurrencyLevel;
    }

    public void setCurrentConcurrencyLevel(int currentConcurrencyLevel) {
        this.currentConcurrencyLevel = currentConcurrencyLevel;
    }


}

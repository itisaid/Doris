package com.alibaba.doris.admin.support;

public class PrefStatObject {

    private String  actionName;

    private String  nameSpace;

    private String  physicalId;

    /**
     * 操作数
     */
    private long    totalOperations;

    /**
     * 每秒请求数tmplist
     */
    private double  ops;

    private double  avgLatency;

    /**
     * 每秒响应字节数
     */
    private double  bps;

    /**
     * 总的延迟时间
     */
    private long    totalLatency;

    /**
     * 总的字节数
     */
    private long    totalBytes;

    /**
     * 最大延迟
     */
    private Integer minLatency;

    /**
     * 最大延迟
     */
    private Integer maxLatency;

    private Integer the80thLatency;

    private Integer the95thLatency;

    private Integer the99thLatency;

    /**
     * 最大并发数
     */
    private Integer maxConcurrencyLevel;

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

    public long getTotalOperations() {
        return totalOperations;
    }

    public void setTotalOperations(long totalOperations) {
        this.totalOperations = totalOperations;
    }

    public double getOps() {
        return ops;
    }

    public void setOps(double ops) {
        this.ops = ops;
    }

    public double getBps() {
        return bps;
    }

    public void setBps(double bps) {
        this.bps = bps;
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

    public Integer getMinLatency() {
        return minLatency;
    }

    public void setMinLatency(Integer minLatency) {
        this.minLatency = minLatency;
    }

    public Integer getMaxLatency() {
        return maxLatency;
    }

    public void setMaxLatency(Integer maxLatency) {
        this.maxLatency = maxLatency;
    }

    public Integer getThe80thLatency() {
        return the80thLatency;
    }

    public void setThe80thLatency(Integer the80thLatency) {
        this.the80thLatency = the80thLatency;
    }

    public Integer getThe95thLatency() {
        return the95thLatency;
    }

    public void setThe95thLatency(Integer the95thLatency) {
        this.the95thLatency = the95thLatency;
    }

    public Integer getThe99thLatency() {
        return the99thLatency;
    }

    public void setThe99thLatency(Integer the99thLatency) {
        this.the99thLatency = the99thLatency;
    }

    public Integer getMaxConcurrencyLevel() {
        return maxConcurrencyLevel;
    }

    public void setMaxConcurrencyLevel(Integer maxConcurrencyLevel) {
        this.maxConcurrencyLevel = maxConcurrencyLevel;
    }

    public double getAvgLatency() {
        return avgLatency;
    }

    public void setAvgLatency(double avgLatency) {
        this.avgLatency = avgLatency;
    }

    public String getPhysicalId() {
        return physicalId;
    }

    public void setPhysicalId(String physicalId) {
        this.physicalId = physicalId;
    }

}

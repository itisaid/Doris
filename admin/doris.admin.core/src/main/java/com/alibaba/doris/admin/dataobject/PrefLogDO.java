package com.alibaba.doris.admin.dataobject;

import java.util.Date;

public class PrefLogDO {

    private Integer id;

    private Date    gmtCreate;

    private Date    gmtModified;

    private String  physicalId;

    /**
     * 最小延迟
     */
    private Integer minLatency;

    /**
     * 最大延迟
     */
    private Integer maxLatency;

    private Integer the80thLatency;

    private Integer the95thLatency;

    private Integer the99thLatency;

    private String  actionName;

    private String  nameSpace;

    /**
     * 操作数
     */
    private long    totalOperations;

    /**
     * 总的延迟时间
     */
    private long    totalLatency;

    /**
     * 总的字节数
     */
    private long    totalBytes;

    /**
     * 最大并发数
     */
    private Integer maxConcurrencyLevel;

    /**
     * 统计开始时间
     */
    private Date    timeStart;

    /**
     * 统计结束时间
     */
    private long    timeUsed;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getPhysicalId() {
        return physicalId;
    }

    public void setPhysicalId(String physicalId) {
        this.physicalId = physicalId;
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

    public Integer getMaxConcurrencyLevel() {
        return maxConcurrencyLevel;
    }

    public void setMaxConcurrencyLevel(Integer maxConcurrencyLevel) {
        this.maxConcurrencyLevel = maxConcurrencyLevel;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeUsed() {
        return timeUsed;
    }

    public void setTimeUsed(long timeUsed) {
        this.timeUsed = timeUsed;
    }

}

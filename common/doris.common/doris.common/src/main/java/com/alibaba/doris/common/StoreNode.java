package com.alibaba.doris.common;

public class StoreNode {

    /**
     * Node节点的URL标识
     */
    private String                URL;

    private String                ip;

    private int                   port;
    /**
     * 所属物理机的ID
     */
    private String                phId;
    /**
     * Node节点所属的GroupID标识
     */
    private StoreNodeSequenceEnum sequence;
    /**
     * Node节点的逻辑标识ID
     */
    private int                   logicId;
    /**
     * Node节点的状态信息
     */
    private NodeRouteStatus       status;

    public String getURL() {
        return URL;
    }

    public void setURL(String uRL) {
        URL = uRL;
    }

    public String getPhId() {
        return phId;
    }

    public void setPhId(String phId) {
        this.phId = phId;
    }

    public StoreNodeSequenceEnum getSequence() {
        return sequence;
    }

    public void setSequence(StoreNodeSequenceEnum sequence) {
        this.sequence = sequence;
    }

    public int getLogicId() {
        return logicId;
    }

    public void setLogicId(int logicId) {
        this.logicId = logicId;
    }

    public NodeRouteStatus getStatus() {
        return status;
    }

    public void setStatus(NodeRouteStatus status) {
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String toString() {
        return sequence + "." + logicId;
    }
}

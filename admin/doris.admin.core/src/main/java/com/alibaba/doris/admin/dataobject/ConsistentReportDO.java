/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.doris.admin.dataobject;

import java.util.Date;

/**
 * @author hongwei.zhaohw 2012-1-6
 */
public class ConsistentReportDO {

    private Integer id;
    private Date    gmtCreate;
    private Date    gmtModified;
    private String  keyStr;
    private Integer namespaceId;
    private String  phisicalNodeIps;
    private String  clientIp;
    private String  exceptionMsg;
    private String  timestamp;
    private String  errorType;
    
    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }
    
    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * @return the gmtCreate
     */
    public Date getGmtCreate() {
        return gmtCreate;
    }
    
    /**
     * @param gmtCreate the gmtCreate to set
     */
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }
    
    /**
     * @return the gmtModified
     */
    public Date getGmtModified() {
        return gmtModified;
    }
    
    /**
     * @param gmtModified the gmtModified to set
     */
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
    
    public void setNamespaceId(Integer namespaceId) {
        this.namespaceId = namespaceId;
    }
    
    public Integer getNamespaceId() {
        return namespaceId;
    }
    
    /**
     * @return the phisicalNodeIps
     */
    public String getPhisicalNodeIps() {
        return phisicalNodeIps;
    }
    
    /**
     * @param phisicalNodeIps the phisicalNodeIps to set
     */
    public void setPhisicalNodeIps(String phisicalNodeIps) {
        this.phisicalNodeIps = phisicalNodeIps;
    }
    
    /**
     * @return the clientIp
     */
    public String getClientIp() {
        return clientIp;
    }
    
    /**
     * @param clientIp the clientIp to set
     */
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getExceptionMsg() {
        return exceptionMsg;
    }

    public void setExceptionMsg(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
    }

    public String getKeyStr() {
        return keyStr;
    }

    public void setKeyStr(String keyStr) {
        this.keyStr = keyStr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
    
}

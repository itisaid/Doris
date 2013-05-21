/**
 * Project: doris.config.server-1.0-SNAPSHOT File Created at 2011-4-27 $Id$ Copyright 1999-2100 Alibaba.com Corporation
 * Limited. All rights reserved. This software is the confidential and proprietary information of Alibaba Company.
 * ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.doris.admin.dataobject;

import java.util.Date;

/**
 * TODO Comment of PhysicalNodoDo
 * 
 * @author mianhe
 */
public class PhysicalNodeDO {

    private int    id;

    private int    logicalId;

    private String physicalId;

    private int    serialId;

    private String machineId;

    private String ip;

    private int    port;

    private int    status;

    private Date   gmtCreate;
    private Date   gmtModified;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the logicalId
     */
    public int getLogicalId() {
        return logicalId;
    }

    /**
     * @param logicalId the logicalId to set
     */
    public void setLogicalId(int logicalId) {
        this.logicalId = logicalId;
    }

    /**
     * @return the physicalId
     */
    public String getPhysicalId() {
        return physicalId;
    }

    /**
     * @param physicalId the physicalId to set
     */
    public void setPhysicalId(String physicalId) {
        this.physicalId = physicalId;
    }

    /**
     * @return the serialId
     */
    public int getSerialId() {
        return serialId;
    }

    /**
     * @param serialId the serialId to set
     */
    public void setSerialId(int serialId) {
        this.serialId = serialId;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
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

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
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
        return physicalId;
    }
}

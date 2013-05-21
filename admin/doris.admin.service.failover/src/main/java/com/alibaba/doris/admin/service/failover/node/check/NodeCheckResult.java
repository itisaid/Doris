/**
 * Project: doris.admin.service.failover-0.1.0-SNAPSHOT
 * 
 * File Created at 2011-5-27
 * $Id$
 * 
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.alibaba.doris.admin.service.failover.node.check;

import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.client.net.OperationFuture;
import com.alibaba.doris.client.net.command.result.CheckResult;

/**
 * TODO Comment of NodeCheckResult
 * 
 * @author mian.hem
 */
public class NodeCheckResult {

    private PhysicalNodeDO               pyhsicalNode;

    private NodeHealth                   originalNodeHealth;

    private NodeHealth                   currentNodeHealth;

    private OperationFuture<CheckResult> resultFuture;

    private int                          retries;

    public OperationFuture<CheckResult> getResultFuture() {
        return resultFuture;
    }

    public void setResultFuture(OperationFuture<CheckResult> resultFuture) {
        this.resultFuture = resultFuture;
    }

    public void setPyhsicalNode(PhysicalNodeDO pyhsicalNode) {
        this.pyhsicalNode = pyhsicalNode;
    }

    public NodeCheckResult(PhysicalNodeDO pNode, OperationFuture<CheckResult> chekResult, int nodeCheckRetries) {
        super();
        this.pyhsicalNode = pNode;
        this.resultFuture = chekResult;
        this.retries = nodeCheckRetries;
    }

    public NodeHealth getOriginalNodeHealth() {
        return originalNodeHealth;
    }

    public void setOriginalNodeHealth(NodeHealth originalNodeHealth) {
        this.originalNodeHealth = originalNodeHealth;
    }

    public NodeHealth getCurrentNodeHealth() {
        return currentNodeHealth;
    }

    public void setCurrentNodeHealth(NodeHealth currentNodeHealth) {
        this.currentNodeHealth = currentNodeHealth;
    }

    public PhysicalNodeDO getPyhsicalNode() {
        return pyhsicalNode;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }
}

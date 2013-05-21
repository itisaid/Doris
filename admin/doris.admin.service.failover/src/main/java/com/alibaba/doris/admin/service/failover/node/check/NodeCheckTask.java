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

import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.common.StoreNode;

/**
 * @deprecated
 * @author mian.hem
 *
 */
public class NodeCheckTask implements Callable<NodeCheckResult>{

    private static final Log  log              = LogFactory.getLog(NodeCheckThread.class);
    private NodeCheckManager nodeCheckManager = null;
    private NodeCheckResult nodeCheckResult = null;
    private PhysicalNodeDO pNode = null;
    
    public NodeCheckTask(StoreNode snode) {
        super();
        nodeCheckManager = NodeCheckManager.getInstance();
        pNode = new PhysicalNodeDO();
        pNode.setPhysicalId(snode.getPhId());
        //this.nodeCheckResult = new NodeCheckResult(pNode);
    }
    
    public NodeCheckTask(PhysicalNodeDO pNode, NodeCheckManager nodeCheckManager) {
        this.nodeCheckManager = nodeCheckManager;
        this.pNode = pNode;
//        this.nodeCheckResult = new NodeCheckResult(pNode);
    }

    public NodeCheckResult call() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("start node checking : pid=" + pNode.getPhysicalId());
        }
        NodeHealth originalNodeHealth = nodeCheckManager.checkNode(pNode.getPhysicalId(), false);
        if (log.isDebugEnabled()) {
            log.debug("checkresult (previous) is " + originalNodeHealth + "for node  : pid="
                    + pNode.getPhysicalId());
        }

        NodeHealth currentNodeHealth = nodeCheckManager.verifyNodeAcess(pNode.getPhysicalId());
        if (log.isDebugEnabled()) {
            log.debug("checkresult is (current)" + currentNodeHealth + "for node  : pid="
                    + pNode.getPhysicalId());
        }

        nodeCheckResult.setOriginalNodeHealth(originalNodeHealth);
        nodeCheckResult.setCurrentNodeHealth(currentNodeHealth);
        return nodeCheckResult;
    }

}

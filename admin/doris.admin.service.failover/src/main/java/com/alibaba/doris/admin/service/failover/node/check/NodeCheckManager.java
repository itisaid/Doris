/**
 * Project: doris.admin.service.common-0.1.0-SNAPSHOT
 * 
 * File Created at 2011-5-24
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.service.common.Managerable;
import com.alibaba.doris.admin.service.common.node.NodesManager;
import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.OperationFuture;
import com.alibaba.doris.client.net.command.CheckCommand.CheckType;
import com.alibaba.doris.client.net.command.CheckCommand.Type;
import com.alibaba.doris.client.net.command.result.CheckResult;
import com.alibaba.doris.common.StoreNode;

/**
 * check the node status.
 * 
 * @author mian.hem
 */
public class NodeCheckManager implements Managerable {

    private static NodeCheckManager instance               = new NodeCheckManager();

    private Map<String, NodeHealth> nodeHealthStatuses     = new ConcurrentHashMap<String, NodeHealth>();

    private static final Log        log                    = LogFactory
                                                                   .getLog(NodeCheckManager.class);

    private ReentrantLock           lock                   = new ReentrantLock();

    private NodeCheckThread         checkThread            = null;

    private NodeCheckManager() {
        super();
        checkThread = new NodeCheckThread(this);
    }

    public static NodeCheckManager getInstance() {
        return instance;
    }

    /**
     * Case 1: needRealAccessing == true <br/>
     * Check the node health status. This method will really invoke node if the
     * current cache indicates this node is OK for accessing real state. If
     * returns NG, then it's might be from cache. <br/>
     * Case 2: needRealAccessing == false<br/>
     * Get the node heal status from cache.
     */
    public NodeHealth checkNode(StoreNode node, boolean needRealAccessing) {
        if (node == null) {
            return null;
        }
        lock.lock();
        NodeHealth health = null;
        try {
            health = nodeHealthStatuses.get(node.getPhId());
            if (needRealAccessing && health == NodeHealth.OK) {
                health = accessAndCacheResult(node);
            }
        } finally {
            lock.unlock();
        }

        if (health == null) {
            health = accessAndCacheResult(node);
        }
        
        return health;
    }

    private NodeHealth accessAndCacheResult(StoreNode node) {
        NodeHealth health;
        health = verifyNodeAcess(node);

        //cache health result.
        nodeHealthStatuses.put(node.getPhId(), health);
        return health;
    }

    /**
     * Check the node health status. This method will really invoke node if the
     * current cache indicates this node is OK for accessing real state. If
     * returns NG, then it's might be from cache.
     */
    public NodeHealth checkNode(StoreNode node) {
        return checkNode(node, true);
    }

    /**
     * Check the node health status. This method will really invoke node if the
     * current cache indicates this node is OK for accessing real state. If
     * returns NG, then it's might be from cache.
     */
    public NodeHealth checkNode(String physicalId) {
        StoreNode storeNode = NodesManager.getInstance().getStoreNode(physicalId);
        return checkNode(storeNode);
    }

    /**
     * Check the node health status. This method will really invoke node if the
     * current cache indicates this node is OK for accessing real state. If
     * returns NG, then it's might be from cache.
     */
    public NodeHealth checkNode(String physicalId, boolean needRealAccessing) {
        StoreNode storeNode = NodesManager.getInstance().getStoreNode(physicalId);
        return checkNode(storeNode, needRealAccessing);
    }

    public NodeHealth verifyNodeAcess(String physicalId) {
        StoreNode storeNode = NodesManager.getInstance().getStoreNode(physicalId);
        return verifyNodeAcess(storeNode);
    }

    public NodeHealth verifyNodeAcess(StoreNode node, Connection conn) {

        NodeHealth nodeHealth = NodeHealth.NG;
        try {
            Type checkType = null;

            switch (node.getSequence()) {
                case TEMP_SEQUENCE: // 临时节点
                    checkType = CheckType.CHECK_TEMP_NODE;
                    break;
                case STANDBY_SEQUENCE: // 备用接地那
                    checkType = CheckType.CHECK_STANDBY_NODE;
                    break;
                case UNUSE_SEQUENCE: // 待用节点
                    checkType = null;
                    break;
                default: //正常（Data）节点
                    checkType = CheckType.CHECK_NORMAL_NODE;
                    break;
            }
                
            OperationFuture<CheckResult> future = conn.check(checkType);
            CheckResult checkResult = null;
            try {
                checkResult = future.get(NodeCheckThread.nodeCheckTimeout, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.error("check failed for node :" + node.getPhId());
                log.error(e.getMessage(), e);
                checkResult = null;
            }

            if (checkResult == null) {
                nodeHealth = NodeHealth.NG;
                if (checkResult == null) {
                    log.warn("Check result is null for node :" + node.getPhId());
                }
            } else if (checkResult.isSuccess()) {
                nodeHealth = NodeHealth.OK;
            } else {
                nodeHealth = NodeHealth.NG;
                log.warn("Check result is NG for node :" + node.getPhId() + ", Message:"
                        + checkResult.getMessage());
            }

        } catch (Exception e) {
            log.debug("fail to check node :" + node.getPhId(), e);
            updateNodeHealth(node, NodeHealth.NG);
            return NodeHealth.NG;
        }

        updateNodeHealth(node, nodeHealth);

        return nodeHealth;
    
    }
    
    /**
     * Make put and get invocation to node for it's health state.
     */
    public NodeHealth verifyNodeAcess(StoreNode node) {
        if (node == null ) {
            return null;
        }
        try {
            Connection conn = NodesManager.getInstance().getNodeConnection(node);
            return verifyNodeAcess(node, conn);
        } catch (Exception e) {
            log.debug("fail to check node :" + node.getPhId(), e);
            updateNodeHealth(node, NodeHealth.NG);
            return NodeHealth.NG;
        }
    }

    public void updateNodeHealth(StoreNode node, NodeHealth nodeHealth) {
        lock.lock();
        try {
            nodeHealthStatuses.put(node.getPhId(), nodeHealth);
        } finally {
            lock.unlock();
        }
    }
    
    
    public void updateNodeHealth(String physicalId, NodeHealth nodeHealth) {
        lock.lock();
        try {
            nodeHealthStatuses.put(physicalId, nodeHealth);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns node health snapshot of all nodes.
     */
    public Map<String, NodeHealth> getNodeHealthStatuses() {
        return nodeHealthStatuses;
    }

    public void start() {
        checkThread.start();
    }

    public void stop() {
        checkThread.over();
    }

}

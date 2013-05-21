/**
 * Project: doris.admin.service.common-0.1.0-SNAPSHOT File Created at 2011-5-24 $Id$ Copyright 1999-2100 Alibaba.com
 * Corporation Limited. All rights reserved. This software is the confidential and proprietary information of Alibaba
 * Company. ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.doris.admin.service.failover.node.check;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.monitor.MonitorEnum;
import com.alibaba.doris.admin.monitor.SystemLogMonitor;
import com.alibaba.doris.admin.service.AdminNodeService;
import com.alibaba.doris.admin.service.PropertiesService;
import com.alibaba.doris.admin.service.common.AdminServiceException;
import com.alibaba.doris.admin.service.common.node.NodesManager;
import com.alibaba.doris.admin.service.common.route.DorisConfigServiceException;
import com.alibaba.doris.admin.service.common.route.RouteConfigProcessor;
import com.alibaba.doris.admin.service.failover.processor.FailoverProcessor;
import com.alibaba.doris.admin.service.failover.processor.ForeverFailoverProcessor;
import com.alibaba.doris.admin.service.failover.processor.TempFailoverProcessor;
import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.OperationFuture;
import com.alibaba.doris.client.net.command.CheckCommand.CheckType;
import com.alibaba.doris.client.net.command.CheckCommand.Type;
import com.alibaba.doris.client.net.command.result.CheckResult;
import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.MonitorWarnConstants;
import com.alibaba.doris.common.NodeRouteStatus;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.StoreNodeSequenceEnum;

/**
 * @author mian.hem
 */
public class NodeCheckThread extends Thread {

    private static final Log         log              = LogFactory.getLog(NodeCheckThread.class);

    private NodesManager             nodeManager      = NodesManager.getInstance();
    private NodeCheckManager         nodeCheckManager = null;
    private AdminNodeService         nodeService      = AdminServiceLocator.getAdminNodeService();

    private Map<String, Long>        tempFailTimes    = new HashMap<String, Long>();

    private boolean                  stopped          = false;

    private static PropertiesService propertyService  = AdminServiceLocator.getPropertiesService();
    private long                     sleepTime        = propertyService.getProperty(
                                                                                    "nodeCheckInterval",
                                                                                    Long.TYPE,
                                                                                    AdminServiceConstants.NODE_DEFAULT_HEART_BEAT_INTERVAL);

    private long                     foreverFailTime  = propertyService.getProperty(
                                                                                    "foreverFailTime",
                                                                                    Long.TYPE,
                                                                                    AdminServiceConstants.NODE_DEFAULT_FOREVER_FAIL_TIME);

    public static long               nodeCheckTimeout = propertyService.getProperty(
                                                                                    "nodeCheckTimeout",
                                                                                    Long.TYPE,
                                                                                    AdminServiceConstants.NODE_CHECK_TIMEOUT_DEFAULT);

    public static int                nodeCheckRetries = propertyService.getProperty(
                                                                                    "nodeCheckRetries",
                                                                                    Integer.TYPE,
                                                                                    AdminServiceConstants.NODE_CHECK_RETRIES_DEFAULT);

    public NodeCheckThread(NodeCheckManager nodeCheckManager) {
        if (nodeCheckManager == null) {
            throw new IllegalArgumentException("nodeCheckManager cannot be null.");
        }
        this.nodeCheckManager = nodeCheckManager;
    }

    public void over() {
        this.stopped = true;
        this.interrupt();
    }

    public void run() {

        while (!stopped) {
            if (log.isDebugEnabled()) {
                log.debug(" node checking starts...");
            }
            try {
                verifyNode();
                sleep(sleepTime);
            } catch (Throwable e) {
                log.error("node check thread exception:", e);
            }

            if (log.isDebugEnabled()) {
                log.debug(" node checking ends...");
            }
        }
    }

    private void verifyNode() {
        try {
            Collection<PhysicalNodeDO> allnodes = nodeManager.getAllNodeList();

            if (log.isDebugEnabled()) {
                log.debug("There are " + allnodes.size() + " nodes to check");
            }

            if (allnodes != null && !allnodes.isEmpty()) {

                List<NodeCheckResult> checkResults = new ArrayList<NodeCheckResult>();

                for (PhysicalNodeDO pNode : allnodes) {
                    NodeCheckResult nodeCheckResult = new NodeCheckResult(pNode, null, nodeCheckRetries);
                    checkResults.add(nodeCheckResult);
                }

                checkAllNodeHealth(checkResults);

                for (NodeCheckResult checkRslt : checkResults) {
                    NodeHealth originalNodeHealth = checkRslt.getOriginalNodeHealth();
                    NodeHealth nodeHealth = checkRslt.getCurrentNodeHealth();
                    
                    String physicalId = checkRslt.getPyhsicalNode().getPhysicalId();
                    // SYSTEM LOGGING
                    if (originalNodeHealth != nodeHealth) {
                       if (nodeHealth == NodeHealth.NG) {
                           SystemLogMonitor.info(MonitorEnum.NODE_HEALTH, MonitorWarnConstants.NODE_HEALTH_CHANGE_TO_NG + physicalId);
                       } else {
                           SystemLogMonitor.info(MonitorEnum.NODE_HEALTH, MonitorWarnConstants.NODE_HEALTH_CHANGE_TO_OK + physicalId);
                       }
                    }
                    
                    if (nodeHealth == null) {
                        if (log.isWarnEnabled()) {
                            log.warn("Cannot get the node health status for node with pid=" + physicalId);
                        }
                        continue;
                    }
                    StoreNodeSequenceEnum storeSequence = StoreNodeSequenceEnum.getTypeByValue(checkRslt.getPyhsicalNode().getSerialId());
                    if (storeSequence == StoreNodeSequenceEnum.TEMP_SEQUENCE) {
                        checkRsltForTempSeq(nodeHealth, checkRslt.getPyhsicalNode());
                    } else {
                        checkRsltForNormalSeq(originalNodeHealth, nodeHealth, checkRslt.getPyhsicalNode());
                    }
                }
            }
        } catch (Throwable e) {
            log.error("fail to verify nodes:", e);
        }
    }

    private void checkRsltForTempSeq(NodeHealth nodeHealth, PhysicalNodeDO physicalNodeDO) {

        String physicalId = physicalNodeDO.getPhysicalId();

        StoreNode node = nodeManager.getStoreNode(physicalId);
        if (node == null) {
            log.error("Cannot find node for node with pid=" + physicalId);
            return;
        }
        NodeRouteStatus routeStatus = node.getStatus();

        // the node route status should be consistent with node health status for temp sequence
        if ((nodeHealth == NodeHealth.OK && routeStatus == NodeRouteStatus.TEMP_FAILED)
            || (nodeHealth == NodeHealth.NG && routeStatus == NodeRouteStatus.OK)) {
            NodeRouteStatus status = (nodeHealth == NodeHealth.OK) ? NodeRouteStatus.OK : NodeRouteStatus.TEMP_FAILED;
            if (log.isInfoEnabled()) {
                log.info("update node status for node \"" + physicalId + "\" by set status to " + status);
            }
            nodeService.updatePhysicalNodeStatus(physicalId, status.getValue());
            
            if (nodeHealth == NodeHealth.NG) {
                SystemLogMonitor.error(MonitorEnum.NODE_HEALTH,
                        MonitorWarnConstants.NODE_TEMP_FAILED + ":" + physicalId);
            } else {
                SystemLogMonitor.info(MonitorEnum.NODE_HEALTH,
                        MonitorWarnConstants.NODE_TEMP_FAILURE_RESOLVED + ":" + physicalId);
            }
        }
    }

    private void checkRsltForNormalSeq(NodeHealth originalNodeHealth, NodeHealth nodeHealth,
                                       PhysicalNodeDO physicalNodeDO) {
        updateTempFailedTimes(nodeHealth, physicalNodeDO);

        // node health changes, fire corresponding events base on nodeHealth
        onNodeHealthStatusChange(physicalNodeDO, nodeHealth, originalNodeHealth);
    }

    private void updateTempFailedTimes(NodeHealth nodeHealth, PhysicalNodeDO physicalNodeDO) {
        String physicalId = physicalNodeDO.getPhysicalId();
        if (NodeHealth.NG == nodeHealth) {
            Long times = tempFailTimes.get(physicalId);
            if (times == null) {
                // first time temp failed.
                if (log.isDebugEnabled()) {
                    log.debug("temp failed first time. for node :pid =" + physicalId);
                }
                tempFailTimes.put(physicalId, 1L);
            } else {
                tempFailTimes.put(physicalId, ++times);
                if (log.isDebugEnabled()) {
                    log.debug("temp failed first time. for node :pid =" + tempFailTimes.get(physicalId));
                }
            }
        } else {
            tempFailTimes.put(physicalId, null);
        }
    }

    private void onNodeHealthStatusChange(PhysicalNodeDO physicalNodeDO, NodeHealth nodeHealth,
                                          NodeHealth originalNodeHealth) {

        String physicalId = physicalNodeDO.getPhysicalId();
        boolean isNormalNode = StoreNodeSequenceEnum.isNormalSequence(String.valueOf(physicalNodeDO.getSerialId()));
        if (nodeHealth == NodeHealth.NG) {

            StoreNode node = nodeManager.getStoreNode(physicalId);
            if (node == null) {
                log.error("Cannot find node for node with pid=" + physicalId);
                return;
            }
            NodeRouteStatus routeStatus = node.getStatus();
            if (routeStatus != NodeRouteStatus.TEMP_FAILED) {
                // 1. from NodeHealth.OK to NodeHealth.NG, generate new route configuration instance
                int status = NodeRouteStatus.TEMP_FAILED.getValue();
                if (log.isInfoEnabled()) {
                    log.info("update node status for node \"" + physicalId + "\" by set status to "
                             + NodeRouteStatus.TEMP_FAILED);
                }
                nodeService.updatePhysicalNodeStatus(physicalId, status);
                try {
                    RouteConfigProcessor.getInstance().refresh();
                } catch (DorisConfigServiceException e) {
                    log.error("failed refresh route config", e);
                    // message 'Re-Gen Route failed:' is used as dragoon warning rule, should be consistent.
                    SystemLogMonitor.error(MonitorEnum.ROUTER,
                            MonitorWarnConstants.RE_GEN_ROUTE_FAILED + ":" + physicalId, e);
                }
                if (log.isInfoEnabled()) {
                    log.info("refresh and generated new route config instance.");
                }
                // message 'Node Temp Failed:' is used as dragoon warning rule, should be consistent.
                SystemLogMonitor.error(MonitorEnum.NODE_HEALTH,
                        MonitorWarnConstants.NODE_TEMP_FAILED + ":" + physicalId);
            }

            boolean foreverFailed = checkForeverFail(physicalId);
            
            if (foreverFailed) {
                SystemLogMonitor.error(MonitorEnum.NODE_HEALTH,
                        MonitorWarnConstants.NODE_FOREVER_FAILED + ":" + physicalId);
            }
            
            if (foreverFailed && isNormalNode) {
                if (log.isInfoEnabled()) {
                    log.info("resolve type: forever failed resolve for node :" + physicalId);
                }
                FailoverProcessor failoverProcessor = ForeverFailoverProcessor.getInstance();

                try {
                    failoverProcessor.failResolve(physicalId);
                    if (log.isInfoEnabled()) {
                        log.info("resolve starts for node with physical id \"" + physicalId + "\".");
                    }
                } catch (AdminServiceException e) {
                    log.error("resolve starts for node with physical id \"" + physicalId + "\", but failed.");
                    SystemLogMonitor.error(MonitorEnum.NODE_HEALTH,
                            MonitorWarnConstants.NODE_FOREVER_FAILURE_RESOLVE_FAILED + ":" + physicalId, e);
                }

                // reset counter
                tempFailTimes.put(physicalId, null);

            }
        } else if (originalNodeHealth == NodeHealth.NG && isNormalNode) {
            // 2. from NodeHealth.NG to NodeHealth.OK, start fail over recover
            FailoverProcessor failoverProcessor = TempFailoverProcessor.getInstance();
            // boolean foreverFailed = checkForeverFail(physicalId);
            // if (foreverFailed) {
            // if (log.isInfoEnabled()) {
            // log.info("resolve type: forever failed resolve for node :" + physicalId);
            // }
            // failoverProcessor = ForeverFailoverProcessor.getInstance();
            // } else {
            if (log.isInfoEnabled()) {
                log.info("resolve type: temp failed resolve for node :" + physicalId);
            }
            // }

            try {
                failoverProcessor.failResolve(physicalId);
                if (log.isInfoEnabled()) {
                    log.info("resolve starts for node with physical id \"" + physicalId + "\".");
                }
            } catch (AdminServiceException e) {
                log.error("resolve starts for node with physical id \"" + physicalId + "\", but failed.");
                // message 'TempFailure Resolve Failed:' is used as dragoon warning rule, should be consistent.
                SystemLogMonitor.error(MonitorEnum.NODE_HEALTH,
                                MonitorWarnConstants.NODE_TEMP_FAILURE_RESOLVE_FAILED + ":" + physicalId, e);
            }

            if (log.isDebugEnabled()) {
                log.debug("temp failed counter is reset for node :pid =" + physicalId);
            }

        }

    }

    private boolean checkForeverFail(String phId) {
        Long times = tempFailTimes.get(phId);
        if (times != null) {
            return (times * sleepTime) > foreverFailTime;
        }
        return false;
    }

    private void checkAllNodeHealth(List<NodeCheckResult> checkResults) {

        for (NodeCheckResult checkResult : checkResults) {
            PhysicalNodeDO pNode = checkResult.getPyhsicalNode();
            try {
                Connection conn = NodesManager.getInstance().getNodeConnection(pNode.getPhysicalId());
                
                StoreNodeSequenceEnum nodeSeq = StoreNodeSequenceEnum.getTypeByValue(pNode.getSerialId());
                Type checkType = null;
                switch (nodeSeq) {
                    case TEMP_SEQUENCE: // 临时节点
                        checkType = CheckType.CHECK_TEMP_NODE;
                        break;
                    case STANDBY_SEQUENCE: // 备用节点
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
                checkResult.setResultFuture(future);
            } catch (Throwable e) {
                // From 0.1.3: log.error("Failed to start \"check\" commmond:", e);
                log.error("Failed to start \"check\" commmond, message:" + e.getMessage());
            }
        }

        List<NodeCheckResult> recheckList = processNodeHealthResult(checkResults);

        if (!recheckList.isEmpty()) {
            checkAllNodeHealth(recheckList);
        }
    }

    private List<NodeCheckResult> processNodeHealthResult(List<NodeCheckResult> checkResults) {

        List<NodeCheckResult> needReCheckResults = new ArrayList<NodeCheckResult>();

        for (NodeCheckResult chkRslt : checkResults) {
            // get the original node health
            String physicalId = chkRslt.getPyhsicalNode().getPhysicalId();
            if (log.isDebugEnabled()) {
                log.debug("start node checking : pid=" + physicalId);
            }

            NodeHealth nodeHealth = NodeHealth.NG;
            try {
                CheckResult checkResult = null;
                try {
                    OperationFuture<CheckResult> future = chkRslt.getResultFuture();
                    if (future != null) {
                        long start = System.currentTimeMillis();
                        checkResult = future.get(nodeCheckTimeout, TimeUnit.MILLISECONDS);
                        if (checkResult == null && log.isDebugEnabled()) {
                            long end = System.currentTimeMillis();
                            log.warn("Time:[" + (end - start)
                                     + "]check failed, need to check data server log...==>pid=" + physicalId);
                        }
                    }
                } catch (Exception e) {
                    log.error("check failed for node :" + physicalId);
                    log.error(e.getMessage(), e);
                    checkResult = null;
                }

                if (checkResult == null) {
                    nodeHealth = NodeHealth.NG;
                    log.error("Check result is null for node :" + physicalId);
                } else if (checkResult.isSuccess()) {
                    nodeHealth = NodeHealth.OK;
                } else {
                    nodeHealth = NodeHealth.NG;
                    log.warn("Check result is NG for node :" + physicalId + ", Message:" + checkResult.getMessage());
                }
            } catch (Exception e) {
                log.debug("fail to check node :" + physicalId, e);
                nodeHealth = NodeHealth.NG;
            }

            if (nodeHealth == NodeHealth.NG) {
                log.error("Check result is NG for node :" + physicalId);
            } else {
                chkRslt.setRetries(0);
            }

            int retries = chkRslt.getRetries();
            if (retries > 0) {
                if (log.isDebugEnabled()) {
                    log.debug("This is the " + (nodeCheckRetries - chkRslt.getRetries() + 1)
                              + "times' retry for node :" + physicalId);
                }
                chkRslt.setRetries(--retries);
                needReCheckResults.add(chkRslt);
            } else {
                NodeHealth originalNodeHealth = nodeCheckManager.checkNode(physicalId, false);
                if (log.isDebugEnabled()) {
                    log.debug("checkresult (previous) is \"" + originalNodeHealth + "\" for node  : pid=" + physicalId);
                }

                nodeCheckManager.updateNodeHealth(physicalId, nodeHealth);

                if (log.isDebugEnabled()) {
                    log.debug("checkresult is (current)\"" + nodeHealth + "\" for node  : pid=" + physicalId);
                }

                chkRslt.setCurrentNodeHealth(nodeHealth);
                chkRslt.setOriginalNodeHealth(originalNodeHealth);
            }
        }

        return needReCheckResults;
    }
}

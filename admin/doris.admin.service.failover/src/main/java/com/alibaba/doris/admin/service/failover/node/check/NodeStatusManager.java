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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.service.common.migrate.manager.PostMigrateStatusManager;
import com.alibaba.doris.admin.service.common.migrate.status.MigrateStatusWatcher;
import com.alibaba.doris.admin.service.common.migrate.status.PostMigrateStatus;
import com.alibaba.doris.admin.service.common.node.NodesManager;
import com.alibaba.doris.common.NodeRouteStatus;
import com.alibaba.doris.common.StoreNode;

/**
 * TODO Comment of NodeStatusManager
 * 
 * @author mian.hem
 */
public class NodeStatusManager {
    
    private static final Log         log              = LogFactory.getLog(NodeStatusManager.class);


    private static final NodeStatusManager instance             = new NodeStatusManager();

    private MigrateStatusWatcher           migrateStatusWatcher = MigrateStatusWatcher
                                                                        .getInstance();

    private NodesManager                   nodesManager         = NodesManager.getInstance();

    private NodeCheckManager               nodeCheckManager     = NodeCheckManager.getInstance();

    private NodeStatusManager() {
        super();
    }

    public static NodeStatusManager getInstance() {
        return instance;
    }

    public NodeAdminStatusWrapper getNodeAdminStatus(String nodePhysicalId) {
        NodeAdminStatusWrapper nodeAdminStatus = new NodeAdminStatusWrapper();
        nodeAdminStatus.setMigrateStatus(migrateStatusWatcher.getMigerateStatus(nodePhysicalId));
        nodeAdminStatus.setMigrateStatusDetail(migrateStatusWatcher
                .getMigerateStatusDetail(nodePhysicalId));
        nodeAdminStatus.setMigrateProgress(migrateStatusWatcher.getMigerateProgress(nodePhysicalId));
        PhysicalNodeDO pNode = NodesManager.getInstance().getNode(nodePhysicalId);
        NodeRouteStatus routeStatus = NodeRouteStatus.getTypeByValue(pNode.getStatus());
        nodeAdminStatus.setNodeRouteStatus(routeStatus);
        Map<String, NodeHealth> allNodeHealth =  nodeCheckManager.getNodeHealthStatuses();
       
        NodeHealth nodeHealth = allNodeHealth.get(nodePhysicalId);
        if (nodeHealth == null) {
            nodeHealth = nodeCheckManager.checkNode(nodePhysicalId);
        }
        nodeAdminStatus.setNodeHealth(nodeHealth);
        StoreNode node = nodesManager.getStoreNode(nodePhysicalId);
        nodeAdminStatus.setStoreNode(node);
        
        
        PostMigrateStatusManager postMigrateStatus = PostMigrateStatusManager.getInstance();
        PostMigrateStatus status = postMigrateStatus.getPostMigrateStatus(nodePhysicalId);
        if (status != null) {
            if (log.isDebugEnabled()) {
                log.debug("in the progress:clean up." + status + "pid=" +nodePhysicalId);
            }
            nodeAdminStatus.setMigrateStatus(status.getStatus());
            nodeAdminStatus.setMigrateStatusDetail(status.toString());
            nodeAdminStatus.setMigrateProgress(status.getSchedule());
        }
        
        return nodeAdminStatus;
    }

    public List<NodeAdminStatusWrapper> getAllNodeAdminStatus() {

        List<NodeAdminStatusWrapper> nodeAdminStatuses = new ArrayList<NodeAdminStatusWrapper>();

        Collection<PhysicalNodeDO> allNodes = nodesManager.getAllNodeList();

        for (PhysicalNodeDO pNode : allNodes) {
            NodeAdminStatusWrapper statusWrapper = getNodeAdminStatus(pNode.getPhysicalId());
            nodeAdminStatuses.add(statusWrapper);
        }
        return nodeAdminStatuses;
    }

}

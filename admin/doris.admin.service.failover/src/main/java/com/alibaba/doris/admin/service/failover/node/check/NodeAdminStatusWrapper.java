/**
 * Project: doris.admin.service.common-0.1.0-SNAPSHOT File Created at 2011-5-24 $Id$ Copyright 1999-2100 Alibaba.com
 * Corporation Limited. All rights reserved. This software is the confidential and proprietary information of Alibaba
 * Company. ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.doris.admin.service.failover.node.check;

import com.alibaba.doris.common.MigrateStatusEnum;
import com.alibaba.doris.common.NodeRouteStatus;
import com.alibaba.doris.common.StoreNode;

/**
 * TODO Comment of NodeAdminStatusWrapper
 * 
 * @author mian.hem
 */
public class NodeAdminStatusWrapper {

    private StoreNode         storeNode;

    private NodeRouteStatus   nodeRouteStatus;

    private MigrateStatusEnum migrateStatus;

    private NodeHealth        nodeHealth;

    private String            migrateStatusDetail;

    private int               migrateProgress;

    public String getMigrateStatusDetail() {
        return migrateStatusDetail;
    }

    public void setMigrateStatusDetail(String migrateStatusDetail) {
        this.migrateStatusDetail = migrateStatusDetail;
    }

    public NodeRouteStatus getNodeRouteStatus() {
        return nodeRouteStatus;
    }

    public void setNodeRouteStatus(NodeRouteStatus nodeRouteStatus) {
        this.nodeRouteStatus = nodeRouteStatus;
    }

    public MigrateStatusEnum getMigrateStatus() {
        return migrateStatus;
    }

    public void setMigrateStatus(MigrateStatusEnum migrateStatus) {
        this.migrateStatus = migrateStatus;
    }

    public NodeHealth getNodeHealth() {
        return nodeHealth;
    }

    public void setNodeHealth(NodeHealth nodeHealth) {
        this.nodeHealth = nodeHealth;
    }

    public int getMigrateProgress() {
        return migrateProgress;
    }

    public void setMigrateProgress(int migrateProgress) {
        this.migrateProgress = migrateProgress;
    }

    
    public StoreNode getStoreNode() {
        return storeNode;
    }

    
    public void setStoreNode(StoreNode storeNode) {
        this.storeNode = storeNode;
    }
    
    

}

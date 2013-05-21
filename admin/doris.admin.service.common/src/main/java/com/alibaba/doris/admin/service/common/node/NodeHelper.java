/**
 * Project: doris.admin.service.common-0.1.0-SNAPSHOT
 * 
 * File Created at 2011-6-2
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
package com.alibaba.doris.admin.service.common.node;

import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.common.NodeRouteStatus;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.StoreNodeSequenceEnum;

/**
 * TODO Comment of NodeHelper
 * 
 * @author mian.hem
 */
public class NodeHelper {
    
    public static StoreNode buildStoreNode(PhysicalNodeDO pNode) {
        StoreNode storeNode = new StoreNode();
        storeNode.setIp(pNode.getIp());
        storeNode.setLogicId(pNode.getLogicalId());
        storeNode.setPhId(pNode.getPhysicalId());
        storeNode.setPort(pNode.getPort());
        StoreNodeSequenceEnum sequence = StoreNodeSequenceEnum.getTypeByValue(pNode.getSerialId());
        storeNode.setSequence(sequence);
        NodeRouteStatus status = NodeRouteStatus.getTypeByValue(pNode.getStatus());
        storeNode.setStatus(status);
        storeNode.setURL(pNode.getIp() + ":" + pNode.getPort());
        return storeNode;
    }
}

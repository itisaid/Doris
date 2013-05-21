/**
 * Project: doris.admin.service.failover-0.1.0-SNAPSHOT
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
package com.alibaba.doris.admin.service.failover.check;

import java.util.Map;

import com.alibaba.doris.admin.service.common.AdminServiceAction;
import com.alibaba.doris.admin.service.failover.node.check.NodeCheckManager;
import com.alibaba.doris.admin.service.failover.node.check.NodeHealth;
import com.alibaba.doris.common.AdminServiceConstants;

/**
 * @author mian.hem
 */
public class AdminNodeCheckAction implements AdminServiceAction {

    private static final AdminNodeCheckAction instance = new AdminNodeCheckAction();

    private AdminNodeCheckAction() {
        super();
    }

    public static AdminNodeCheckAction getInstance() {
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.alibaba.doris.admin.service.common.AdminServiceAction#execute(java
     * .util.Map)
     */
    public String execute(Map<String, String> params) {
        String nodePhysicalId = params.get(AdminServiceConstants.STORE_NODE_PHYSICAL_ID);
        NodeHealth nodeHealth = NodeCheckManager.getInstance().checkNode(nodePhysicalId);
        Boolean result = (nodeHealth == NodeHealth.OK);
        return result.toString();
    }

}

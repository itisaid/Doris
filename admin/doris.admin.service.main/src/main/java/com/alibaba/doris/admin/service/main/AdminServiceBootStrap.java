/**
 * Project: doris.admin.service.main-0.1.0-SNAPSHOT
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
package com.alibaba.doris.admin.service.main;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.service.common.node.NodesManager;
import com.alibaba.doris.admin.service.common.route.RouteConfigProcessor;
import com.alibaba.doris.admin.service.failover.node.check.NodeCheckManager;

/**
 * TODO Comment of AdminServiceBootStrap
 * 
 * @author mian.hem
 */
public class AdminServiceBootStrap {
    private static final Log logger = LogFactory.getLog(AdminServiceBootStrap.class);

    public void start() {
        if (logger.isInfoEnabled()) {
            logger.info("AdminServiceBootStrap starts...");
        }
        boolean isMaster = AdminServiceLocator.getAdminService().isMasterAdmin();
        if (isMaster) { 
            if (logger.isInfoEnabled()) {
                logger.info("NodeCheckManager starts...");
            }
            NodeCheckManager.getInstance().start();
        }
        
        NodesManager.getInstance().start();
        
        
        RouteConfigProcessor.getInstance().start();
        
        if (logger.isInfoEnabled()) {
            logger.info("AdminServiceBootStrap ends...");
        }
    };

    public void stop() {
        if (logger.isInfoEnabled()) {
            logger.info("AdminServiceBootStrap stop starts...");
        }
        
        NodeCheckManager.getInstance().stop();
        NodesManager.getInstance().stop();
        RouteConfigProcessor.getInstance().stop();
        
        if (logger.isInfoEnabled()) {
            logger.info("AdminServiceBootStrap stopped...");
        }
    };
}

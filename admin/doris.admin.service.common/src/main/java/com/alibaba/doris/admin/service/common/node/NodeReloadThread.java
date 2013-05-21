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
package com.alibaba.doris.admin.service.common.node;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.service.PropertiesService;
import com.alibaba.doris.common.AdminServiceConstants;

/**
 * TODO Comment of NodeReloadThread
 * 
 * @author mian.hem
 */
public class NodeReloadThread extends Thread {
    private static PropertiesService propertyService    = AdminServiceLocator
                                                                .getPropertiesService();
    private static final long        nodeReloadInterval = propertyService
                                                                .getProperty(
                                                                        "nodeReloadInterval",
                                                                        Long.TYPE,
                                                                        AdminServiceConstants.NODE_DEFAULT_RELOAD_INTERVAL);
    private static final Log         log                = LogFactory.getLog(NodeReloadThread.class);
    private NodesManager             nodeManager        = null;

    protected volatile boolean       stopped            = false;

    public NodeReloadThread(NodesManager nodeManager) {
        super();
        if (nodeManager == null) {
            throw new IllegalStateException("nodeManager cannot be null!");
        }
        this.nodeManager = nodeManager;
    }

    public void run() {
        while (!stopped) {
            if (log.isDebugEnabled()) {
                log.debug("Node reload task starts...");
            }
            try {
                sleep(nodeReloadInterval);
                nodeManager.reLoadNodes();
                if (log.isDebugEnabled()) {
                    log.debug("Node reload task ends...");
                }
            } catch (Throwable e) {
                log.error("node reload thread exceptioin occurs." + e.getMessage(), e);
            }
        }
    }

    public synchronized void end() {
        nodeManager = null;
        stopped = true;
    }
}

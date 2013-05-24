/**
 * Project: doris.dataserver.monitor-0.1.0
 * 
 * File Created at 2011-12-19
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
package com.alibaba.doris.dataserver.monitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.common.util.IPAddressUtil;
import com.alibaba.doris.dataserver.ApplicationContext;
import com.alibaba.doris.dataserver.BaseModule;
import com.alibaba.doris.dataserver.Module;
import com.alibaba.doris.dataserver.ModuleContext;
import com.alibaba.doris.dataserver.config.ModuleConstances;
import com.alibaba.doris.dataserver.config.data.ModuleConfigure;
// import com.alibaba.dragoon.client.DragoonClient;

/**
 * @author mian.hem
 */
public class DragoonMonitorModule  extends BaseModule {
    private static final Log logger = LogFactory.getLog(DragoonMonitorModule.class);

    private int              serverPort;

    /*
     * (non-Javadoc)
     * @see
     * com.alibaba.doris.dataserver.Module#load(com.alibaba.doris.dataserver
     * .config.data.ModuleConfigure)
     */
    public void load(ModuleConfigure conf) {

        init();

//         DragoonClient.setJdbcStatEnable(false);
//         DragoonClient.setSpringStatEnable(false);
//         DragoonClient.setUriStatEnable(false);
//         DragoonClient.setLog4jStatEnable(true);

        String appName = conf.getParam("dragoon_app_name");
        logger.info("Dragoon app name:" + appName + ", and port is: " + serverPort);
        String ipaddress = "";
        try {
          ipaddress =  IPAddressUtil.getIPAddress();
        } catch (Exception e ) {
            logger.error("fail to get ip address", e);
        }
//        DragoonClient.start(appName, ipaddress + ":" + serverPort);

        logger.info("Dragoon monitor completed for port:" + serverPort);
    }

    private void init() {
        this.setName("Dragoon Monitor Module");
        ModuleContext  moduleContext =   super.getModuleContext();
        if (null != moduleContext) {
            ApplicationContext appContext = moduleContext.getApplicationContext();
            Module module = appContext.getModuleByName(ModuleConstances.NETWORK_MODULE);
            ModuleContext netWorkModuleContext = module.getModuleContext();
            if (netWorkModuleContext == null) {
                throw new RuntimeException("netWorkModuleContext not found");
            }
            
            int port = (Integer) netWorkModuleContext.getAttribute("serverPort");
            this.serverPort = port;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.doris.dataserver.Module#unload()
     */
    public void unload() {

    }
}

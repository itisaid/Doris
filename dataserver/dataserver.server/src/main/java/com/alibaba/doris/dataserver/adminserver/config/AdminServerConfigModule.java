/**
 * Project: doris.dataserver.server-0.1.0-SNAPSHOT File Created at 2011-6-2 $Id$ Copyright 1999-2100 Alibaba.com
 * Corporation Limited. All rights reserved. This software is the confidential and proprietary information of Alibaba
 * Company. ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.doris.dataserver.adminserver.config;

import org.apache.commons.lang.StringUtils;

import com.alibaba.doris.common.config.ConfigException;
import com.alibaba.doris.common.config.ConfigManager;
import com.alibaba.doris.common.config.ConfigManagerImpl;
import com.alibaba.doris.common.configer.RouteTableConfiger;
import com.alibaba.doris.common.event.RouteConfigListener;
import com.alibaba.doris.common.route.RouteTable;
import com.alibaba.doris.common.router.service.RouteStrategyImpl;
import com.alibaba.doris.common.router.service.RouteTableConfigerImpl;
import com.alibaba.doris.dataserver.BaseModule;
import com.alibaba.doris.dataserver.DataServerException;
import com.alibaba.doris.dataserver.FatalModuleInitializationException;
import com.alibaba.doris.dataserver.ModuleContext;
import com.alibaba.doris.dataserver.ModuleContextAware;
import com.alibaba.doris.dataserver.config.data.ModuleConfigure;

/**
 * TODO Comment of AdminServerConfigModule
 * 
 * @author mian.hem
 */
public class AdminServerConfigModule extends BaseModule implements ModuleContextAware {

    private RouteTableConfiger routeTableConfiger = null;
    private ConfigManager      configManager      = null;

    public void load(ModuleConfigure conf) {
        String configLocation = conf.getParam("adminserver.configuration.location");
        if (StringUtils.isEmpty(configLocation)) {
            throw new FatalModuleInitializationException("property \"adminserver.configuration.location\" is not set");
        }

        configManager = new ConfigManagerImpl();
        configManager.setConfigLocation(configLocation);
        try {
            configManager.initConfig();
        } catch (ConfigException e) {
            throw new FatalModuleInitializationException("init config manager failed.", e);
        }

        // TODO:add the correct listener.
        this.routeTableConfiger = new RouteTableConfigerImpl();
        RouteConfigListener routeStrategy = new RouteStrategyImpl();

        routeTableConfiger.addConfigListener(routeStrategy);
        configManager.addConfigListener(routeTableConfiger);

        ModuleContext moduleContext = getModuleContext();
        // add to applicationContext
        moduleContext.getApplicationContext().setAttribute("configManager", configManager);
        moduleContext.getApplicationContext().setAttribute("routeTableConfiger", routeTableConfiger);

        // TODO: if not used remove below.
        moduleContext.setAttribute("routeTableConfiger", routeTableConfiger);
    }

    public void unload() {
        configManager.removeConfigListener(routeTableConfiger);
    }

    /**
     * Returns the route table will is fetched from admin server by interval.
     */
    public RouteTable getRouteTable() {
        if (routeTableConfiger == null) {
            throw new DataServerException("AdminServerConfigModule is not loaded correctly.");
        }
        return routeTableConfiger.getRouteTable();
    }
}

package com.alibaba.doris.common.router.service;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.doris.common.adminservice.impl.RouteConfigServiceImpl;
import com.alibaba.doris.common.config.ConfigManager;
import com.alibaba.doris.common.configer.RouteTableConfiger;
import com.alibaba.doris.common.event.RouteConfigChangeEvent;
import com.alibaba.doris.common.event.RouteConfigListener;
import com.alibaba.doris.common.route.RouteTable;

public class RouteTableConfigerImpl implements RouteTableConfiger {
    private List<RouteConfigListener> listeners = new ArrayList<RouteConfigListener>();
    private RouteTable                routeTable;
    private long                      configVersion;
    private ConfigManager             configManager;

    public RouteTableConfigerImpl() {
    }

    public String getConfigListenerName() {
        return "routeConfig";
    }

    public void addConfigListener(RouteConfigListener routeConfigListener) {
        listeners.add(routeConfigListener);
    }

    public List<RouteConfigListener> getConfigListeners() {
        return listeners;
    }

    public RouteTable getRouteTable() {
        return routeTable;
    }

    public synchronized RouteTable getRouteTable(boolean t) {
        if (t) {
            routeTable = RouteConfigParser.parse(RouteConfigServiceImpl.getInstance()
                    .getRouteConfig());
            return routeTable;
        } else {
            return routeTable;
        }
    }

    public void initConfig() {
    }

    public void setConfigLocation(String configLocation) {
        // nothing
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public synchronized void onConfigChange(String configContent) {

        RouteTable tempRouteTable = RouteConfigParser.parse(configContent);

		if (routeTable != null
				&& (tempRouteTable == null || (tempRouteTable.getVersion() == this.routeTable
						.getVersion()))) {
			return;
		}

        RouteConfigChangeEvent event = new RouteConfigChangeEvent();
        event.setRouteTable(tempRouteTable);

        for (RouteConfigListener listener : listeners) {
            listener.onConfigChange(event);
        }
        
        this.routeTable = tempRouteTable;
        this.configVersion = routeTable.getVersion();
    }

    public Long getConfigVersion() {
        return this.configVersion;
    }
}

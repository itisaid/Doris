package com.alibaba.doris.common.adminservice.impl;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.adminservice.BaseAdminService;
import com.alibaba.doris.common.adminservice.RouterConfigService;

public class RouteConfigServiceImpl extends BaseAdminService<String> implements RouterConfigService {

    private static RouterConfigService instance = new RouteConfigServiceImpl();

    private RouteConfigServiceImpl() {
    }

    public static RouterConfigService getInstance() {
        return instance;
    }

    public String convert(String response) {
        return response;
    }

    @Override
    public String getActionName() {
        return AdminServiceConstants.ROUTE_CONFIG_ACTION;
    }

    public String getRouteConfig() {
        return requestRefresh(new HashMap<String, String>());
    }

    public String getRouteConfig(long version) {
        Map<String, String> paras = new HashMap<String, String>();
        paras.put("configVersion", String.valueOf(version));
        return requestForce(paras);
    }
}

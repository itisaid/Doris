package com.alibaba.doris.common.adminservice.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.adminservice.BaseAdminService;
import com.alibaba.doris.common.adminservice.CommonConfigService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class CommonConfigServiceImpl extends BaseAdminService<Map<String, String>> implements
        CommonConfigService {

    public static final CommonConfigService instance = new CommonConfigServiceImpl();

    private CommonConfigServiceImpl() {

    }

    public static CommonConfigService getInstance() {
        return instance;
    }

    /**
     * any value inside this map cannot be null; otherwise, treated as no
     * version specified.
     */
    public Map<String, String> getConfig(Map<String, Long> actionVersions) {
        Map<String, String> paras = new HashMap<String, String>();
        String actionName = StringUtils.join(actionVersions.keySet(), ",");
        String versions = StringUtils.join(actionVersions.values(), ",");
        paras.put(AdminServiceConstants.COMMON_CONFIG_ACTION_ITEMS, actionName);
        paras.put(AdminServiceConstants.COMMON_CONFIG_VERSION_ITEMS, versions);
        return requestForce(paras);
    }

    @Override
    public Map<String, String> convert(String response) {
        if (response == null) {
            return new HashMap<String, String>();
        }
		TypeReference<Map<String, String>> mtf = new TypeReference<Map<String, String>>() {};
		return JSON.parseObject(response, mtf);
    }

    @Override
    public String getActionName() {
        return AdminServiceConstants.COMMON_CONFIG_ACTION;
    }

    public Map<String, String> getConfig(List<String> actions) {
        Map<String, Long> paras = new HashMap<String, Long>();
        for (String action : actions) {
            paras.put(action, null);
        }
        return getConfig(paras);
    }
    
    
}


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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.doris.admin.service.common.AdminServiceAction;
import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.fastjson.JSON;

/**
 * TODO Comment of CommonConfigServiceAction
 * 
 * @author mian.hem
 */
public class CommonConfigServiceAction implements AdminServiceAction {

    private static CommonConfigServiceAction instance = new CommonConfigServiceAction();

    private CommonConfigServiceAction() {

    }

    public static CommonConfigServiceAction getInstance() {
        return instance;
    }

    public String execute(Map<String, String> params) {
        String subActions = params.get(AdminServiceConstants.COMMON_CONFIG_ACTION_ITEMS);
        String subVersions = params.get(AdminServiceConstants.COMMON_CONFIG_VERSION_ITEMS);

        String[] actions = null;
        if (StringUtils.isNotEmpty(subActions)) {
            actions = subActions.split(",");
        }

        if (actions.length == 0) {
            return null;
        }
        
        String[] versions = null;
        if (StringUtils.isNotEmpty(subVersions)) {
            versions = subVersions.split(",");
        }

        boolean noVersion = (versions == null 
                || (versions.length != actions.length));

        Map<String, String> resultMap = new HashMap<String, String>();
        for (int i = 0; i < actions.length; i++) {
            String actionName = actions[i];
            if(noVersion) {
                params.put(AdminServiceConstants.CONFIG_VERSION, "0");
            } else {
                params.put(AdminServiceConstants.CONFIG_VERSION, StringUtils.isEmpty(versions[i])? "0" : versions[i]);
            }
            AdminServiceAction action = AdminServiceActionFactory.getAdminServiceAction(actionName);
            String resultItem = action.execute(params);
            resultMap.put(actionName, resultItem);
        }

        return JSON.toJSONString(resultMap);
    }

}

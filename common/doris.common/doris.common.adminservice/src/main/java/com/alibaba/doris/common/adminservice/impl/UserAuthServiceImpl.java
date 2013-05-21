/**
 * Project: doris.common.adminservice-0.1.0-SNAPSHOT
 * 
 * File Created at 2011-6-30
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
package com.alibaba.doris.common.adminservice.impl;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.adminservice.BaseAdminService;
import com.alibaba.doris.common.adminservice.UserAuthService;

/**
 * @author mian.hem
 */
public class UserAuthServiceImpl extends BaseAdminService<Integer> implements UserAuthService {

    private static UserAuthServiceImpl instance = new UserAuthServiceImpl();
    
    private UserAuthServiceImpl() {
        
    }
    
    public static UserAuthServiceImpl getInstance() {
        return instance;
    }
    
    public int getUserAuth(String userName, String password) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(AdminServiceConstants.USER_AUTH_USER_NAME, userName);
        paramMap.put(AdminServiceConstants.USER_AUTH_PASSWORD, password);
        return super.requestForce(paramMap);
    }

    @Override
    public Integer convert(String response) {
        if (response == null) {
            return -1;
        }
        if( response.length() > 2) {
        	return -1;
        }
        return Integer.parseInt(response);
    }

    @Override
    public String getActionName() {
        return AdminServiceConstants.USER_AUTH_ACTION;
    }

}

/**
 * Project: doris.common.adminservice-0.1.0-SNAPSHOT
 * 
 * File Created at 2011-5-26
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
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.adminservice.BaseAdminService;
import com.alibaba.doris.common.adminservice.CheckFailService;
import com.alibaba.fastjson.JSON;

/**
 * TODO Comment of CheckFailServiceImpl
 * 
 * @author mian.hem
 */
public class CheckFailServiceImpl extends BaseAdminService<Boolean> implements CheckFailService {

    public static final CheckFailService instance = new CheckFailServiceImpl();

    private CheckFailServiceImpl() {

    }

    public static CheckFailService getInstance() {
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.alibaba.doris.common.adminservice.CheckFailService#checkFailNode(
     * com.alibaba.doris.common.StoreNode)
     */
    public boolean checkFailNode(StoreNode sn) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(AdminServiceConstants.STORE_NODE_PHYSICAL_ID, sn.getPhId());
        return super.requestForce(paramMap);

    }

    @Override
    public Boolean convert(String response) {
        if (response == null) {
            return Boolean.TRUE;
        }
        return Boolean.parseBoolean(response);
    }

    @Override
    public String getActionName() {
        return AdminServiceConstants.NODE_CHECK_ACTION;
    }

}

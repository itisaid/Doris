/**
 * Project: doris.config.server-1.0-SNAPSHOT
 * 
 * File Created at 2011-4-27
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
package com.alibaba.doris.admin.dao.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.alibaba.doris.admin.dao.ConfigInstanceDao;
import com.alibaba.doris.admin.dataobject.RouterConfigInstanceDO;

/**
 * TODO Comment of ConfigInstanceDao
 * 
 * @author mianhe
 */
public class ConfigInstanceDaoImpl extends SqlMapClientDaoSupport implements ConfigInstanceDao {

    public RouterConfigInstanceDO loadLatestConfigInstance() {
        RouterConfigInstanceDO latestConfigInstance = (RouterConfigInstanceDO) getSqlMapClientTemplate()
                .queryForObject("ConfigInstance.readLatestById");
        return latestConfigInstance;
    }

    public int insertConfigInstance(RouterConfigInstanceDO configInstanceDO) {
        Integer latestConfigInstanceId = (Integer) getSqlMapClientTemplate().insert(
                "ConfigInstance.insertConfigInstance", configInstanceDO);
        return latestConfigInstanceId;
    }

}

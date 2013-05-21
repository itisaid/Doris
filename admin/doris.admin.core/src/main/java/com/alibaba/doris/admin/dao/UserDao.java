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
package com.alibaba.doris.admin.dao;

import com.alibaba.doris.admin.dataobject.UserDO;

/**
 * @author mianhe
 *
 */
public interface UserDao {

    public UserDO findUserByName(String userName);
    
}

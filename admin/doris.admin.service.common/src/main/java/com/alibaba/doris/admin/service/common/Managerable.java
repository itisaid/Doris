/**
 * Project: doris.admin.service.failover-0.1.0-SNAPSHOT
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
package com.alibaba.doris.admin.service.common;

/**
 * Interface gives start and stop service functionality.
 */
public interface Managerable {

    /**
     * Starts manager service.
     */
    void start();

    /**
     * Stop manager service.
     */
    void stop();
}

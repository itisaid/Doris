/**
 * Project: doris.config.client-1.0-SNAPSHOT
 * 
 * File Created at 2011-4-26
 * $Id: ConfigException.java 88531 2011-05-11 04:31:32Z frank.lizh $
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
package com.alibaba.doris.common.config;

/**
 * Doris 客户端异常
 * 
 * @author mianhe
 *
 */
public class ConfigException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 4354224559101400655L;

    /**
     * @param errMsg
     * @param e
     */
    public ConfigException(String errMsg, Exception e) {
        super(errMsg, e);
    }

    /**
     * @param string
     */
    public ConfigException(String errMsg) {
        super(errMsg);
    }

}

/**
 * Project: testJava
 * 
 * File Created at 2011-8-4
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
package com.alibaba.doris.client.tools.importdata;

/**
 * TODO Comment of KeyValue
 * 
 * @author luyi.huangly
 */
public class KeyPair {

    /**
     * @param key
     * @param value
     */
    public KeyPair(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }

    private String key;
    private String value;

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

}

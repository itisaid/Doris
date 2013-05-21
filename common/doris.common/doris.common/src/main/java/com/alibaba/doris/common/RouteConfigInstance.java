/**
 * Project: doris.common-0.1.0-SNAPSHOT
 * 
 * File Created at 2011-5-18
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
package com.alibaba.doris.common;

import java.util.List;

/**
 * TODO Comment of RouteConfigInstance
 * @author mian.hem
 *
 */
public class RouteConfigInstance {

    private long version;
    
    private List<StoreNode> storeNodes;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public List<StoreNode> getStoreNodes() {
        return storeNodes;
    }

    public void setStoreNodes(List<StoreNode> storeNodes) {
        this.storeNodes = storeNodes;
    }
}

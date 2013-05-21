package com.alibaba.doris.common.adminservice;

import com.alibaba.doris.common.StoreNode;

public interface StoreNodeService {

    StoreNode getStoreNode(String physicalId);
}

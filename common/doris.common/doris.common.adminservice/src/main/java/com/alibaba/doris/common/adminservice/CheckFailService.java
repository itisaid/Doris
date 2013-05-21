package com.alibaba.doris.common.adminservice;

import com.alibaba.doris.common.StoreNode;


public interface CheckFailService {

    /**
     * 
     * @param sn
     * @return true 节点可用，false 节点不可用
     */
    boolean checkFailNode(StoreNode sn);
}

package com.alibaba.doris.common.adminservice.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.adminservice.BaseAdminService;
import com.alibaba.doris.common.adminservice.StoreNodeService;
import com.alibaba.fastjson.JSON;

/**
 * 从admin获取一个StoreNode
 * 
 * @author frank
 */
public class StoreNodeServiceImpl extends BaseAdminService<StoreNode> implements StoreNodeService {

    private static StoreNodeServiceImpl instance = new StoreNodeServiceImpl();

    private Map<String, StoreNode>      snMap    = new ConcurrentHashMap<String, StoreNode>();

    private StoreNodeServiceImpl() {

    }

    public static StoreNodeServiceImpl getInstance() {
        return instance;
    }

    public StoreNode getStoreNode(String physicalId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AdminServiceConstants.STORE_NODE_PHYSICAL_ID, physicalId);
        StoreNode sn = requestForce(params);
        if (sn != null) {
            snMap.put(physicalId, sn);
            return sn;
        } else {
            StoreNode tsn = snMap.get(physicalId);
            if (tsn != null) {
                return tsn;
            }
        }
        return null;
    }

    @Override
    public StoreNode convert(String response) {
        return JSON.parseObject(response, StoreNode.class);
    }

    @Override
    public String getActionName() {
        return AdminServiceConstants.STORE_NODE_ACTION;
    }

}

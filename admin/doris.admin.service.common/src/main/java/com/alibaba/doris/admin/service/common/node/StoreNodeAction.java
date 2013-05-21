package com.alibaba.doris.admin.service.common.node;

import java.util.Map;

import com.alibaba.doris.admin.service.common.AdminServiceAction;
import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.fastjson.JSON;

public class StoreNodeAction implements AdminServiceAction {

    private StoreNodeAction() {

    }

    private static final StoreNodeAction instance = new StoreNodeAction();

    public static StoreNodeAction getInstance() {
        return instance;
    }

    public String execute(Map<String, String> params) {
        String physicalId = params.get(AdminServiceConstants.STORE_NODE_PHYSICAL_ID);
        StoreNode storeNode = NodesManager.getInstance().getStoreNode(physicalId);
        return JSON.toJSONString(storeNode);
    }

}

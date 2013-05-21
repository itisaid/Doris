package com.alibaba.doris.admin.service.common.virtual;

import java.util.Map;

import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.service.common.AdminServiceAction;

public class VirtualNumberAction implements AdminServiceAction {

    private VirtualNumberAction() {

    }

    private static final VirtualNumberAction instance = new VirtualNumberAction();

    public static VirtualNumberAction getInstance() {
        return instance;
    }

    private String virtualNodeNumber = null;

    public String execute(Map<String, String> params) {
        if (virtualNodeNumber == null) {
            virtualNodeNumber = String.valueOf(AdminServiceLocator.getVirtualNodeService()
                    .getVirtualNodeNum());
        }
        return virtualNodeNumber;
    }

}

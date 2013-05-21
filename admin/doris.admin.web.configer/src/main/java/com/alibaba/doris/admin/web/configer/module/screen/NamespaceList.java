package com.alibaba.doris.admin.web.configer.module.screen;

import java.util.List;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.NamespaceDO;
import com.alibaba.doris.admin.service.AdminService;
import com.alibaba.doris.admin.service.NamespaceService;

public class NamespaceList {

    NamespaceService namespaceService = AdminServiceLocator.getNamespaceService();
    AdminService     adminService     = AdminServiceLocator.getAdminService();

    public void execute(Context context) {
        List<NamespaceDO> namespaceList = namespaceService.queryUsableNamespaces();
        boolean isMasterAdmin = adminService.isMasterAdmin();
        context.put("isMasterAdmin", isMasterAdmin);
        context.put("namespaceList", namespaceList);
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

}

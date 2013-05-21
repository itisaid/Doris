package com.alibaba.doris.admin.web.configer.module.screen;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.service.AdminService;

public class NamespaceAdd {

    AdminService adminService = AdminServiceLocator.getAdminService();

    public void execute(Context context) {
        boolean isMasterAdmin = adminService.isMasterAdmin();
        context.put("isMasterAdmin", Boolean.toString(isMasterAdmin));
    }
}

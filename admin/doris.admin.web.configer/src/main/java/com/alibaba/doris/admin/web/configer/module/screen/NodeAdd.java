package com.alibaba.doris.admin.web.configer.module.screen;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.service.AdminService;

/**
 * @project :Doris
 * @author : len.liu
 * @datetime : 2011-7-8 下午02:21:25
 * @version :0.1
 * @Modification:
 */
public class NodeAdd {

    AdminService adminService = AdminServiceLocator.getAdminService();

    public void execute(Context context) {
        boolean isMasterAdmin = adminService.isMasterAdmin();
        context.put("isMasterAdmin", Boolean.toString(isMasterAdmin));
    }
}

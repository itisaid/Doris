package com.alibaba.doris.admin.web.configer.module.screen;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.service.AdminNodeService;
import com.alibaba.doris.admin.service.AdminService;
import com.alibaba.turbine.module.screen.TemplateScreen;

/**
 * 编辑节点信息，暂时不需要加入权限控制，doris 权限体系做完后需要加入权限控制
 * 
 * @author mian.hem
 */
public class NodeEdit extends TemplateScreen {

    AdminNodeService adminNodeService = AdminServiceLocator.getAdminNodeService();
    AdminService adminService = AdminServiceLocator.getAdminService();
    
    public void execute(Context context, HttpServletRequest request) {
        String nodeId = request.getParameter("nodeId");
        PhysicalNodeDO physicalNode = adminNodeService.queryPhysicalNodeById(Integer
                .valueOf(nodeId));
        context.put("node", physicalNode);

        boolean isMasterAdmin = adminService.isMasterAdmin();
        context.put("isMasterAdmin", Boolean.toString(isMasterAdmin));
    }
}

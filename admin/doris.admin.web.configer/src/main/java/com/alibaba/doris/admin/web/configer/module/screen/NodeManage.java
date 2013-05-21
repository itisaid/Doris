package com.alibaba.doris.admin.web.configer.module.screen;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.service.AdminNodeService;
import com.alibaba.doris.admin.service.AdminService;
import com.alibaba.doris.admin.web.configer.support.SequenceForView;
import com.alibaba.doris.admin.web.configer.util.PhysicalNodeUtil;
import com.alibaba.doris.admin.web.configer.util.SequenceUtil;
import com.alibaba.doris.admin.web.configer.util.WebConstant;

/**
 * @project :Doris
 * @author : len.liu
 * @datetime : 2011-6-2 下午04:54:00
 * @version :0.1
 * @Modification:
 */
public class NodeManage {

    AdminNodeService adminNodeService = AdminServiceLocator.getAdminNodeService();

    AdminService     adminService     = AdminServiceLocator.getAdminService();

    /**
     * @param context
     */
    public void execute(Context context, HttpServletRequest request) {
        List<PhysicalNodeDO> physicalNodeList = adminNodeService.queryAllPhysicalNodes();

        Map<Integer, SequenceForView> nodemap = PhysicalNodeUtil.getSortedNodeMap(physicalNodeList);
        Map<Integer, String> noMigrateSequenceMap = SequenceUtil.getNoMigrateSequenceMap();
        Map<Integer, String> allSequenceMap = SequenceUtil.getAllSequenceMap();
        boolean isMasterAdmin = adminService.isMasterAdmin();
        context.put("isMasterAdmin", Boolean.toString(isMasterAdmin));
        context.put("migrateSequenceMap", SequenceUtil.getMigrateSequenceMap());
        context.put("nodemap", nodemap);
        context.put("errorResult", request.getParameter("errorResult"));
        context.put(WebConstant.NO_MIGRATE_SEQUENCE_IDS_KEY, noMigrateSequenceMap);
        context.put(WebConstant.ALL_SEQUENCE_IDS_KEY, allSequenceMap);
    }
}

package com.alibaba.doris.admin.web.configer.module.action;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.citrus.service.form.CustomErrors;
import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.FormField;
import com.alibaba.citrus.turbine.dataresolver.FormGroup;
import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.service.AdminNodeService;
import com.alibaba.doris.admin.service.NodeValidatorService;
import com.alibaba.doris.admin.service.common.node.NodesManager;
import com.alibaba.doris.admin.web.configer.util.WebConstant;

public class NodeAddAction {

    AdminNodeService     adminNodeService     = AdminServiceLocator.getAdminNodeService();
    NodesManager         nodesManager         = NodesManager.getInstance();
    NodeValidatorService nodeValidatorService = AdminServiceLocator.getNodeValidatorService();
    

    /**
     * XXME 相同的MachineID下面的Node，不可以分布在不同的序列里面，即一台物理机里面的Node肯定是分布在同一个序列里面？ XXME
     * 另外，在新增Node的时候，不需要给LogincID、SerialID及Status赋值，那么是否应该将DB中的整数类型设置为VARCHAR？LogicID的自然有序需要教授在save DB的时候来保证
     */
    public void doAddNode(@FormGroup("physicalNode") PhysicalNodeDO physicalNodeDO,
                          @FormField(name = "physicalIdError", group = "physicalNode") CustomErrors physicalIdErr,
                          @FormField(name = "ipAndPortError", group = "physicalNode") CustomErrors ipAndPorterr,
                          Navigator nav, Context context) {
        // XXME 需要保证逻辑ID的自然有序
        boolean isExistPhysicalId = nodeValidatorService.checkPhysicalIdExist(physicalNodeDO);
        if (isExistPhysicalId) {
            ipAndPorterr.setMessage("ipAndPortRepeated");
            return;
        }
        physicalNodeDO.setPhysicalId(physicalNodeDO.getIp() + ":" + physicalNodeDO.getPort());
        adminNodeService.addPhysicalNode(physicalNodeDO);
        nodesManager.reLoadNodes();
        
        // XXME 需要处理保存失败的跳转以及提示等|len.liu
        nav.redirectTo(WebConstant.NODE_MANAGE_LIST_LINK);
    }
    
    
    public void doEditNode(@FormGroup("physicalNode") PhysicalNodeDO physicalNodeDO,
                           @FormField(name = "physicalIdError", group = "physicalNode") CustomErrors physicalIdErr,
                           @FormField(name = "ipAndPortError", group = "physicalNode") CustomErrors ipAndPorterr,
                           Navigator nav, Context context, HttpServletRequest request) {
         // XXME 需要保证逻辑ID的自然有序
         Integer nodeId = Integer.parseInt(request.getParameter("nodeId"));
         physicalNodeDO.setId(nodeId);
         
         boolean isExistPhysicalId = nodeValidatorService.checkPhysicalIdExist4Edit(physicalNodeDO);
         if (isExistPhysicalId) {
             ipAndPorterr.setMessage("ipAndPortRepeated");
             return;
         }
         
         
         physicalNodeDO.setPhysicalId(physicalNodeDO.getIp() + ":" + physicalNodeDO.getPort());
         
         adminNodeService.updatePhysicalNodeByNodeId(physicalNodeDO);
         nodesManager.reLoadNodes();
         
         // XXME 需要处理保存失败的跳转以及提示等|len.liu
         nav.redirectTo(WebConstant.NODE_MANAGE_LIST_LINK);
     }
}

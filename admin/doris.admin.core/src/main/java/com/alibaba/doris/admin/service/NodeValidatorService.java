package com.alibaba.doris.admin.service;

import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;

/**
 * @project :Doris
 * @author : len.liu
 * @datetime : 2011-6-14 下午01:12:26
 * @version :0.1
 * @Modification:
 */
public class NodeValidatorService {

    AdminNodeService adminNodeService;

    /**
     * 检查新增的Node的PhysicalId(IP:Port)是否已经存在
     * 
     * @param physicalNodeDo:必须包含PhysicalId，IP，Port信息
     * @return
     */
    public boolean checkPhysicalIdExist(PhysicalNodeDO physicalNodeDo) {
        PhysicalNodeDO nodeDo = adminNodeService.queryPhysicalNodesByConditions(physicalNodeDo);
        if (nodeDo == null) {
            return false;
        } else {
            return true;
        }
    }
    
    
    public boolean checkPhysicalIdExist4Edit(PhysicalNodeDO physicalNodeDo) {
        PhysicalNodeDO nodeDo = adminNodeService.queryDuplicateNodesForEdit(physicalNodeDo);
        return (nodeDo != null);
    }

    public void setAdminNodeService(AdminNodeService adminNodeService) {
        this.adminNodeService = adminNodeService;
    }

}

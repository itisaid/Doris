package com.alibaba.doris.admin.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.doris.admin.dao.PhysicalNodeDao;
import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.service.AdminNodeService;
import com.alibaba.doris.common.StoreNodeSequenceEnum;

/**
 * @project :doris
 * @author : len.liu
 * @datetime : 2011-5-12 下午10:14:06
 * @version :0.1
 * @Modification:
 */
@Service
public class AdminNodeServiceImp implements AdminNodeService {

    private PhysicalNodeDao physicalNodeDao;

    public Integer addPhysicalNode(PhysicalNodeDO phsicalNodeDo) {
        return physicalNodeDao.addPhysicalNode(phsicalNodeDo);
    }

    public List<PhysicalNodeDO> queryAllPhysicalNodes() {
        return physicalNodeDao.queryAllPhysicalNodes();
    }

    public List<PhysicalNodeDO> queryAllUsablePhysicalNodes() {
        return physicalNodeDao.queryAllUsablePhysicalNodes();
    }

    public PhysicalNodeDO queryPhysicalNodeByPhysicalId(String physicalId) {
        return physicalNodeDao.queryPhysicalNodeByPhysicalId(physicalId);
    }

    public List<PhysicalNodeDO> queryPhysicalNodesBySerialId(String serialId) {
        return physicalNodeDao.queryPhysicalNodesBySerialId(serialId);
    }

    public List<PhysicalNodeDO> queryUnUsablePhysicalNodes() {
        return physicalNodeDao.queryUnUsablePhysicalNodes();
    }

    public void updatePhysicalNodeStatus(String id, int status) {
        physicalNodeDao.updatePhysicalNodeStatus(id, status);

    }

    public PhysicalNodeDO queryPhysicalNodesByConditions(PhysicalNodeDO physicalNodeDO) {
        return physicalNodeDao.queryPhysicalNodesByConditions(physicalNodeDO);
    }

    public void setPhysicalNodeDao(PhysicalNodeDao physicalNodeDao) {
        this.physicalNodeDao = physicalNodeDao;
    }

    public PhysicalNodeDO queryPhysicalNodeById(Integer id) {
        return physicalNodeDao.queryPhysicalNodeById(id);
    }

    public void updatePhysicalNode(PhysicalNodeDO physicalNodeDO) {
        physicalNodeDao.updatePhysicalNode(physicalNodeDO);
    }

    public void updatePhysicalNodeList(List<PhysicalNodeDO> physicalNodeDoList) {
        physicalNodeDao.updatePhysicalNodeList(physicalNodeDoList);

    }

    public void deletePhysicalNode(String physicalId) {
        physicalNodeDao.deletePhysicalNode(physicalId);
    }

    public List<PhysicalNodeDO> queryNomalPhysicalNodesByIP(String IP) {
        return physicalNodeDao.queryNomalPhysicalNodesByIP(IP);
    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.doris.admin.service.AdminNodeService#isLegalMigrate(java.lang.String, java.lang.String)
     */
    public boolean checkLegalMigrateByIP(String IP, StoreNodeSequenceEnum targetSequence) {
        Map<String, StoreNodeSequenceEnum> checkMap = new HashMap<String, StoreNodeSequenceEnum>();
        List<PhysicalNodeDO> nodeList = queryNomalPhysicalNodesByIP(IP);
        if (nodeList != null && nodeList.size() > 0) {
            checkMap.put(IP, StoreNodeSequenceEnum.getTypeByValue(nodeList.get(0).getSerialId()));
        } else {
            return true;
        }
        if (StoreNodeSequenceEnum.isNormalSequence(targetSequence) && checkMap.get(IP) != null
            && !checkMap.get(IP).equals(targetSequence)) {
            return false;
        } else {
            checkMap.put(IP, targetSequence);
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.doris.admin.service.AdminNodeService#checkLegalMigrateByPhysicalId(java.lang.String,
     * java.lang.String)
     */
    public boolean checkLegalMigrateByPhysicalId(String physicalId, StoreNodeSequenceEnum targetSequence) {
        PhysicalNodeDO node = queryPhysicalNodeByPhysicalId(physicalId);
        if (node == null) return false;
        String IP = node.getIp();
        return checkLegalMigrateByIP(IP, targetSequence);
    }

    public PhysicalNodeDO queryDuplicateNodesForEdit(PhysicalNodeDO physicalNodeDO) {
        return physicalNodeDao.queryDuplicateNodesForEdit(physicalNodeDO);
    }

    public void updatePhysicalNodeByNodeId(PhysicalNodeDO physicalNodeDO) {
        physicalNodeDao.updatePhysicalNodeByNodeId(physicalNodeDO);        
    }

}

/**
 * Project: doris.config.server-1.0-SNAPSHOT File Created at 2011-4-27 $Id$ Copyright 1999-2100 Alibaba.com Corporation
 * Limited. All rights reserved. This software is the confidential and proprietary information of Alibaba Company.
 * ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.doris.admin.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.alibaba.doris.admin.dao.PhysicalNodeDao;
import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;

/**
 * TODO Comment of PhysicalNodeDao
 * 
 * @author mianhe
 */
public class PhysicalNodeDaoImpl extends SqlMapClientDaoSupport implements PhysicalNodeDao {


    public Integer addPhysicalNode(PhysicalNodeDO phsicalNodeDo) {
        return (Integer) getSqlMapClientTemplate().insert("PhysicalNode.addPhysicalNode",
                phsicalNodeDo);

    }

    @SuppressWarnings("unchecked")
    public List<PhysicalNodeDO> queryAllPhysicalNodes() {
        return (List<PhysicalNodeDO>) getSqlMapClientTemplate().queryForList(
                "PhysicalNode.listPhysicalNodes");
    }

    @SuppressWarnings("unchecked")
    public List<PhysicalNodeDO> queryAllUsablePhysicalNodes() {
        return (List<PhysicalNodeDO>) getSqlMapClientTemplate().queryForList(
                "PhysicalNode.queryAllUsablePhysicalNodes");
    }

    @SuppressWarnings("unchecked")
    public List<PhysicalNodeDO> queryUnUsablePhysicalNodes() {
        return (List<PhysicalNodeDO>) getSqlMapClientTemplate().queryForList(
                "PhysicalNode.queryUnUsablePhysicalNodes");
    }

    @SuppressWarnings("unchecked")
    public List<PhysicalNodeDO> queryPhysicalNodesBySerialId(String serialId) {
        return (List<PhysicalNodeDO>) getSqlMapClientTemplate().queryForList(
                "PhysicalNode.queryPhysicalNodesBySerialId", serialId);
    }

    public PhysicalNodeDO queryPhysicalNodeById(Integer serialId) {
        return (PhysicalNodeDO) getSqlMapClientTemplate().queryForObject(
                "PhysicalNode.queryPhysicalNodeById", serialId);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updatePhysicalNodeStatus(String id, int status) {
        Map paramMap = new HashMap();
        paramMap.put("physicalId", id);
        paramMap.put("status", status);
        getSqlMapClientTemplate().update("PhysicalNode.updatePhysicalNodeStatus", paramMap);

    }

    public void updatePhysicalNode(PhysicalNodeDO physicalNodeDO) {
        getSqlMapClientTemplate().update("PhysicalNode.updatePhysicalNode", physicalNodeDO);

    }

    public PhysicalNodeDO queryPhysicalNodeByPhysicalId(String physicalId) {
        return (PhysicalNodeDO) getSqlMapClientTemplate().queryForObject(
                "PhysicalNode.queryPhysicalNodeByPhysicalId", physicalId);
    }

    public PhysicalNodeDO queryPhysicalNodesByConditions(PhysicalNodeDO physicalNodeDO) {
        return (PhysicalNodeDO) getSqlMapClientTemplate().queryForObject(
                "PhysicalNode.queryPhysicalNodesByConditions", physicalNodeDO);
    }
    
    
    public PhysicalNodeDO queryDuplicateNodesForEdit(PhysicalNodeDO physicalNodeDO) {
        return (PhysicalNodeDO) getSqlMapClientTemplate().queryForObject(
                "PhysicalNode.queryDuplicateNodesForEdit", physicalNodeDO);

    }
    
    public void updatePhysicalNodeList(List<PhysicalNodeDO> physicalNodeDoList) {
        for (PhysicalNodeDO nodeDo : physicalNodeDoList) {
            updatePhysicalNode(nodeDo);
        }

    }

    public void deletePhysicalNode(String physicalId) {
        getSqlMapClientTemplate().delete("PhysicalNode.deletePhysicalNodeByPhysicalId", physicalId);
    }

    @SuppressWarnings("unchecked")
    public List<PhysicalNodeDO> queryNomalPhysicalNodesByIP(String IP) {
        return (List<PhysicalNodeDO>) getSqlMapClientTemplate().queryForList(
                "PhysicalNode.queryNomalPhysicalNodesByIP", IP);
    }

    public void updatePhysicalNodeByNodeId(PhysicalNodeDO physicalNodeDO) {
        getSqlMapClientTemplate().update("PhysicalNode.updatePhysicalNodeByNodeId", physicalNodeDO);
    }

}

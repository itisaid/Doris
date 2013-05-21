/**
 * Project: doris.config.server-1.0-SNAPSHOT File Created at 2011-4-27 $Id$ Copyright 1999-2100 Alibaba.com Corporation
 * Limited. All rights reserved. This software is the confidential and proprietary information of Alibaba Company.
 * ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.doris.admin.dao;

import java.util.List;

import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;

/**
 * TODO Comment of PhysicalNodeDao
 * 
 * @author mianhe
 */
public interface PhysicalNodeDao {

    /**
     * 新增一个节点Node，但此操作不将其加入到集群中去
     */
    Integer addPhysicalNode(PhysicalNodeDO phsicalNodeDo);

    /**
     * @return 所有序列的所有Node节点，含集群中提供服务的Nodes以及未分配到集群中的Nodes.
     */
    List<PhysicalNodeDO> queryAllPhysicalNodes();

    /**
     * @return 所有在集群中可用的Node节点，主要包括在集群中提供服务的节点Nodes；
     */
    List<PhysicalNodeDO> queryAllUsablePhysicalNodes();

    /**
     * @return 根据IP返回Node对象集合；
     */
    List<PhysicalNodeDO> queryNomalPhysicalNodesByIP(String IP);

    /**
     * @return 所有新增的但还没有加入到集群中的节点Nodes
     */
    List<PhysicalNodeDO> queryUnUsablePhysicalNodes();

    /**
     * 根据NodeId返回Node对象
     * 
     * @param id
     * @return
     */
    public PhysicalNodeDO queryPhysicalNodeById(Integer id);

    /**
     * 根据physicalId返回Node对象
     * 
     * @param physicalId
     * @return
     */
    public PhysicalNodeDO queryPhysicalNodeByPhysicalId(String physicalId);

    /**
     * 检查重复性：利用ip+port或physicalId
     * 
     * @param physicalNodeDO
     * @return
     */
    public PhysicalNodeDO queryPhysicalNodesByConditions(PhysicalNodeDO physicalNodeDO);
    
    /**
     * 检查重复性：利用ip+port或physicalId, 排除当前编辑ID
     * 
     * @param physicalNodeDO
     * @return
     */
    public PhysicalNodeDO queryDuplicateNodesForEdit(PhysicalNodeDO physicalNodeDO);

    /**
     * @param serialId 序列ID
     * @return 包含在某条序列中的Nodes
     */
    List<PhysicalNodeDO> queryPhysicalNodesBySerialId(String serialId);

    /**
     * 更新某一个NodeId对应的状态
     * 
     * @param physicalId
     * @param status
     */
    public void updatePhysicalNodeStatus(String physicalId, int status);

    /**
     * 更新Node信息
     * 
     * @param physicalNodeDO
     */
    public void updatePhysicalNode(PhysicalNodeDO physicalNodeDO);
    
    public void updatePhysicalNodeByNodeId(PhysicalNodeDO physicalNodeDO);

    /**
     * 更新NodeList的状态
     * 
     * @param physicalNodeDoList
     */
    public void updatePhysicalNodeList(List<PhysicalNodeDO> physicalNodeDoList);

    /**
     * 删除Node
     * 
     * @param physicalNodeDO
     */
    public void deletePhysicalNode(String physicalId);

}

package com.alibaba.doris.admin.service;

import java.util.List;

import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.common.StoreNodeSequenceEnum;

/**
 * @project :doris
 * @author : len.liu
 * @datetime : 2011-5-12 下午10:10:20
 * @version :0.1
 * @Modification:
 */
public interface AdminNodeService {

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
     * @return 所有新增的但还没有加入到集群中的节点Nodes
     */
    List<PhysicalNodeDO> queryUnUsablePhysicalNodes();

    /**
     * @param serialId 序列ID
     * @return 包含在某条序列中的Nodes
     */
    List<PhysicalNodeDO> queryPhysicalNodesBySerialId(String serialId);

    /**
     * @param physicalId
     * @return 根据physicalId返回Node对象，PhysicalId在整个集群中是唯一存在的
     */
    PhysicalNodeDO queryPhysicalNodeByPhysicalId(String physicalId);

    /**
     * @param Id
     * @return 根据主键Id返回Node对象
     */
    PhysicalNodeDO queryPhysicalNodeById(Integer id);

    /**
     * 检查重复性：利用ip+port或physicalId
     * 
     * @param physicalNodeDO
     * @return
     */
    PhysicalNodeDO queryPhysicalNodesByConditions(PhysicalNodeDO physicalNodeDO);
  
    /**
     * 检查重复性：利用ip+port或physicalId, 排除当前node
     * 
     * @param physicalNodeDO
     * @return
     */
    PhysicalNodeDO queryDuplicateNodesForEdit(PhysicalNodeDO physicalNodeDO);

    /**
     * 根据physical id 修改一个节点Node的状态
     */
    void updatePhysicalNodeStatus(String physicalId, int status);

    /**
     * 更新Node信息
     * 
     * @param physicalNodeDO
     */
    public void updatePhysicalNode(PhysicalNodeDO physicalNodeDO);

    
    public void updatePhysicalNodeByNodeId(PhysicalNodeDO physicalNodeDO);

    /**
     * 删除Node
     * 
     * @param physicalNodeDO
     */
    public void deletePhysicalNode(String physicalId);

    /**
     * 更新NodeList的状态
     * 
     * @param physicalNodeDoList
     */
    public void updatePhysicalNodeList(List<PhysicalNodeDO> physicalNodeDoList);

    /**
     * @return 根据IP返回Node对象集合；
     */
    List<PhysicalNodeDO> queryNomalPhysicalNodesByIP(String IP);

    /**
     * 检查同一台Machine下的Node不可以分配到不同的sequence里面去，即同一台Machine下的Node必须在相同的sequence里面
     * 
     * @param IP准备迁移的Node的machine IP
     * @param targetSequence 要迁移的目标序列
     * @return
     */
    public boolean checkLegalMigrateByIP(String IP, StoreNodeSequenceEnum targetSequence);

    /**
     * 检查同一台Machine下的Node不可以分配到不同的sequence里面去，即同一台Machine下的Node必须在相同的sequence里面
     * 
     * @param physicalId准备迁移的Node的physicalId
     * @param targetSequence 要迁移的目标序列
     * @return
     */
    public boolean checkLegalMigrateByPhysicalId(String physicalId, StoreNodeSequenceEnum targetSequence);
}

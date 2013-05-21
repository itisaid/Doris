package com.alibaba.doris.admin.service.common.migrate.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.service.common.node.NodesManager;
import com.alibaba.doris.algorithm.mirgate.MigrationPair;
import com.alibaba.doris.algorithm.mirgate.MigrationRouter;
import com.alibaba.doris.algorithm.mirgate.TempFailResolveRouter;
import com.alibaba.doris.common.StoreNodeSequenceEnum;
import com.alibaba.doris.common.route.MigrationRoutePair;

/**
 * 迁移命令解析
 * 
 * @author frank
 */
public class CommandParser {

    private static int virtualNodeNum = AdminServiceLocator.getVirtualNodeService()
                                              .getVirtualNodeNum(); // TODO

    // admin.core
    // give a
    // method

    private CommandParser() {

    }

    /**
     * 解析扩容命令
     * 
     * @param sourcePhysicalId
     * @param physicalIdList
     * @return
     */
    public static List<MigrationRoutePair> parseExpansionCommand(String sourcePhysicalId,
                                                                 List<String> physicalIdList) {
        PhysicalNodeDO node = NodesManager.getInstance().getNode(sourcePhysicalId);
        int oldLength = NodesManager.getInstance().getSequenceLenght(node.getSerialId());
        int logicNo = node.getLogicalId();
        int newLength = oldLength + physicalIdList.size();
        MigrationRouter mr = new MigrationRouter(virtualNodeNum, oldLength, newLength);
        List<MigrationPair> pairList = mr.getMigrationPairOfPNode(logicNo);
        List<MigrationRoutePair> routePairList = new ArrayList<MigrationRoutePair>();
        for (MigrationPair pair : pairList) {
            MigrationRoutePair mrp = new MigrationRoutePair();
            mrp.setTargetPhysicalId(physicalIdList.get(pair.getTarget() - oldLength));
            mrp.setVnode(pair.getVnode());
            routePairList.add(mrp);
        }
        return routePairList;

    }

    /**
     * 解析失效恢复命令
     * 
     * @param sourcePhysicalId
     * @param targetPhysicalId
     * @return
     */
    public static List<MigrationRoutePair> parseFailCommand(String sourcePhysicalId,
                                                            String targetPhysicalId) {

        PhysicalNodeDO sourceNode = NodesManager.getInstance().getNode(sourcePhysicalId);
        int sourceSequenceLength = NodesManager.getInstance().getSequenceLenght(
                sourceNode.getSerialId());
        int sourceLogicNo = sourceNode.getLogicalId();
        TempFailResolveRouter sfrr = new TempFailResolveRouter(virtualNodeNum, sourceSequenceLength);
        List<Integer> sourceVirtualList = sfrr.getTempFailMigerateVirtialNodes(sourceLogicNo);

        PhysicalNodeDO targetNode = NodesManager.getInstance().getNode(targetPhysicalId);
        int targetSequenceLength = NodesManager.getInstance().getSequenceLenght(
                targetNode.getSerialId());
        int targetLogicNo = targetNode.getLogicalId();
        TempFailResolveRouter tfrr = new TempFailResolveRouter(virtualNodeNum, targetSequenceLength);
        List<Integer> targetVirtualList = tfrr.getTempFailMigerateVirtialNodes(targetLogicNo);

        Set<Integer> sourceSet = new HashSet<Integer>(sourceVirtualList);
        List<MigrationRoutePair> routePairList = new ArrayList<MigrationRoutePair>();

        int serialId = sourceNode.getSerialId();

        for (Integer vNo : targetVirtualList) {
            if (serialId == StoreNodeSequenceEnum.TEMP_SEQUENCE.getValue()
                    || sourceSet.contains(vNo)) {//临时失效：目标虚拟节点都发送，永久失效：源节点包含目标虚拟节点即发送。
                MigrationRoutePair mrp = new MigrationRoutePair();
                mrp.setTargetPhysicalId(targetPhysicalId);
                mrp.setVnode(vNo);
                routePairList.add(mrp);
            }
        }
        return routePairList;
    }

    public static void main(String[] args) {
        List<String> nl = new ArrayList<String>();
        nl.add("1");
        System.out.println(parseExpansionCommand("0", nl));
    }
}

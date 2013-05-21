package com.alibaba.doris.admin.service.failover.migrate;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.monitor.MonitorEnum;
import com.alibaba.doris.admin.monitor.SystemLogMonitor;
import com.alibaba.doris.admin.service.common.migrate.command.MigrateCommand;
import com.alibaba.doris.admin.service.common.node.NodesManager;
import com.alibaba.doris.admin.service.failover.node.check.NodeCheckManager;
import com.alibaba.doris.admin.service.failover.node.check.NodeHealth;
import com.alibaba.doris.common.MigrateTypeEnum;
import com.alibaba.doris.common.MonitorWarnConstants;
import com.alibaba.doris.common.NodeRouteStatus;
import com.alibaba.doris.common.StoreNodeSequenceEnum;

/**
 * 永久失效恢复迁移调度线程
 * 
 * @author frank
 */
public class ForeverFailoverMigrateThread extends FailoverMigrateThread {

    private List<PhysicalNodeDO> relativeNodeList;
    // private static final Log log = LogFactory.getLog(ForeverFailoverMigrateThread.class);
    PhysicalNodeDO               standbyNode;

    public ForeverFailoverMigrateThread(String failPhysicalNodeId) {

        // processor保证这里一定能获得standbyNode
        standbyNode = NodesManager.getInstance().getStandbyNodeId(failPhysicalNodeId);
        if (standbyNode == null||NodeCheckManager.getInstance().checkNode(standbyNode.getPhysicalId(), false).equals(NodeHealth.NG)) {
            return;
        }
        PhysicalNodeDO node = NodesManager.getInstance().getNode(failPhysicalNodeId);
        int failNodeSequence = node.getSerialId();
        int failNodeLogicId = node.getLogicalId();
        standbyNode.setLogicalId(failNodeLogicId);
        standbyNode.setSerialId(failNodeSequence);
        standbyNode.setStatus(NodeRouteStatus.TEMP_FAILED.getValue());
        node.setSerialId(StoreNodeSequenceEnum.UNUSE_SEQUENCE.getValue());
        List<PhysicalNodeDO> nodes = new ArrayList<PhysicalNodeDO>();

        nodes.add(standbyNode);
        nodes.add(node);
        nodeService.updatePhysicalNodeList(nodes);
        NodesManager.getInstance().reLoadNodes();
        
        migrateKey = standbyNode.getPhysicalId();
        this.failPhysicalNodeIdList.add(migrateKey);

        // 获得永久失效节点的对等节点，只考虑两个序列,考虑到序列长度不一样的情况，对等序列的所有节点都可能包含失效节点的数据
        if (StoreNodeSequenceEnum.NORMAL_SEQUENCE_1.equals(StoreNodeSequenceEnum.getTypeByValue(failNodeSequence))) {
            relativeNodeList = NodesManager.getInstance().getNodeListBySequence(StoreNodeSequenceEnum.NORMAL_SEQUENCE_2);
        } else {
            relativeNodeList = NodesManager.getInstance().getNodeListBySequence(StoreNodeSequenceEnum.NORMAL_SEQUENCE_1);
        }

        this.start();
    }

    @Override
    protected boolean sendMigerateCommand() {
        return processSendCommand(relativeNodeList, MigrateTypeEnum.FOREVER_FAILOVER);
    }

    @Override
    protected void sendMigerateFinishCommand() {
        for (int i = 0; i < relativeNodeList.size(); i++) {
            PhysicalNodeDO node = relativeNodeList.get(i);
            SystemLogMonitor.info(MonitorEnum.MIGRATION, MonitorWarnConstants.NODE_FOREVER_FAILURE_RESOLVED
                                                         + node.getPhysicalId());
            MigrateCommand.finishMigerate(node.getPhysicalId(), failPhysicalNodeIdList, commandParamList.get(i),
                                          MigrateTypeEnum.FOREVER_FAILOVER);
        }

    }

    public void notifyError() {
        // TODO Auto-generated method stub

    }

    protected void updateStoreNode() {
        standbyNode.setStatus(NodeRouteStatus.OK.getValue());
        nodeService.updatePhysicalNode(standbyNode);
    }
}

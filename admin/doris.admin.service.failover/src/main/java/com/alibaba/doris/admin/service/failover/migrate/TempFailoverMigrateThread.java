package com.alibaba.doris.admin.service.failover.migrate;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.monitor.MonitorEnum;
import com.alibaba.doris.admin.monitor.SystemLogMonitor;
import com.alibaba.doris.admin.service.common.migrate.command.MigrateCommand;
import com.alibaba.doris.admin.service.common.node.NodesManager;
import com.alibaba.doris.common.MigrateTypeEnum;
import com.alibaba.doris.common.MonitorWarnConstants;
import com.alibaba.doris.common.NodeRouteStatus;

/**
 * 临时失败恢复迁移调度线程
 * 
 * @author frank
 */
public class TempFailoverMigrateThread extends FailoverMigrateThread {

    // private static final Log log = LogFactory
    // .getLog(TempFailoverMigrateThread.class);
    private List<PhysicalNodeDO> tempPhysicalNodeIdList = new ArrayList<PhysicalNodeDO>();

    public TempFailoverMigrateThread(String failPhysicalNodeId) {
        super(failPhysicalNodeId);
        tempPhysicalNodeIdList = NodesManager.getInstance().getAllTempNodeList();
        this.migrateKey = failPhysicalNodeIdList.get(0);
        this.start();
    }

    protected boolean sendMigerateCommand() {
        return processSendCommand(tempPhysicalNodeIdList, MigrateTypeEnum.TEMP_FAILOVER);
    }

    protected void sendMigerateFinishCommand() {

        for (int i = 0; i < tempPhysicalNodeIdList.size(); i++) {
            PhysicalNodeDO tempNode = tempPhysicalNodeIdList.get(i);
            SystemLogMonitor.info(MonitorEnum.MIGRATION, MonitorWarnConstants.NODE_TEMP_FAILURE_RESOLVED
                                                         + tempNode.getPhysicalId());
            MigrateCommand.finishMigerate(tempNode.getPhysicalId(), failPhysicalNodeIdList, commandParamList.get(i),
                                          MigrateTypeEnum.TEMP_FAILOVER);
        }
    }

    public void notifyError() {
        // TODO Auto-generated method stub

    }

    protected void updateStoreNode() {
        PhysicalNodeDO node = NodesManager.getInstance().getNode(failPhysicalNodeIdList.get(0));
        node.setStatus(NodeRouteStatus.OK.getValue());
        nodeService.updatePhysicalNode(node);
    }
}

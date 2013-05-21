package com.alibaba.doris.admin.service.expansion.migrate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.monitor.MonitorEnum;
import com.alibaba.doris.admin.monitor.SystemLogMonitor;
import com.alibaba.doris.admin.service.common.migrate.MigrateThread;
import com.alibaba.doris.admin.service.common.migrate.command.MigrateCommand;
import com.alibaba.doris.admin.service.common.migrate.command.MigrateCommandResult;
import com.alibaba.doris.admin.service.common.migrate.manager.MigrateManager;
import com.alibaba.doris.admin.service.common.migrate.status.MigrateStatus;
import com.alibaba.doris.admin.service.common.node.NodesManager;
import com.alibaba.doris.common.MigrateStatusEnum;
import com.alibaba.doris.common.MigrateTypeEnum;
import com.alibaba.doris.common.MonitorWarnConstants;
import com.alibaba.doris.common.NodeRouteStatus;
import com.alibaba.doris.common.StoreNodeSequenceEnum;

/**
 * 扩容迁移的执行线程
 * 
 * @author frank
 */
public class ExpansionMigrateThread extends MigrateThread {

    private static final Log log             = LogFactory.getLog(ExpansionMigrateThread.class);
    private List<String>     newPhysicalIdList;                                                // 序列新增节点，也是迁移目标节点

    private List<String>     sourceNodeList;                                                   // 迁移前的序列节点，也是迁移源节点,这里要考虑null的情况
    private List<String>     sourceParamList = new ArrayList<String>();

    public ExpansionMigrateThread(List<String> newPhysicalIdList, StoreNodeSequenceEnum sequence) {
        this.newPhysicalIdList = newPhysicalIdList;
        this.sourceNodeList = NodesManager.getInstance().getNodePhysicalIdListBySequence(sequence);
        if (sourceNodeList == null || sourceNodeList.isEmpty() || sequence.equals(StoreNodeSequenceEnum.TEMP_SEQUENCE)) {// 源节点空或临时序列不需要迁移
            this.needMigrate = false;
        }
        this.migrateKey = String.valueOf(sequence.getValue());
        this.start();
    }

    protected boolean sendMigerateCommand() {
        boolean ok = false;
        List<MigrateStatus> tempList = new ArrayList<MigrateStatus>();
        sourceParamList.clear();
        for (String sourcePhysicalId : sourceNodeList) {
            MigrateCommandResult mcr = MigrateCommand.executeMigerate(sourcePhysicalId, newPhysicalIdList,
                                                                      MigrateTypeEnum.EXPANSION);
            Boolean sentResult = mcr.getResult();
            sourceParamList.add(mcr.getCommandParam());
            if (sentResult == null) {
                if (log.isInfoEnabled()) {
                    log.info("No command need send for expansion:" + sourcePhysicalId + "-->" + newPhysicalIdList);
                }
                continue;
            }
            if (sentResult) {
                tempList.add(MigrateManager.getInstance().addMigerateNode(sourcePhysicalId, MigrateTypeEnum.EXPANSION,
                                                                          MigrateStatusEnum.PREPARE));
                ok = true;
            } else {// 命令发送失败，依然放入监控，运维人员可通过telnet给失败node单独发送迁移命令使扩容迁移继续，也可以在console点击reMigrate重启扩容
                tempList.add(MigrateManager.getInstance().addMigerateNode(sourcePhysicalId, MigrateTypeEnum.EXPANSION,
                                                                          MigrateStatusEnum.COMMAND_FAIL));
            }
        }
        statusList = tempList;
        if (monitorThread != null) {
            monitorThread.setStatusList(tempList);
        }
        return ok;
    }

    @Override
    public List<String> getMigeratingNodePhysicalIdList() {
        return newPhysicalIdList;
    }

    public void notifyError() {
        // 要不要停止迁移？

    }

    @Override
    protected void sendMigerateFinishCommand() {
        for (int i = 0; i < sourceNodeList.size(); i++) {
            String sourcePhysicalId = sourceNodeList.get(i);
            SystemLogMonitor.info(MonitorEnum.MIGRATION, MonitorWarnConstants.NODE_EXPANSION_MIGRATION_FINISHED
                                                         + sourcePhysicalId);
            String param = sourceParamList.get(i);
            MigrateCommand.finishMigerate(sourcePhysicalId, newPhysicalIdList,param, MigrateTypeEnum.EXPANSION);
        }

    }

    /**
     * 更新新节点状态、逻辑ID、序列号，将新节点保存数据库
     */
    protected void updateStoreNode() {
        if (newPhysicalIdList == null || newPhysicalIdList.isEmpty()) {
            return;
        }
        List<PhysicalNodeDO> newNodes = new ArrayList<PhysicalNodeDO>();
        int currentSequence = Integer.valueOf(migrateKey);
        int currentLogicId = NodesManager.getInstance().getLargestLogicId(
                                                                          StoreNodeSequenceEnum.getTypeByValue(currentSequence)) + 1;
        for (String physicalId : newPhysicalIdList) {
            PhysicalNodeDO sn = NodesManager.getInstance().getNode(physicalId);
            sn.setLogicalId(currentLogicId++);
            sn.setSerialId(currentSequence);
            sn.setStatus(NodeRouteStatus.OK.getValue());
            newNodes.add(sn);

        }
        nodeService.updatePhysicalNodeList(newNodes);
        //NodesManager.getInstance().reLoadNodes();
    }

}

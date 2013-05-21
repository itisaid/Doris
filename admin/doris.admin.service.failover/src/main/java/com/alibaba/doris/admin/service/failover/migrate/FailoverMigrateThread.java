package com.alibaba.doris.admin.service.failover.migrate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.service.common.migrate.MigrateThread;
import com.alibaba.doris.admin.service.common.migrate.command.MigrateCommand;
import com.alibaba.doris.admin.service.common.migrate.command.MigrateCommandResult;
import com.alibaba.doris.admin.service.common.migrate.manager.MigrateManager;
import com.alibaba.doris.admin.service.common.migrate.status.MigrateStatus;
import com.alibaba.doris.common.MigrateStatusEnum;
import com.alibaba.doris.common.MigrateTypeEnum;

/**
 * 失效迁移线程基类
 * 
 * @author frank
 */
public abstract class FailoverMigrateThread extends MigrateThread {
    private static final Log log                    = LogFactory
                                                            .getLog(FailoverMigrateThread.class);
    protected List<String>   failPhysicalNodeIdList = new ArrayList<String>();
    protected List<String> commandParamList = new ArrayList<String>();

    public FailoverMigrateThread() {
    }

    public FailoverMigrateThread(String failPhysicalNodeId) {
        this.failPhysicalNodeIdList.add(failPhysicalNodeId);
    }

    @Override
    public List<String> getMigeratingNodePhysicalIdList() {
        return failPhysicalNodeIdList;
    }

    protected boolean processSendCommand(List<PhysicalNodeDO> physicalNodeIdList,
                                         MigrateTypeEnum type) {
        List<MigrateStatus> tempList = new ArrayList<MigrateStatus>();
        tempList.add(MigrateManager.getInstance().addMigerateNode(failPhysicalNodeIdList.get(0),
                                                                  type, MigrateStatusEnum.PREPARE));
        boolean ok = true;
        commandParamList.clear();
        for (PhysicalNodeDO physicalNode : physicalNodeIdList) {

            // 如果给前出节点发迁移命令，命令发送失败，则不启动迁移监控线程，使迁移无法完成。
            MigrateCommandResult mcr= MigrateCommand.executeMigerate(physicalNode.getPhysicalId(),
                    failPhysicalNodeIdList, type);
             Boolean sentResult = mcr.getResult();
             commandParamList.add(mcr.getCommandParam());
            if (sentResult == null) {
                if (log.isInfoEnabled()) {
                    log.info("No command need send for temp fail over:"
                            + physicalNode.getPhysicalId() + "-->" + failPhysicalNodeIdList);
                }
                continue;
            }
            if (sentResult) {
                // 初始化迁移状态,必需的，避免无数据迁移的临时节点立即返回完成导致迁移结束
                MigrateManager.getInstance().updateMigerateStatus(physicalNode.getPhysicalId(),
                        failPhysicalNodeIdList.get(0), 0, MigrateStatusEnum.PREPARE,
                        "sent command.");
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("sent temp fail migrate command failed:"
                            + physicalNode.getPhysicalId() + "-->" + failPhysicalNodeIdList);
                }
                
                ok = false;
                break;
            }
        }
        statusList = tempList;
        if (monitorThread != null) {
            monitorThread.setStatusList(tempList);
        }
        return ok;
    }
}

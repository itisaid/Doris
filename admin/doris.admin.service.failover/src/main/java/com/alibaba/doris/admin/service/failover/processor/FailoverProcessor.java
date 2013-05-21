package com.alibaba.doris.admin.service.failover.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.service.common.AdminServiceException;
import com.alibaba.doris.admin.service.common.migrate.MigrateThread;
import com.alibaba.doris.admin.service.common.migrate.manager.MigrateManager;
import com.alibaba.doris.admin.service.common.node.NodesManager;
import com.alibaba.doris.common.StoreNodeSequenceEnum;

/**
 * 失效恢复processor基类
 * 
 * @author frank
 */
public abstract class FailoverProcessor {

    private static final Log log         = LogFactory.getLog(FailoverProcessor.class);
    protected boolean        isMainAdmin = AdminServiceLocator.getAdminService().isMasterAdmin();

    /**
     * 失效恢复处理，processor的主方法
     * 
     * @param failPhysicalNodeId 失效恢复节点
     * @return 是否启动失效恢复迁移调度
     */
    public synchronized void failResolve(String failPhysicalNodeId) throws AdminServiceException {
        if (!isMainAdmin) {
            if (log.isErrorEnabled()) {
                log.error("This is backup admin server, doesn't support expansion migrating task.");
            }
            throw new AdminServiceException("This is backup admin server, doesn't support expansion migrating task.");
        }

        PhysicalNodeDO node = NodesManager.getInstance().getNode(failPhysicalNodeId);
        if (failPhysicalNodeId == null || node == null
            || node.getSerialId() < StoreNodeSequenceEnum.NORMAL_SEQUENCE_1.getValue()
            || node.getSerialId() > StoreNodeSequenceEnum.NORMAL_SEQUENCE_4.getValue()) {
            if (log.isWarnEnabled()) {
                log.warn("illegal input parameters:" + failPhysicalNodeId);
            }
            throw new AdminServiceException("illegal input parameters:" + failPhysicalNodeId + ", it's sequence:"
                                            + node.getSerialId());
        }
        NodesManager.getInstance().reLoadNodes();
        
        canFailoverMigerate(failPhysicalNodeId);

        if (MigrateManager.getInstance().isMigrating(failPhysicalNodeId)) {// 节点正在失效迁移中，kill
            MigrateThread migThread = MigrateManager.getInstance().getMigerateThread(failPhysicalNodeId);
            if (migThread != null) {
                migThread.over();
            }

            if (log.isWarnEnabled()) {
                log.warn(NodesManager.getInstance().getLogFormatNodeId(failPhysicalNodeId)
                         + " is migerating,kill and restart.");
            }
        }
        // 启动迁移线程
        startMigerateThread(failPhysicalNodeId);

        if (log.isInfoEnabled()) {
            log.info("execute failover migerate in " + failPhysicalNodeId);
        }

    }

    /**
     * 是否允许失效恢复迁移
     * 
     * @param failPhysicalNodeId
     * @return
     */
    protected abstract boolean canFailoverMigerate(String failPhysicalNodeId) throws AdminServiceException;

    /**
     * 启动迁移调度线程
     * 
     * @param failPhysicalNodeId
     */
    protected abstract void startMigerateThread(String failPhysicalNodeId);
}

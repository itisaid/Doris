package com.alibaba.doris.admin.service.failover.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.service.common.AdminServiceException;
import com.alibaba.doris.admin.service.common.node.NodesManager;
import com.alibaba.doris.admin.service.failover.migrate.ForeverFailoverMigrateThread;
import com.alibaba.doris.admin.service.failover.node.check.NodeCheckManager;
import com.alibaba.doris.admin.service.failover.node.check.NodeHealth;

/**
 * 永久失效恢复processor
 * 
 * @author frank
 */
public class ForeverFailoverProcessor extends FailoverProcessor {

    private static final Log         log      = LogFactory.getLog(ForeverFailoverProcessor.class);
    private static FailoverProcessor instance = new ForeverFailoverProcessor();

    private ForeverFailoverProcessor() {
    }

    public static FailoverProcessor getInstance() {
        return instance;
    }

    protected void startMigerateThread(String failPhysicalNodeId) {

        ForeverFailoverMigrateThread migThread = new ForeverFailoverMigrateThread(failPhysicalNodeId);

    }

    /**
     * 判断是否存在可替换的备用节点
     * 应该维护备用节点的健康，如果备用节点NG，异常报警而不是继续寻找
     * 
     */
    @Override
    protected boolean canFailoverMigerate(String failPhysicalNodeId) throws AdminServiceException{
        
        PhysicalNodeDO standbyNode = NodesManager.getInstance().getStandbyNodeId(failPhysicalNodeId);
        if (standbyNode == null||NodeCheckManager.getInstance().checkNode(standbyNode.getPhysicalId(), false).equals(NodeHealth.NG)) {
            if (log.isWarnEnabled()) {
                log.warn("There is no standby node for forever fail migrate.");
            }
            throw new AdminServiceException("There is no standby node for forever fail migrate.");
        }
        return true;
    }

}

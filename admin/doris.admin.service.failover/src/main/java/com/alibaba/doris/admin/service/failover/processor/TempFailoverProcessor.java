package com.alibaba.doris.admin.service.failover.processor;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.service.common.AdminServiceException;
import com.alibaba.doris.admin.service.common.node.NodesManager;
import com.alibaba.doris.admin.service.failover.migrate.TempFailoverMigrateThread;
import com.alibaba.doris.common.NodeRouteStatus;

/**
 * 临时失效恢复processor
 * 
 * @author frank
 */
public class TempFailoverProcessor extends FailoverProcessor {

    private static final Log         log      = LogFactory.getLog(TempFailoverProcessor.class);

    private static FailoverProcessor instance = new TempFailoverProcessor();

    private TempFailoverProcessor() {
    }

    public static FailoverProcessor getInstance() {
        return instance;
    }

    protected void startMigerateThread(String failPhysicalNodeId) {

        TempFailoverMigrateThread migThread = new TempFailoverMigrateThread(failPhysicalNodeId);

    }

    @Override
    protected boolean canFailoverMigerate(String failPhysicalNodeId) throws AdminServiceException {

        List<PhysicalNodeDO> tempNodeIdList = NodesManager.getInstance().getAllTempNodeList();
        if (tempNodeIdList == null || tempNodeIdList.isEmpty()) {// 没有临时节点，不能恢复
            if (log.isWarnEnabled()) {
                log.warn("there is no temp node for temp fail migrate.");
            }
            throw new AdminServiceException("there is no temp node for temp fail migrate.");
        }

        // 临时节点有失效，不能恢复
        for (PhysicalNodeDO node : tempNodeIdList) {
            if (node.getStatus() != NodeRouteStatus.OK.getValue()) {
                if (log.isWarnEnabled()) {
                    log.warn("there are some temp nodes which is temp failed. Could NOT execute temp fail migration.");
                }
                throw new AdminServiceException(
                                                "there are some temp nodes which is temp failed. Could NOT execute temp fail migration.");
            }
        }
        return true;
    }

}

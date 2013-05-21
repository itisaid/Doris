package com.alibaba.doris.admin.service.expansion.processor;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.service.common.AdminServiceException;
import com.alibaba.doris.admin.service.common.migrate.MigrateThread;
import com.alibaba.doris.admin.service.common.migrate.manager.MigrateManager;
import com.alibaba.doris.admin.service.common.migrate.status.MigrateStatus;
import com.alibaba.doris.admin.service.common.node.NodesManager;
import com.alibaba.doris.admin.service.expansion.migrate.ExpansionMigrateThread;
import com.alibaba.doris.common.NodeRouteStatus;
import com.alibaba.doris.common.StoreNodeSequenceEnum;

/**
 * 扩容迁移Processor
 * 
 * @author frank
 */
public class ExpansionMigrateProcessor {

    private static final Log                 log         = LogFactory
                                                                 .getLog(ExpansionMigrateProcessor.class);

    private static ExpansionMigrateProcessor instance    = new ExpansionMigrateProcessor();

    private boolean                          isMainAdmin = AdminServiceLocator.getAdminService()
                                                                 .isMasterAdmin();

    private ExpansionMigrateProcessor() {
    }

    public static ExpansionMigrateProcessor getInstance() {
        return instance;
    }

    /**
     * 启动扩容迁移任务，任务启动立即返回，该方法可重复调用，每次调用都会重发迁移命令，reset迁移状态<br>
     * 如果已经在扩容迁移中，输入的新增节点被忽略
     * 
     * @param newNodes 序列中新增的节点
     * @param sequence 要扩容的序列,仅正常序列和临时序列
     * @throws AdminServiceException 扩容任务不能启动
     */
    public synchronized void migerate(List<String> newNodePhysicalIdList, StoreNodeSequenceEnum sequence)
                                                                                                         throws AdminServiceException {
        if (!isMainAdmin) {
            if (log.isErrorEnabled()) {
                log.error("This is backup admin server, doesn't support expansion migrating task.");
            }
            throw new AdminServiceException("This is backup admin server, doesn't support expansion migrating task.");
        }
        
        if (newNodePhysicalIdList == null || newNodePhysicalIdList.isEmpty() || sequence == null) {
            if(log.isErrorEnabled()){
                log.error("illegal input param! newNodePhysicalIdList:"+newNodePhysicalIdList+"sequence:"+sequence);
            }
            throw new AdminServiceException("illegal input param! newNodePhysicalIdList:"+newNodePhysicalIdList+"sequence:"+sequence);
        }
        if (log.isInfoEnabled()) {
            log.info("execute migerate in " + sequence + " for " + newNodePhysicalIdList);
        }
        NodesManager.getInstance().reLoadNodes();
        List<PhysicalNodeDO> nodeList = NodesManager.getInstance().getNodeListBySequence(sequence);
        // 检查序列中现有节点，是否可进行扩容迁移
        for (PhysicalNodeDO node : nodeList) {
            // 如果节点路由状态不OK，不得扩容
            if (node.getStatus() != NodeRouteStatus.OK.getValue()) {
                throw new AdminServiceException("Node("+node.getPhysicalId() + ") is NG, couldn't expansion.");
            }

            // 如果有正在迁移的Node，不得扩容
            MigrateStatus ms = MigrateManager.getInstance().getMigerateStatus(node.getPhysicalId());
            if (ms != null) {
                throw new AdminServiceException(node.getPhysicalId()
                                                + "  node is resolving failover or expanding,forbidden to expansion");
            }

        }
        // 当前序列已在迁移中，不可迁移
        if (MigrateManager.getInstance().getMigerateThread(String.valueOf(sequence.getValue())) != null) {
            throw new AdminServiceException(sequence + " is expanding!");
        }
        
        //这个逻辑不会被执行，先保留
        if (isMigrating(sequence)) {// 序列正在迁移中，kill & restart，
            MigrateThread migThread = getMigThread(sequence);
            migThread.over();

            if (log.isWarnEnabled()) {
                log.warn(sequence + " is migerating,kill and restart.");
            }

        }
        // 启动迁移线程
        ExpansionMigrateThread migThread = new ExpansionMigrateThread(newNodePhysicalIdList, sequence);
//        migThread.start();
        if (log.isInfoEnabled()) {
            log.info("start migerate thread " + migThread);
        }

    }

    /**
     * 启动扩容迁移（重新启动未完成迁移）
     * 
     * @param sequence
     * @throws AdminServiceException
     */
    public synchronized void migerate(StoreNodeSequenceEnum sequence) throws AdminServiceException {
        if (!isMainAdmin) {
            if (log.isErrorEnabled()) {
                log.error("This is backup admin server, doesn't support expansion migrating task.");
            }
            throw new AdminServiceException("This is backup admin server, doesn't support expansion migrating task.");
        }
        
        MigrateThread mt = MigrateManager.getInstance().getMigerateThread(String.valueOf(sequence.getValue()));
        if (mt == null) {
            throw new AdminServiceException(sequence + " is NOT expanding!");
        }

        if (!mt.redo()) {
            throw new AdminServiceException(sequence
                                            + " expanding migrate command maybe could execute in some store node");
        }
    }

    /**
     * 检查序列是否正在扩容迁移，执行迁移前最好调用该方法
     * 
     * @param sequence
     * @return
     */
    public boolean isMigrating(StoreNodeSequenceEnum sequence) {
        return MigrateManager.getInstance().isMigrating(String.valueOf(sequence.getValue()));
    }

    /**
     * 检查节点是否在扩容迁移中，包括源节点和目标节点
     * 
     * @param physicalId
     * @return
     */
    public boolean isMigrating(String physicalId) {
        return MigrateManager.getInstance().isMigrating(physicalId);
    }

    /**
     * 获得正在迁移中的序列的新节点
     * 
     * @param sequence
     * @return
     */
    public List<String> getMigeratingNewNode(StoreNodeSequenceEnum sequence) {
        if (!isMigrating(sequence)) {
            return null;
        }
        return getMigThread(sequence).getMigeratingNodePhysicalIdList();
    }

    private MigrateThread getMigThread(StoreNodeSequenceEnum sequence) {
        return MigrateManager.getInstance().getMigerateThread(String.valueOf(sequence.getValue()));
    }

}

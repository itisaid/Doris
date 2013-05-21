package com.alibaba.doris.admin.service.common.migrate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.monitor.MonitorEnum;
import com.alibaba.doris.admin.monitor.SystemLogMonitor;
import com.alibaba.doris.admin.service.AdminNodeService;
import com.alibaba.doris.admin.service.common.migrate.manager.MigrateManager;
import com.alibaba.doris.admin.service.common.migrate.status.MigrateStatus;
import com.alibaba.doris.admin.service.common.migrate.status.MigrateStatusCallback;
import com.alibaba.doris.admin.service.common.migrate.status.MigrateStatusMonitorThread;
import com.alibaba.doris.admin.service.common.node.NodesManager;
import com.alibaba.doris.admin.service.common.route.DorisConfigServiceException;
import com.alibaba.doris.admin.service.common.route.RouteConfigProcessor;
import com.alibaba.doris.common.MonitorWarnConstants;

/**
 * 迁移线程基类
 * 
 * @author frank
 */
public abstract class MigrateThread extends Thread implements MigrateStatusCallback {

    private static final Log             log         = LogFactory.getLog(MigrateThread.class);
    protected boolean                    isGoOn      = true;
    protected static final int           sleepTime   = 1000;
    protected MigrateStatusMonitorThread monitorThread;
    protected List<MigrateStatus>        statusList  = new ArrayList<MigrateStatus>();
    protected String                     migrateKey;
    protected boolean                    needMigrate = true;                                     // 某些情况（扩容迁移源为null）不需要迁移数据，直接更新节点状态
    protected AdminNodeService           nodeService = AdminServiceLocator.getAdminNodeService();

    public abstract List<String> getMigeratingNodePhysicalIdList();

    public boolean redo() {
        return sendMigerateCommand();
    }

    public void run() {
        if (needMigrate) {
            if (!basicCommand()) {
                this.over();
                return;
            }
            monitorThread = new MigrateStatusMonitorThread(statusList, this);
            monitorThread.start();
            while (isGoOn) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            }
        } else {
            updateStoreNode();
            
            NodesManager.getInstance().reLoadNodes();
            
            if (log.isDebugEnabled()) {
                log.debug("Don't need migrate, but need refresh route. ");
            }
            
            try {
                RouteConfigProcessor.getInstance().refresh();//不是必须的
            } catch (DorisConfigServiceException e) {
                SystemLogMonitor.error(MonitorEnum.ROUTER,
                        MonitorWarnConstants.RE_GEN_ROUTE_FAILED, e);
                log.error("ERROR IN REFRESH CONFIG TABLE.");
            }
        }
    }

    public synchronized void over() {
        if (monitorThread != null) {
            monitorThread.over();// 不是多余
        }
        // 从迁移管理器移除迁移线程
        MigrateManager.getInstance().removeMigerateThread(migrateKey);
        isGoOn = false;
    }

    /**
     * 发送迁移命令，并更新NodeManager<br>
     * 
     * @return 命令是否成功发送：扩容的时候，只要有一个成功就算成功，恢复的时候，全部成功才算成功
     */
    protected boolean basicCommand() {
        boolean result = sendMigerateCommand();
        MigrateManager.getInstance().addMigerateThread(migrateKey, this);
        return result;
    }

    protected abstract boolean sendMigerateCommand();

    public void finishAll() {

        // 关闭监控线程,尽早关闭，否则可能会再callback finishAll
        monitorThread.over();

        if (log.isInfoEnabled()) {
            log.info("migerate in " + migrateKey + " finished.");
        }

        updateStoreNode();
       
        NodesManager.getInstance().reLoadNodes();
        
        // 刷新配置实例 
        try {
            RouteConfigProcessor.getInstance().refresh();
        } catch (DorisConfigServiceException e) {
            log.error("ERROR IN REFRESH CONFIG TABLE. NOT FINIS MIGRATE.");
            SystemLogMonitor.error(MonitorEnum.ROUTER, MonitorWarnConstants.RE_GEN_ROUTE_FAILED, e);
        }

        sendMigerateFinishCommand();
        
        // 自我终结
        this.over();
    }

    /**
     * 更新失效节点状态
     */
    protected abstract void updateStoreNode();

    /**
     * 通知迁移节点迁移结束
     */
    protected abstract void sendMigerateFinishCommand();

}

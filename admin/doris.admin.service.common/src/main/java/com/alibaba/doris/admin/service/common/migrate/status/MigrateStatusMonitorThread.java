package com.alibaba.doris.admin.service.common.migrate.status;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.monitor.MonitorEnum;
import com.alibaba.doris.admin.monitor.SystemLogMonitor;
import com.alibaba.doris.admin.service.common.migrate.manager.MigrateManager;
import com.alibaba.doris.common.MigrateStatusEnum;
import com.alibaba.doris.common.MonitorWarnConstants;

/**
 * 迁移状态监控线程，如果发生特定事件，callback迁移执行线程<br>
 * 该线程的终止被执行线程控制
 * 
 * @author frank
 */
public class MigrateStatusMonitorThread extends Thread {

    private static final Log      log       = LogFactory.getLog(MigrateStatusMonitorThread.class);
    private MigrateStatusCallback callback;
    private List<MigrateStatus>   statusList;
    private boolean               isGoOn    = true;
    private static int            sleepTime = 1000;                                               // 监控时间间隔
    private long                  taskId;                                                         // 监控线程任务ID，用时间戳生成,由于迁移任务进行同步控制并耗时较长，该ID不会重复。

    public MigrateStatusMonitorThread(List<MigrateStatus> statusList, MigrateStatusCallback callback) {
        this.callback = callback;
        this.statusList = statusList;
        this.taskId = System.currentTimeMillis();
    }

    public void run() {

        if (log.isInfoEnabled()) {
            log.info(taskId + "Migerate monitor thread start:" + statusList);
        }
        while (isGoOn) {
            boolean allFinish = true;
            boolean error = false;
            for (MigrateStatus status : statusList) {
                status.resetReportTime();
                if (log.isDebugEnabled()) {
                    log.debug(taskId + " " + status.toString());
                }
                if (!status.getMigerateStatus().equals(MigrateStatusEnum.FINISH)) {// 至少有一个迁移没有完成
                    allFinish = false;
                }
                if (status.getMigerateStatus().equals(MigrateStatusEnum.MIGERATE_ERROR)) {// 至少有一个迁移报告错误
                    error = true;
                    allFinish = false;
                }
                if (status.isTimeout()) {
                    // message 'Migrate Report Timeout:' is used as dragoon warning rule, should be consistent.
                    SystemLogMonitor.error(MonitorEnum.MIGRATION, MonitorWarnConstants.MIGRATE_REPORT_TIMEOUT + ":"
                            + status.getPhysicalId() + ",Mig-Type:" + status.getMigerateType());
                }
            }
            if (allFinish) {
                callback.finishAll();
            }
            if (error) {
                callback.notifyError();
            }
            try {
                sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void over() {
        clear();
        this.isGoOn = false;
    }

    /**
     * 清理现场
     */
    private void clear() {
        // 移除迁移监控
        for (MigrateStatus status : statusList) {
            MigrateManager.getInstance().removeMigerateNode(status.getPhysicalId());
        }
    }

    public List<MigrateStatus> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<MigrateStatus> statusList) {
        this.statusList = statusList;
    }

}

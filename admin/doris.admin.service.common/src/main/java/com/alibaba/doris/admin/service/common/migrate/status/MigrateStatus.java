package com.alibaba.doris.admin.service.common.migrate.status;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.service.common.node.NodesManager;
import com.alibaba.doris.common.MigrateStatusEnum;
import com.alibaba.doris.common.MigrateTypeEnum;

/**
 * 记录迁移中节点的迁移状态，toString()可获得状态详细信息
 * 
 * @author frank
 */
public class MigrateStatus {

    private static final Log           log              = LogFactory.getLog(MigrateStatus.class);
    private String                     physicalId;
    private Map<String, MigrateStatus> statusItemMap;                                            // 扩容迁移：null，失效恢复迁移：<迁出node，迁出状态>
    private int                        schedule         = 0;                                     // 迁移进度
    private MigrateStatusEnum          migerateStatus   = MigrateStatusEnum.PREPARE;             // 迁移状态
    private MigrateTypeEnum            migerateType;                                             // 迁移类型
    private long                       waitingReportTime;                                        // 距上次报告状态经过的时间，单位ms
    private long                       reportTimeStamp;                                          // 上次报告状态系统时间,单位ms
    private String                     message;
    private static final String        SPLIT            = " ";
    private boolean                    hadReportTimeout = false;                                 // 已经发送超时报警，报警只发一次。
    private static final long          reportTimeout    = 240000;                                // 报告超时时间，4分钟。

    public MigrateStatus(String physicalId, MigrateTypeEnum migerateType, MigrateStatusEnum status) {
        this.physicalId = physicalId;
        this.statusItemMap = new HashMap<String, MigrateStatus>();
        this.migerateType = migerateType;
        this.migerateStatus = status;
        this.reportTimeStamp = System.currentTimeMillis();// 用当前系统时间初始化
    }

    public Map<String, MigrateStatus> getStatusItemMap() {
        return statusItemMap;
    }

    public void setStatusItemMap(Map<String, MigrateStatus> statusItemMap) {
        this.statusItemMap = statusItemMap;
    }

    public int getSchedule() {
        return schedule;
    }

    /**
     * 设置进度
     * 
     * @param sourcePhysicalId
     * @param schedule
     * @param statusEnum TODO
     * @param message TODO
     */
    public synchronized void setSchedule(String sourcePhysicalId, int schedule, MigrateStatusEnum statusEnum,
                                         String message) {

        if (migerateType.equals(MigrateTypeEnum.EXPANSION)) {// expansion
            buildMyself(this, schedule, statusEnum, message);
        } else {// fail over
            if (statusItemMap == null) {
                statusItemMap = new ConcurrentHashMap<String, MigrateStatus>();
            }
            MigrateStatus ms = statusItemMap.get(sourcePhysicalId);
            if (ms == null) {
                ms = new MigrateStatus(sourcePhysicalId, migerateType, MigrateStatusEnum.MIGERATING);
                statusItemMap.put(sourcePhysicalId, ms);
            }
            buildMyself(ms, schedule, statusEnum, message);

            int weight = statusItemMap.size();
            int scheduleByWeight = 0;
            boolean allItemFinish = true;
            for (Entry<String, MigrateStatus> entry : statusItemMap.entrySet()) {
                int itemSchedule = entry.getValue().getSchedule();
                if (itemSchedule != 100) {
                    allItemFinish = false;
                }
                scheduleByWeight += itemSchedule / weight;
            }
            if (allItemFinish) {
                scheduleByWeight = 100;
            }

            buildMyself(this, scheduleByWeight, statusEnum.equals(MigrateStatusEnum.FINISH)?MigrateStatusEnum.MIGERATING:statusEnum, message);

        }
    }

    /**
     * 根据报告的进度，重构状态
     * 
     * @param ms
     * @param schedule
     */
    private void buildMyself(MigrateStatus ms, int schedule, MigrateStatusEnum statusEnum, String message) {
        if (log.isDebugEnabled()) {
            log.debug("build migrate status:" + ms.getPhysicalId() + " schedule:" + schedule);
        }
        ms.schedule = schedule;
        ms.migerateStatus = statusEnum == null ? MigrateStatusEnum.MIGERATING : statusEnum;
        ms.reportTimeStamp = System.currentTimeMillis();
        ms.message = message;
        if (schedule == -1) {
            ms.migerateStatus = MigrateStatusEnum.MIGERATE_ERROR;
        }
        if (schedule == 100) {// schedule=100也是finish
            ms.migerateStatus = MigrateStatusEnum.FINISH;
        }
    }

    public synchronized void resetReportTime() {
        waitingReportTime = System.currentTimeMillis() - reportTimeStamp;
        this.hadReportTimeout = false;// 超时开关恢复
        if (statusItemMap != null && !statusItemMap.isEmpty()) {
            for (Entry<String, MigrateStatus> entry : statusItemMap.entrySet()) {
                entry.getValue().setWaitingReportTime(
                                                      System.currentTimeMillis()
                                                              - entry.getValue().getReportTimeStamp());
                entry.getValue().hadReportTimeout = false;// 超时开关恢复
            }
        }
    }

    /**
     * 迁移过程报告超时
     * 
     * @param status
     * @return
     */
    public boolean isTimeout() {
        if (!this.isHadReportTimeout() && this.getWaitingReportTime() >= MigrateStatus.getReporttimeout()) {
            this.setHadReportTimeout(true);
            return true;
        }
        Map<String, MigrateStatus> statusItemMap = this.getStatusItemMap();
        if (statusItemMap != null && !statusItemMap.isEmpty()) {
            for (Entry<String, MigrateStatus> statusItem : statusItemMap.entrySet()) {
                MigrateStatus ms = statusItem.getValue();
                if (!ms.isHadReportTimeout() && ms.getWaitingReportTime() >= MigrateStatus.getReporttimeout()) {
                    ms.setHadReportTimeout(true);
                    return true;
                }
            }
        }
        return false;
    }

    public MigrateStatusEnum getMigerateStatus() {
        return migerateStatus;
    }

    public void setMigerateStatus(MigrateStatusEnum migerateStatus) {
        this.migerateStatus = migerateStatus;
    }

    public String getPhysicalId() {
        return physicalId;
    }

    public MigrateTypeEnum getMigerateType() {
        return migerateType;
    }

    public long getReportTimeStamp() {
        return reportTimeStamp;
    }

    public void setReportTimeStamp(long reportTimeStamp) {
        this.reportTimeStamp = reportTimeStamp;
    }

    public long getWaitingReportTime() {
        return waitingReportTime;
    }

    public void setWaitingReportTime(long waitingReportTime) {
        this.waitingReportTime = waitingReportTime;
    }

    public boolean isHadReportTimeout() {
        return hadReportTimeout;
    }

    public void setHadReportTimeout(boolean hadReportTimeout) {
        this.hadReportTimeout = hadReportTimeout;
    }

    public static long getReporttimeout() {
        return reportTimeout;
    }

    public String toString() {
        String item = "";
        if (statusItemMap != null && !statusItemMap.isEmpty()) {
            item = "------item detail:";
            for (Entry<String, MigrateStatus> entry : statusItemMap.entrySet()) {
                item += entry.getValue().toString();
            }
        }

        return NodesManager.getInstance().getLogFormatNodeId(physicalId) + SPLIT + physicalId + SPLIT + "Before "
               + waitingReportTime + "ms " + SPLIT + migerateType.getValue() + SPLIT + migerateStatus.getValue()
               + SPLIT + schedule + "%" + "M:" + message + SPLIT + item;
    }
}

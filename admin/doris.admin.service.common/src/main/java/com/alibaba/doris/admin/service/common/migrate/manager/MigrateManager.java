package com.alibaba.doris.admin.service.common.migrate.manager;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.monitor.MonitorEnum;
import com.alibaba.doris.admin.monitor.SystemLogMonitor;
import com.alibaba.doris.admin.service.common.AdminServiceException;
import com.alibaba.doris.admin.service.common.migrate.MigrateThread;
import com.alibaba.doris.admin.service.common.migrate.status.MigrateStatus;
import com.alibaba.doris.common.MigrateStatusEnum;
import com.alibaba.doris.common.MigrateTypeEnum;
import com.alibaba.doris.common.MonitorWarnConstants;

/**
 * 迁移管理器，总控迁移状态，迁移线程
 * 
 * @author frank
 */
public class MigrateManager {

    private static final Log           log              = LogFactory.getLog(MigrateManager.class);
    private static MigrateManager      instance         = new MigrateManager();
    private Map<String, MigrateStatus> migrateStatusMap = new ConcurrentHashMap<String, MigrateStatus>(); // key：物理节点编号
    private Map<String, MigrateThread> migrateThreadMap = new ConcurrentHashMap<String, MigrateThread>(); // 失效迁移：key为失效物理节点编号，扩容迁移：key为序列编号

    private MigrateManager() {
    }

    public static MigrateManager getInstance() {
        return instance;
    }

    public MigrateThread getMigerateThread(String key) {
        return migrateThreadMap.get(key);
    }

    public MigrateStatus getMigerateStatus(String physicalId) {
        return migrateStatusMap.get(physicalId);
    }

    /**
     * 更新迁移状态，供dataserver报告迁移状态时调用
     * 
     * @param sourcePhysicalId
     * @param targetPhysicalId
     * @param schedule
     * @param statusEnum TODO
     * @param message TODO
     */
    public synchronized void updateMigerateStatus(String sourcePhysicalId, String targetPhysicalId, int schedule,
                                                  MigrateStatusEnum statusEnum, String message) {
        if (log.isDebugEnabled()) {
            log.debug("update migrate status--- src:" + sourcePhysicalId + " target:" + targetPhysicalId + " schedule:"
                      + schedule + " statusEnum:" + statusEnum + " message:" + message);
        }
        MigrateStatus status;
        if (targetPhysicalId != null) {
            status = migrateStatusMap.get(targetPhysicalId);
            if (status != null) {// targetPhysicalId 正在恢复
                status.setSchedule(sourcePhysicalId, schedule, statusEnum, message);
            }
        }
        status = migrateStatusMap.get(sourcePhysicalId);
        if (status != null) {
            status.setSchedule(sourcePhysicalId, schedule, statusEnum, message);
        }
        
        if (schedule == -1 || statusEnum == MigrateStatusEnum.MIGERATE_ERROR) {
            // message 'Incorrect migrate progress:' is used as dragoon warning rule, should be consistent.
            SystemLogMonitor.error(MonitorEnum.MIGRATION,
                    MonitorWarnConstants.INCORRECT_MIGRATE_PROGRESS + ":" + sourcePhysicalId + "->" + targetPhysicalId);
        }
    }

    /**
     * 添加迁移任务状态管理
     * 
     * @param physicalId 如果为临时failover、或永久failover迁移，为迁入节点（即恢复节点）；如果为expansion，为迁出节点
     * @param migerateType 迁移类型
     * @param status 初始迁移状态
     * @throws AdminServiceException
     */
    public MigrateStatus addMigerateNode(String physicalId, MigrateTypeEnum migerateType, MigrateStatusEnum status) {
        MigrateStatus migerateStatus = new MigrateStatus(physicalId, migerateType, status);
        migrateStatusMap.put(physicalId, migerateStatus);
        return migerateStatus;

    }

    public void addMigerateThread(String key, MigrateThread thread) {
        migrateThreadMap.put(key, thread);
    }

    public void removeMigerateNode(String physicalId) {
        migrateStatusMap.remove(physicalId);
    }

    public void removeMigerateThread(String key) {
        migrateThreadMap.remove(key);
    }

    /**
     * 序列是否正在扩容迁移中
     * 
     * @param sequence
     * @return
     */
    public boolean isMigrating(String migKey) {
        if (migrateStatusMap.get(migKey) != null) {// 节点在迁移中
            return true;
        }
        if (migrateThreadMap.get(migKey) != null) {// 序列在迁移中
            return true;
        }
        for (Entry<String, MigrateThread> entry : migrateThreadMap.entrySet()) {// 扩容目标节点在迁移中
            List<String> list = entry.getValue().getMigeratingNodePhysicalIdList();
            for (String physicalId : list) {
                if (migKey.equals(physicalId)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 清除集群全部迁移状态
     */
    public void clearStatus(){
        migrateStatusMap.clear();
        migrateThreadMap.clear();
    }
}

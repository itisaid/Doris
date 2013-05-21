package com.alibaba.doris.admin.service.common.migrate.status;

import com.alibaba.doris.admin.service.common.migrate.manager.MigrateManager;
import com.alibaba.doris.common.MigrateStatusEnum;

/**
 * 迁移状态查看器，查看迁移进度
 * 
 * @author frank
 */
public class MigrateStatusWatcher {

    private static MigrateStatusWatcher instance = new MigrateStatusWatcher();
    private MigrateManager              manager  = MigrateManager.getInstance();

    private MigrateStatusWatcher() {

    }

    public static MigrateStatusWatcher getInstance() {
        return instance;
    }

    /**
     * 获得物理节点的迁移进程
     * 
     * @param physicalId 节点的物理编号
     * @return  0 没有迁移进度; 100 迁移完成
     */
    public int getMigerateProgress(String physicalId) {
        MigrateStatus migerateStatus = manager.getMigerateStatus(physicalId);
        if (migerateStatus == null) {
            return 0;
        }
        return migerateStatus.getSchedule();
    }
    
    /**
     * 获得物理节点的迁移状态
     * 
     * @param physicalId 节点的物理编号
     * @return 迁移状态枚举值
     */
    public MigrateStatusEnum getMigerateStatus(String physicalId) {
        MigrateStatus migerateStatus = manager.getMigerateStatus(physicalId);
        if (migerateStatus == null) {
            return null;
        }
        return migerateStatus.getMigerateStatus();
    }

    /**
     * 获得物理节点的迁移状态详细信息
     * 
     * @param physicalId 节点物理编号
     * @return 迁移状态详细信息
     */
    public String getMigerateStatusDetail(String physicalId) {
        MigrateStatus migerateStatus = manager.getMigerateStatus(physicalId);
        if (migerateStatus == null) {
            return null;
        }
        return migerateStatus.toString();
    }

}

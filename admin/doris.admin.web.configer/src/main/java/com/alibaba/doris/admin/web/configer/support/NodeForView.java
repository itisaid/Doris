package com.alibaba.doris.admin.web.configer.support;

import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;

public class NodeForView {

    private PhysicalNodeDO physicalNodeDO;

    private String         healthStatus;

    private String         migrationStatus;

    private int            migrateProgress;

    private String         migrationStatusDetail;

    private String         routeStatus;
    
    public int getId() {
        return physicalNodeDO.getId();
    }

    public int getLogicalId() {
        return physicalNodeDO.getLogicalId();
    }

    public String getPhysicalId() {
        return physicalNodeDO.getPhysicalId();
    }

    public int getSerialId() {
        return physicalNodeDO.getSerialId();
    }

    public String getMachineId() {
        return physicalNodeDO.getMachineId();
    }

    public String getIp() {
        return physicalNodeDO.getIp();
    }

    public int getPort() {
        return physicalNodeDO.getPort();
    }

    /**
     * @return 迁移状态
     */
    public String getMigrationStatus() {
        return migrationStatus;
    }

    /**
     * @return 健康状态
     */
    public String getHealthStatus() {
        return healthStatus;
    }

    public String getRouteStatus() {
        return routeStatus;
    }

    /**
     * @return 路由状态
     */
    public void setRouteStatus(String routeStatus) {
        this.routeStatus = routeStatus;
    }

    public void setPhysicalNodeDO(PhysicalNodeDO physicalNodeDO) {
        this.physicalNodeDO = physicalNodeDO;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public void setMigrationStatus(String migrationStatus) {
        this.migrationStatus = migrationStatus;
    }

    public String getMigrationStatusDetail() {
        return migrationStatusDetail;
    }

    public void setMigrationStatusDetail(String migrationStatusDetail) {
        this.migrationStatusDetail = migrationStatusDetail;
    }

    public int getMigrateProgress() {
        return migrateProgress;
    }

    public void setMigrateProgress(int migrateProgress) {
        this.migrateProgress = migrateProgress;
    }

    public PhysicalNodeDO getPhysicalNodeDO() {
        return physicalNodeDO;
    }

}

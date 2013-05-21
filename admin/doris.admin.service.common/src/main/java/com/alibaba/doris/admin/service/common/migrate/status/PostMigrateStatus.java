package com.alibaba.doris.admin.service.common.migrate.status;

import com.alibaba.doris.common.MigrateStatusEnum;

public class PostMigrateStatus {

    private String            physicalId;
    private int               schedule;
    private MigrateStatusEnum status;
    private String            message;

    public String getPhysicalId() {
        return physicalId;
    }

    public void setPhysicalId(String physicalId) {
        this.physicalId = physicalId;
    }

    public int getSchedule() {
        return schedule;
    }

    public void setSchedule(int schedule) {
        this.schedule = schedule;
    }

    public MigrateStatusEnum getStatus() {
        return status;
    }

    public void setStatus(MigrateStatusEnum status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return physicalId + " " + status.getValue() + schedule + "% " + "M:" + message;
    }
}

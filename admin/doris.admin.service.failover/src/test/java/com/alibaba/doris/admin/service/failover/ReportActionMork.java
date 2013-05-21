package com.alibaba.doris.admin.service.failover;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.doris.admin.service.common.migrate.AdminMigrateStatusReportAction;
import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.MigrateStatusEnum;

public class ReportActionMork {

    public static void morkReport(String sourcePhysicalId, String targetPhysicalId, String schedule,
                                  MigrateStatusEnum status, String message) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AdminServiceConstants.MIGRATE_REPORT_SOURCE_NODE_PORT, sourcePhysicalId);
        params.put(AdminServiceConstants.MIGRATE_REPORT_TARGET_NODE_PHYSICAL_ID, targetPhysicalId);
        params.put(AdminServiceConstants.MIGRATE_REPORT_SCHEDULE, schedule);
        params.put(AdminServiceConstants.MIGRATE_REPORT_STATUS, status.getValue());
        params.put(AdminServiceConstants.MIGRATE_REPORT_MESSAGE, message);
        AdminMigrateStatusReportAction.getInstance().execute(params);
    }
}

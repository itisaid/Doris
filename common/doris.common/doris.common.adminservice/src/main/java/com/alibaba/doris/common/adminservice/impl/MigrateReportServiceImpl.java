package com.alibaba.doris.common.adminservice.impl;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.MigrateStatusEnum;
import com.alibaba.doris.common.adminservice.BaseAdminService;
import com.alibaba.doris.common.adminservice.MigrateReportService;

public class MigrateReportServiceImpl extends BaseAdminService<String> implements MigrateReportService {

    private static MigrateReportService instance = new MigrateReportServiceImpl();

    private MigrateReportServiceImpl() {
    }

    public static MigrateReportService getInstance() {
        return instance;
    }

    public String report(String srcPhysicalId, String targetPhysicalId, int schedule, MigrateStatusEnum status,
                         String message) {

        Map<String, String> params = new HashMap<String, String>();
        params.put(AdminServiceConstants.MIGRATE_REPORT_SOURCE_NODE_PORT, srcPhysicalId);
        params.put(AdminServiceConstants.MIGRATE_REPORT_TARGET_NODE_PHYSICAL_ID, targetPhysicalId);
        params.put(AdminServiceConstants.MIGRATE_REPORT_SCHEDULE, String.valueOf(schedule));
        params.put(AdminServiceConstants.MIGRATE_REPORT_STATUS, status.getValue());
        params.put(AdminServiceConstants.MIGRATE_REPORT_MESSAGE, message);
        return requestForce(params);
    }

    @Override
    public String convert(String response) {
        return response;
    }

    @Override
    public String getActionName() {
        return AdminServiceConstants.MIGRATE_REPORT_ACTION;
    }

}

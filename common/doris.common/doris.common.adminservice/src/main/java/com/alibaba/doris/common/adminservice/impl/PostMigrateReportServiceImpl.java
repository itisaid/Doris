package com.alibaba.doris.common.adminservice.impl;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.MigrateStatusEnum;
import com.alibaba.doris.common.adminservice.BaseAdminService;
import com.alibaba.doris.common.adminservice.PostMigrateReportService;

public class PostMigrateReportServiceImpl extends BaseAdminService<String> implements PostMigrateReportService {

    private static PostMigrateReportServiceImpl instance = new PostMigrateReportServiceImpl();

    private PostMigrateReportServiceImpl() {

    }

    public static PostMigrateReportServiceImpl getInstance() {
        return instance;
    }

    public String report(String physicalPort, int schedule, MigrateStatusEnum status, String message) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AdminServiceConstants.POST_MIGRATE_REPORT_NODE_PORT, physicalPort);
        params.put(AdminServiceConstants.POST_MIGRATE_REPORT_SCHEDULE, String.valueOf(schedule));
        params.put(AdminServiceConstants.POST_MIGRATE_REPORT_STATUS, status.getValue());
        params.put(AdminServiceConstants.POST_MIGRATE_REPORT_MESSAGE, message);
        return requestForce(params);
    }

    @Override
    public String convert(String response) {
        return response;
    }

    @Override
    public String getActionName() {
        return AdminServiceConstants.POST_MIGRATE_REPORT_ACTION;
    }

}

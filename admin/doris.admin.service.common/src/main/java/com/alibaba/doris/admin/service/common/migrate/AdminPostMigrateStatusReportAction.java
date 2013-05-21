package com.alibaba.doris.admin.service.common.migrate;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.service.common.AdminServiceAction;
import com.alibaba.doris.admin.service.common.migrate.manager.PostMigrateStatusManager;
import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.MigrateStatusEnum;

public class AdminPostMigrateStatusReportAction implements AdminServiceAction {

    private static final Log                          log      = LogFactory.getLog(AdminPostMigrateStatusReportAction.class);
    private static AdminPostMigrateStatusReportAction instance = new AdminPostMigrateStatusReportAction();

    private AdminPostMigrateStatusReportAction() {

    }

    public static AdminPostMigrateStatusReportAction getInsatance() {
        return instance;
    }

    public String execute(Map<String, String> params) {
        String remoteIp = params.get(AdminServiceConstants.REMOTE_IP);
        String port = params.get(AdminServiceConstants.POST_MIGRATE_REPORT_NODE_PORT);
        String physicalId = remoteIp + ":" + port;
        if (log.isDebugEnabled()) {
            log.debug("post migrate report from:" + physicalId + "status:"
                      + params.get(AdminServiceConstants.POST_MIGRATE_REPORT_STATUS));
        }
        PostMigrateStatusManager.getInstance().updatePostMigrateStatus(
                                                                       physicalId,
                                                                       Integer.valueOf(params.get(AdminServiceConstants.POST_MIGRATE_REPORT_SCHEDULE)),
                                                                       MigrateStatusEnum.getEnum(params.get(AdminServiceConstants.POST_MIGRATE_REPORT_STATUS)),
                                                                       params.get(AdminServiceConstants.POST_MIGRATE_REPORT_MESSAGE));
        return "OK";
    }

}

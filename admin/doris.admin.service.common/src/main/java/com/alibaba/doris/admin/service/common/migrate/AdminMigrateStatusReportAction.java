package com.alibaba.doris.admin.service.common.migrate;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.service.common.AdminServiceAction;
import com.alibaba.doris.admin.service.common.migrate.manager.MigrateManager;
import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.MigrateStatusEnum;

/**
 * 完成data server迁移状态报告处理
 * 
 * @author frank
 */
public class AdminMigrateStatusReportAction implements AdminServiceAction {
    private static final Log           log              = LogFactory.getLog(AdminMigrateStatusReportAction.class);

    private static AdminMigrateStatusReportAction instance = new AdminMigrateStatusReportAction();

    private AdminMigrateStatusReportAction() {
    }

    public static AdminMigrateStatusReportAction getInstance() {
        return instance;
    }

    public String execute(Map<String, String> params) {
        String remoteIp = params.get(AdminServiceConstants.REMOTE_IP);
        String port = params.get(AdminServiceConstants.MIGRATE_REPORT_SOURCE_NODE_PORT);
        String sourcePhysicalId = remoteIp + ":" + port;
        if (log.isDebugEnabled()) {
            log.debug("migrate report src:" + sourcePhysicalId );
        }
        MigrateManager.getInstance().updateMigerateStatus(
                                                          sourcePhysicalId,
                                                          params.get(AdminServiceConstants.MIGRATE_REPORT_TARGET_NODE_PHYSICAL_ID),
                                                          Integer.valueOf(params.get(AdminServiceConstants.MIGRATE_REPORT_SCHEDULE)),
                                                          MigrateStatusEnum.getEnum(params.get(AdminServiceConstants.MIGRATE_REPORT_STATUS)),
                                                          params.get(AdminServiceConstants.MIGRATE_REPORT_MESSAGE));
        return "OK";
    }

}

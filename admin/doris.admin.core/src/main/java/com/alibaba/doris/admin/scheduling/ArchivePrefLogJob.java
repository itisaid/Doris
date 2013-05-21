package com.alibaba.doris.admin.scheduling;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.alibaba.doris.admin.core.AdminServiceLocator;

public class ArchivePrefLogJob extends QuartzJobBean {

    protected void executeInternal(JobExecutionContext ctx) throws JobExecutionException {
        if (AdminServiceLocator.getAdminService().isMasterAdmin()) {
            AdminServiceLocator.getMonitorService().archiveWithTx(1);
        }
    }
}

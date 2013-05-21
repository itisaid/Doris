package com.alibaba.doris.admin.monitor;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.service.SystemLogService;

public class SystemLogMonitor {

    private static SystemLogService systemLogService = AdminServiceLocator.getSystemLogService();
    static Log                      log              = LogFactory.getLog(SystemLogMonitor.class);

    /**
     * 记录日志
     * 
     * @param actionName:<code>MonitorEnum</code>
     * @param message
     */
    public static void info(MonitorEnum actionName, String message) {
        systemLogService.info(actionName.getName(), message);
    }

    /**
     * 记录日志并发送报警
     * 
     * @param subject
     * @param detail
     */
    public static void error(MonitorEnum actionName, String message, Throwable e) {
        // XXME 增加报警接口调用
        if (StringUtils.isBlank(message)) {
            message = e.getMessage();
        }
        DorisMonitorException monitorException = new DorisMonitorException(message, e);

        log.error(actionName.getName() + ":" + monitorException.getMessage(), monitorException);

        systemLogService.error(actionName.getName(), message);
    }


    public static void error(MonitorEnum actionName, String message) {
        DorisMonitorException monitorException = new DorisMonitorException(message);
        error(actionName, message, monitorException);
    }

}

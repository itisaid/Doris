package com.alibaba.doris.admin.service.common.consistent;

import java.util.Map;

import com.alibaba.doris.admin.service.common.AdminServiceAction;

/**
 * 类ConsistentReportAction.java的实现描述：TODO 类实现描述
 * 
 * @author hongwei.zhaohw 2012-1-6
 */
public class ConsistentReportAction implements AdminServiceAction {

    private static ConsistentReportAction  instance                = new ConsistentReportAction();

    private ConsistentReportAction() {
    }

    public static ConsistentReportAction getInstance() {
        return instance;
    }

    public String execute(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return Boolean.toString(false);
        }

        ConsistentReportTaskManager.getInstance().produceReportTask(params);

        return Boolean.toString(true);
    }

}

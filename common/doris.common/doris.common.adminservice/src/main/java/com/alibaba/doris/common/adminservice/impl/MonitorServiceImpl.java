package com.alibaba.doris.common.adminservice.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.PrefReportUnit;
import com.alibaba.doris.common.adminservice.BaseAdminService;
import com.alibaba.doris.common.adminservice.MonitorService;
import com.alibaba.fastjson.JSON;

/**
 * 性能监控
 * 
 * @author helios
 */
public class MonitorServiceImpl extends BaseAdminService<Boolean> implements MonitorService {

    private static MonitorServiceImpl instance = new MonitorServiceImpl();

    private MonitorServiceImpl() {

    }

    public static MonitorServiceImpl getInstance() {
        return instance;
    }

    public String report(List<PrefReportUnit> report, int port) {
        String reportContent = JSON.toJSONString(report);

        Map<String, String> params = new HashMap<String, String>();
        params.put(AdminServiceConstants.MONITOR_REPORT_PREF_OBJECT, reportContent);
        params.put(AdminServiceConstants.REMOTE_PORT, "" + port);


        requestForce(params);
        
        return reportContent;
    }

    @Override
    public Boolean convert(String response) {
        return Boolean.TRUE;
    }

    @Override
    public String getActionName() {
        return AdminServiceConstants.MONITOR_ACTION;
    }

}

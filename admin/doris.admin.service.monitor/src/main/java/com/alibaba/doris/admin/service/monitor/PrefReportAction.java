/**
 * 
 */
package com.alibaba.doris.admin.service.monitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.PrefLogDO;
import com.alibaba.doris.admin.service.MonitorService;
import com.alibaba.doris.admin.service.common.AdminServiceAction;
import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.PrefReportUnit;
import com.alibaba.fastjson.JSON;

/**
 * @author helios
 */
public class PrefReportAction implements AdminServiceAction {

    private static final PrefReportAction instance = new PrefReportAction();

    private PrefReportAction() {
        super();
    }

    public static PrefReportAction getInstance() {
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.alibaba.doris.admin.service.common.AdminServiceAction#execute(java
     * .util.Map)
     */
    public String execute(Map<String, String> params) {
        MonitorService monitorService = AdminServiceLocator.getService(MonitorService.class);

        String jsonValue = params.get(AdminServiceConstants.MONITOR_REPORT_PREF_OBJECT);
        String remoteIp = params.get(AdminServiceConstants.REMOTE_IP);
        String physicalId = remoteIp + ":" + params.get(AdminServiceConstants.REMOTE_PORT);

        List<PrefReportUnit> reportList = JSON.parseArray(jsonValue, PrefReportUnit.class);

        List<PrefLogDO> prefLogs = new ArrayList<PrefLogDO>();

        for (PrefReportUnit unit : reportList) {
            if (isValid(unit)) {

                PrefLogDO preflog = convert(unit);

                preflog.setPhysicalId(physicalId);
                prefLogs.add(preflog);
            }
        }

        monitorService.savePrefReports(prefLogs);

        return "";
    }

    private boolean isValid(PrefReportUnit unit) {
        return unit != null && unit.getActionName() != null;
    }

    private PrefLogDO convert(PrefReportUnit unit) {
        PrefLogDO preflog = new PrefLogDO();

        preflog.setActionName(unit.getActionName());
        preflog.setNameSpace(unit.getNameSpace());
        preflog.setMinLatency(unit.getMinLatency());
        preflog.setMaxLatency(unit.getMaxLatency());

        preflog.setThe80thLatency(unit.getThe80thLatency());
        preflog.setThe95thLatency(unit.getThe95thLatency());
        preflog.setThe99thLatency(unit.getThe99thLatency());

        preflog.setMaxConcurrencyLevel(unit.getMaxConcurrencyLevel());

        preflog.setTotalOperations(unit.getTotalOperation());

        preflog.setTotalLatency(unit.getTotalLatency());
        preflog.setTotalBytes(unit.getTotalBytes());

        preflog.setTimeStart(new Date(unit.getTimeStart()));
        preflog.setTimeUsed(unit.getTimeUsed());

        return preflog;
    }

}

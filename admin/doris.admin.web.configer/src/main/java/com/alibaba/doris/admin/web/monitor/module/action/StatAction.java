package com.alibaba.doris.admin.web.monitor.module.action;

import java.util.Date;

import org.apache.commons.lang.math.NumberUtils;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.service.MonitorService;
import com.alibaba.doris.admin.support.PrefQuery;

public class StatAction {

    private static final int DEFAULT_OFFSET = 3600 * 1000 * 12;

    private MonitorService monitorService = AdminServiceLocator.getMonitorService();

    public void doQuery(Context context, @Param("timeOffset") String timeOffset,
                        @Param("physicalId") String physicalId, @Param("nameSpace") String nameSpace) {

        PrefQuery query = new PrefQuery();
        query.setGmtStart(new Date(System.currentTimeMillis()
                - NumberUtils.toInt(timeOffset, DEFAULT_OFFSET)));
        query.setPhysicalId(physicalId);
        query.setNameSpace(nameSpace);

        context.put("stats", monitorService.statByQuery(query));
        context.put("physicalId", physicalId);
        context.put("nameSpace", nameSpace);
    }

    public void doStatWithNameSpace(Context context, @Param("timeOffset") String timeOffset,
                                    @Param("physicalId") String physicalId,
                                    @Param("nameSpace") String nameSpace) {

        PrefQuery query = new PrefQuery();
        query.setGmtStart(new Date(System.currentTimeMillis()
                - NumberUtils.toInt(timeOffset, DEFAULT_OFFSET)));
        query.setPhysicalId(physicalId);
        query.setNameSpace(nameSpace);

        context.put("stats", monitorService.statWithNameSpace(query));
        context.put("physicalId", physicalId);
        context.put("nameSpace", nameSpace);
    }

    public void doStatWithPhysicalId(Context context, @Param("timeOffset") String timeOffset,
                                     @Param("physicalId") String physicalId,
                                     @Param("nameSpace") String nameSpace) {

        PrefQuery query = new PrefQuery();
        query.setGmtStart(new Date(System.currentTimeMillis()
                - NumberUtils.toInt(timeOffset, DEFAULT_OFFSET)));
        query.setPhysicalId(physicalId);
        query.setNameSpace(nameSpace);

        context.put("stats", monitorService.statWithPhysicalId(query));
        context.put("physicalId", physicalId);
        context.put("nameSpace", nameSpace);
    }

}

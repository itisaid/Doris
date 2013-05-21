package com.alibaba.doris.admin.web.configer.module.screen;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.ConsistentReportDO;
import com.alibaba.doris.admin.service.AdminService;
import com.alibaba.doris.admin.service.ConsistentReportService;
import com.alibaba.doris.admin.web.configer.util.DateUtil;
import com.alibaba.doris.admin.web.configer.util.PageViewUtil;
import com.alibaba.doris.admin.web.configer.util.WebConstant;

public class ConsistentReportList {

    Log                     logger                  = LogFactory.getLog(ConsistentReportList.class);

    AdminService            adminService            = AdminServiceLocator.getAdminService();
    ConsistentReportService consistentReportService = AdminServiceLocator.getConsistentReportService();

    @SuppressWarnings("unchecked")
    public void execute(Context context, @Param("startTime") String startTime, @Param("endTime") String endTime,
                        @Param("errorType") String errorType, @Param("currentpage") String currentpage) {
        boolean isMasterAdmin = adminService.isMasterAdmin();
        context.put("isMasterAdmin", isMasterAdmin);

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        DateUtil.setStartTimeAndEndTime(startTime, endTime, start, end);

        SimpleDateFormat sfWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        startTime = sfWithTime.format(start.getTime());
        endTime = sfWithTime.format(end.getTime());

        int currentpageNum = NumberUtils.toInt(currentpage, 1);
        @SuppressWarnings("rawtypes")
        Map paramMap = new HashMap();
        paramMap.put("startTime", startTime);
        paramMap.put("endTime", endTime);
        paramMap.put("errorType", errorType);
        paramMap.put("startRow", (currentpageNum - 1) * WebConstant.DEFAULT_ITEMS_PER_PAGE);
        paramMap.put("pageSize", WebConstant.DEFAULT_ITEMS_PER_PAGE);

        int totalLines = consistentReportService.countConsistentReport(paramMap);
        List<ConsistentReportDO> consistentReportList = consistentReportService.queryConsistentReport(paramMap);

        context.put("consistentReportList", consistentReportList);
        context.put("startTime", startTime.substring(0, 10));
        context.put("endTime", endTime.substring(0, 10));
        context.put("errorType", errorType);
        context.put("pageView", PageViewUtil.buildPageView(currentpageNum, totalLines));

    }


}

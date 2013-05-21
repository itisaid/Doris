package com.alibaba.doris.admin.web.configer.module.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.service.ConsistentReportService;
import com.alibaba.doris.admin.web.configer.util.DateUtil;

public class ConsistentReportAction {

    ConsistentReportService consistentReportService = AdminServiceLocator.getConsistentReportService();

    public void doRemoveConsistentReport(Context context, Navigator nav, HttpServletRequest request) {
        String consistentReportIds = request.getParameter("consistentReportIds");
        if (StringUtils.isBlank(consistentReportIds)) {
            context.put("message", "请输入正确的id");
            return;
        }

        List<Integer> idList = retrieveIds(consistentReportIds);
        if (idList == null || idList.isEmpty()) {
            context.put("message", "请输入正确的id");
            return;
        }

        Integer deleteByRows = consistentReportService.deleteByIds(idList);
        if (deleteByRows > 0) {
            context.put("message", "删除成功!");
        } else {
            context.put("message", "删除失败");
        }
    }

    @SuppressWarnings("unchecked")
    private List<Integer> retrieveIds(String consistentReportIds) {
        if (StringUtils.isBlank(consistentReportIds)) {
            return Collections.EMPTY_LIST;
        }

        String[] strIds = consistentReportIds.split(",");
        if (strIds == null || strIds.length < 1) {
            return Collections.EMPTY_LIST;
        }

        List<Integer> idList = new ArrayList<Integer>(strIds.length);
        for (String idStr : strIds) {
            int id = Integer.parseInt(idStr);
            idList.add(id);
        }
        return idList;
    }

    /**
     * 按时间段批量删除一致性问题报告
     * 
     * @param context
     * @param deleteStartTime
     * @param deleteEndTime
     */
    public void doBatchDelete(Context context, @Param("deleteStartTime")
    String deleteStartTime, @Param("deleteEndTime")
    String deleteEndTime) {

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        DateUtil.setStartTimeAndEndTime(deleteStartTime, deleteEndTime, start, end);

        SimpleDateFormat sfWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        deleteStartTime = sfWithTime.format(start.getTime());
        deleteEndTime = sfWithTime.format(end.getTime());

        Integer deletedRows = consistentReportService.deleteByGmtCreate(deleteStartTime, deleteEndTime);
        context.put("deleteRows", deletedRows);
    }

}

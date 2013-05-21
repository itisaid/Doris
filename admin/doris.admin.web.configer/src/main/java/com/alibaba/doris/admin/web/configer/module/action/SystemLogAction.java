package com.alibaba.doris.admin.web.configer.module.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.SystemLogDO;
import com.alibaba.doris.admin.service.SystemLogService;
import com.alibaba.doris.admin.web.configer.support.SystemLogView;
import com.alibaba.doris.admin.web.configer.util.PageViewUtil;
import com.alibaba.doris.admin.web.configer.util.WebConstant;

/**
 * @project :Doris
 * @author : len.liu
 * @datetime : 2011-7-18 下午07:20:25
 * @version :0.1
 * @Modification:
 */
public class SystemLogAction {

    Log              log              = LogFactory.getLog(SystemLogAction.class);
    SystemLogService systemLogService = AdminServiceLocator.getSystemLogService();

    @SuppressWarnings("unchecked")
    public void doQuery(Context context, @Param("startTime") String startTime, @Param("endTime") String endTime,
                        @Param("actionName") String actionName, @Param("currentpage") String currentpage) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        
        if (StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime)) {
            // 当前时间向前一天（24小时）内的systemlog
            setTime(start, 0,0,0,0);
            setTime(end, 23,59,59,999);
        } else if (!StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime)) {
            try {
                start.setTime(sf.parse(startTime));
                
                end.setTime(start.getTime());
                setTime(end, 23,59,59,999);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (StringUtils.isBlank(startTime) && !StringUtils.isBlank(endTime)) {
            try {
                end.setTime(sf.parse(endTime));

                start.setTime(end.getTime());
                setTime(start, 0,0,0,0);
                setTime(end, 23,59,59,999);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                end.setTime(sf.parse(endTime));
                start.setTime(sf.parse(startTime));

                //start.setTime(end.getTime());
                setTime(end, 23,59,59,999);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        
        SimpleDateFormat sfWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        startTime = sfWithTime.format(start.getTime());
        endTime = sfWithTime.format(end.getTime());
        
        int currentpageNum = NumberUtils.toInt(currentpage, 1);
        @SuppressWarnings("rawtypes")
        Map paramMap = new HashMap();
        paramMap.put("startTime", startTime);
        paramMap.put("endTime", endTime);
        paramMap.put("actionName", actionName);
        paramMap.put("startRow", (currentpageNum - 1) * WebConstant.DEFAULT_ITEMS_PER_PAGE);
        paramMap.put("pageSize", WebConstant.DEFAULT_ITEMS_PER_PAGE);

        int totalLines = systemLogService.querySystemLogCounts(paramMap);
        List<SystemLogDO> logList = systemLogService.querySystemLogs(paramMap);

        context.put("logList", formateLogList(logList));
        context.put("startTime", startTime.substring(0, 10));
        context.put("endTime", endTime.substring(0, 10));
        context.put("actionName", actionName);
        context.put("pageView", PageViewUtil.buildPageView(currentpageNum, totalLines));
    }

    private void setTime(Calendar end, int hour, int minute, int second, int millsecond) {
        end.set(Calendar.HOUR_OF_DAY, hour);
        end.set(Calendar.MINUTE, minute);
        end.set(Calendar.SECOND, second);
        end.set(Calendar.MILLISECOND, millsecond);
    }

    private static List<SystemLogView> formateLogList(List<SystemLogDO> logList) {
        List<SystemLogView> viewList = new ArrayList<SystemLogView>();
        if (logList == null) return viewList; 
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (SystemLogDO logDo : logList) {
            SystemLogView view = new SystemLogView();
            view.setActionTime(sf.format(logDo.getActionTime()));
            view.setId(logDo.getId());
            view.setActionName(logDo.getActionName());
            view.setGmtCreate(logDo.getGmtCreate());
            view.setGmtModified(logDo.getGmtModified());
            view.setLogInfo(logDo.getLogInfo());
            viewList.add(view);
        }
        return viewList;
    }

}

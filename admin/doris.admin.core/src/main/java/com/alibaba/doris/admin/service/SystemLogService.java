package com.alibaba.doris.admin.service;

import java.util.List;
import java.util.Map;

import com.alibaba.doris.admin.dataobject.SystemLogDO;

public interface SystemLogService {

    @SuppressWarnings("unchecked")
    List<SystemLogDO> querySystemLogs( Map paramMap);
    
    int querySystemLogCounts( Map paramMap);

    //
    // void insertSystemLog(SystemLogDO systemLogDO);
    //
    // void insertSystemLogs(List<SystemLogDO> systemLogs);

    public void info(String subject, String detail);

    public void error(String subject, String detail);
}

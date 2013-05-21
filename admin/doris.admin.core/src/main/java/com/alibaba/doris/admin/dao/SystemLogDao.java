package com.alibaba.doris.admin.dao;

import java.util.List;
import java.util.Map;

import com.alibaba.doris.admin.dataobject.SystemLogDO;

public interface SystemLogDao {

    @SuppressWarnings("unchecked")
    List<SystemLogDO> querySystemLogs(Map paramMap);

    @SuppressWarnings("unchecked")
    int querySystemLogCounts(Map paramMap);

    void insertSystemLog(SystemLogDO systemLogDO);

    void insertSystemLogs(List<SystemLogDO> systemLogs);
}

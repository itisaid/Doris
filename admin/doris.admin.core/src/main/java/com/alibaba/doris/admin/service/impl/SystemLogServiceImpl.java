package com.alibaba.doris.admin.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.doris.admin.dao.SystemLogDao;
import com.alibaba.doris.admin.dataobject.SystemLogDO;
import com.alibaba.doris.admin.service.SystemLogService;

public class SystemLogServiceImpl implements SystemLogService {

    private SystemLogDao systemLogDao;

    public void insertSystemLog(SystemLogDO systemLogDO) {
        systemLogDao.insertSystemLog(systemLogDO);
    }

    public void insertSystemLogs(List<SystemLogDO> systemLogs) {
        systemLogDao.insertSystemLogs(systemLogs);
    }

    @SuppressWarnings("unchecked")
    public List<SystemLogDO> querySystemLogs(Map paramMap) {
        return systemLogDao.querySystemLogs(paramMap);
    }

    public void setSystemLogDao(SystemLogDao systemLogDao) {
        this.systemLogDao = systemLogDao;
    }

    public void error(String actionName, String logInfo) {
        SystemLogDO logDo = new SystemLogDO();
        logDo.setActionName(actionName);
        logDo.setLogInfo(logInfo);
        logDo.setActionTime(new Date());
        logDo.setGmtCreate(new Date());
        logDo.setGmtModified(new Date());
        insertSystemLog(logDo);

    }

    public void info(String actionName, String logInfo) {
        SystemLogDO logDo = new SystemLogDO();
        logDo.setActionName(actionName);
        logDo.setLogInfo(logInfo);
        logDo.setActionTime(new Date());
        logDo.setGmtCreate(new Date());
        logDo.setGmtModified(new Date());
        insertSystemLog(logDo);

    }

    @SuppressWarnings("unchecked")
    public int querySystemLogCounts( Map paramMap) {
        return systemLogDao.querySystemLogCounts( paramMap);
    }

}

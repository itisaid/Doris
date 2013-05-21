package com.alibaba.doris.admin.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.alibaba.doris.admin.dao.SystemLogDao;
import com.alibaba.doris.admin.dataobject.SystemLogDO;

public class SystemLogDaoImpl extends SqlMapClientDaoSupport implements SystemLogDao {

    @SuppressWarnings("unchecked")
    public List<SystemLogDO> querySystemLogs(Map paramMap) {
        return (List<SystemLogDO>) getSqlMapClientTemplate().queryForList("SystemLog.listSystemLogs", paramMap);
    }

    public void insertSystemLogs(List<SystemLogDO> systemLogs) {
        for (SystemLogDO systemLogDo : systemLogs) {
            insertSystemLog(systemLogDo);
        }

    }

    public void insertSystemLog(SystemLogDO systemLog) {
        getSqlMapClientTemplate().insert("SystemLog.addSystemLog", systemLog);

    }

    @SuppressWarnings("unchecked")
    public int querySystemLogCounts(Map paramMap) {
        return (Integer) getSqlMapClientTemplate().queryForObject("SystemLog.queryAllCounts", paramMap);
    }

}

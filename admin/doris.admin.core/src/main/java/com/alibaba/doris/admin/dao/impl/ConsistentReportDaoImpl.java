package com.alibaba.doris.admin.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.alibaba.doris.admin.dao.ConsistentReportDao;
import com.alibaba.doris.admin.dataobject.ConsistentReportDO;

public class ConsistentReportDaoImpl extends SqlMapClientDaoSupport implements ConsistentReportDao {

    public Integer insert(ConsistentReportDO consistentReportDO) {
        return (Integer) getSqlMapClientTemplate().insert("CONSISTENT_REPORT.insert", consistentReportDO);
    }

    public Integer deleteByIds(List<Integer> idList) {
        return getSqlMapClientTemplate().delete("CONSISTENT_REPORT.deleteByIds", idList);
    }

    public List<ConsistentReportDO> queryConsistentReport(Map params) {
        return (List<ConsistentReportDO>) getSqlMapClientTemplate().queryForList("CONSISTENT_REPORT.queryConsistentReport",
                                                                                 params);
    }

    public int countConsistentReport(Map params) {
        return (Integer) getSqlMapClientTemplate().queryForObject("CONSISTENT_REPORT.countConsistentReport", params);
    }

    public Integer deleteByGmtCreate(String gmtCreateFrom, String gmtCreateTo) {
        Map<String, String> params = new HashMap<String, String>(2);
        params.put("gmtCreateFrom", gmtCreateFrom);
        params.put("gmtCreateTo", gmtCreateTo);
        return getSqlMapClientTemplate().delete("CONSISTENT_REPORT.deleteByGmtCreate", params);
    }

}

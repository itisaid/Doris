package com.alibaba.doris.admin.dao;

import java.util.List;
import java.util.Map;

import com.alibaba.doris.admin.dataobject.ConsistentReportDO;

public interface ConsistentReportDao {

    Integer insert(ConsistentReportDO consistentReportDO);

    List<ConsistentReportDO> queryConsistentReport(Map params);

    int countConsistentReport(Map params);

    Integer deleteByIds(List<Integer> ids);

    Integer deleteByGmtCreate(String gmtCreateFrom, String gmtCreateTo);

}

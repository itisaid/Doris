package com.alibaba.doris.admin.service.impl;

import java.util.List;
import java.util.Map;

import com.alibaba.doris.admin.dao.ConsistentReportDao;
import com.alibaba.doris.admin.dataobject.ConsistentReportDO;
import com.alibaba.doris.admin.service.ConsistentReportService;


public class ConsistentReportServiceImpl implements ConsistentReportService {

    private ConsistentReportDao consistentReportDao;

    /**
     * @param consistentReportDao the consistentReportDao to set
     */
    public void setConsistentReportDao(ConsistentReportDao consistentReportDao) {
        this.consistentReportDao = consistentReportDao;
    }

    public Integer saveConsistentReport(ConsistentReportDO consistentReportDO) {
        return consistentReportDao.insert(consistentReportDO);
    }

    public List<ConsistentReportDO> queryConsistentReport(Map params) {
        return consistentReportDao.queryConsistentReport(params);
    }

    public int countConsistentReport(Map params) {
        return consistentReportDao.countConsistentReport(params);
    }

    public Integer deleteByIds(List<Integer> ids) {
        return consistentReportDao.deleteByIds(ids);
    }

    public Integer deleteByGmtCreate(String gmtCreateFrom, String gmtCreateTo) {
        return consistentReportDao.deleteByGmtCreate(gmtCreateFrom, gmtCreateTo);
    }

}

package com.alibaba.doris.admin.service;

import java.util.List;

import com.alibaba.doris.admin.dataobject.PrefLogDO;
import com.alibaba.doris.admin.support.PrefQuery;
import com.alibaba.doris.admin.support.PrefStatObject;

/**
 * 监控日志
 * 
 * @author helios
 */
public interface MonitorService {

    /**
     * 只做记录不报警
     * 
     * @param message
     */
    void info(String subject, String detail);

    /**
     * 报警
     * 
     * @param message
     */
    void error(String subject, String detail);

    /**
     * 保存性能报告
     * 
     * @param reports
     */
    void savePrefReports(List<PrefLogDO> reports);

    /**
     * @param query
     * @return
     */
    List<PrefStatObject> statByQuery(PrefQuery query);

    /**
     * @param query
     * @return
     */
    List<PrefStatObject> statWithNameSpace(PrefQuery query);

    /**
     * @param query
     * @return
     */
    List<PrefStatObject> statWithPhysicalId(PrefQuery query);

    /**
     * 进行归档操作
     */
    void archiveWithTx(int hourBefore);

    /**
     * 将数据按小时进行汇总
     * 
     * @param hourBefore
     */
    void deletePrefLogArchive(int dayBefore);
}

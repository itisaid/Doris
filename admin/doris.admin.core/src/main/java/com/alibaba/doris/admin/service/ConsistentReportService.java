/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.doris.admin.service;

import java.util.List;
import java.util.Map;

import com.alibaba.doris.admin.dataobject.ConsistentReportDO;

/**
 * 类ConsistentReportService.java的实现描述：TODO 类实现描述
 * 
 * @author hongwei.zhaohw 2012-1-6
 */
public interface ConsistentReportService {

    Integer saveConsistentReport(ConsistentReportDO consistentReportDO);

    Integer deleteByIds(List<Integer> ids);

    List<ConsistentReportDO> queryConsistentReport(Map params);

    int countConsistentReport(Map params);

    Integer deleteByGmtCreate(String gmtCreateFrom, String gmtCreateTo);

}

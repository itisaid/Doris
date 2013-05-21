package com.alibaba.doris.admin.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.ConsistentReportDO;
import com.alibaba.doris.admin.service.ConsistentReportService;

public class ConsistentReportServiceImplTest {

    private static ConsistentReportService consistentReportService;
    
    

    @BeforeClass
    public static void setUp() throws Exception {
        //consistentReportService = AdminServiceLocator.getConsistentReportService();
    }

    @Test
    public void testSaveConsistentReport() {
//        assertNotNull(consistentReportService);
//        Integer id = insert();
//        assertTrue(id > 0);
    }

    private Integer insert() {
        ConsistentReportDO consistentReportDO = new ConsistentReportDO();
        consistentReportDO.setClientIp("127.0.0.1");
        consistentReportDO.setErrorType("read");
        consistentReportDO.setExceptionMsg("testcase");
        consistentReportDO.setKeyStr("testKey");
        consistentReportDO.setNamespaceId(1);
        consistentReportDO.setPhisicalNodeIps("10.0.0.1:8080;10.0.0.2:9090");
        consistentReportDO.setTimestamp(Long.toString(System.currentTimeMillis()));
        Integer id = consistentReportService.saveConsistentReport(consistentReportDO);
        return id;
    }

    @Test
    public void testQueryConsistentReport() {
//        Map params = new HashMap();
//        params.put("errorType", "read");
//        params.put("pageSize", 2);
//        params.put("startRow", 0);
//        List<ConsistentReportDO> reportList = consistentReportService.queryConsistentReport(params);
//        assertTrue(reportList != null);
//        assertEquals(reportList.size(), 1);
//
//        int totalRows = consistentReportService.countConsistentReport(params);
//        assertTrue(totalRows > 0);
//        System.out.println(totalRows);
    }

    public void testDeleteByIds() {
        List<Integer> ids = new ArrayList<Integer>();
        ids.add(insert());
        ids.add(insert());
        Integer deletedRows = consistentReportService.deleteByIds(ids);
        assertTrue(2 == deletedRows);
    }

}

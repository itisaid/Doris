package com.alibaba.doris.common.adminservice.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.ConsistentErrorType;
import com.alibaba.doris.common.adminservice.BaseAdminService;
import com.alibaba.doris.common.adminservice.ConsistentErrorReportService;
import com.alibaba.doris.common.util.IPAddressUtil;

public class ConsistentErrorReportServiceImpl extends BaseAdminService<Boolean> implements ConsistentErrorReportService {

    private static Log                          logger   = LogFactory.getLog(ConsistentErrorReportServiceImpl.class);

    private static ConsistentErrorReportService instance = new ConsistentErrorReportServiceImpl();

    private ConsistentErrorReportServiceImpl() {
    }

    public static ConsistentErrorReportService getInstance() {
        return instance;
    }

    public Boolean report(int namespaceId, String key, String phisicalIps, String exceptionMsg,
                          ConsistentErrorType errorType, String timestamp) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(AdminServiceConstants.CONSISTENT_KEY, key);
        paramMap.put(AdminServiceConstants.CONSISTENT_NAMESPACE_ID, String.valueOf(namespaceId));
        paramMap.put(AdminServiceConstants.CONSISTENT_PHISICAL_IPS, phisicalIps);
        paramMap.put(AdminServiceConstants.CONSISTENT_CLIENT_IP, IPAddressUtil.getIPAddress());
        paramMap.put(AdminServiceConstants.CONSISTENT_EXCEPTION_MSG, exceptionMsg);
        paramMap.put(AdminServiceConstants.CONSISTENT_TIMESTAMP, timestamp);
        paramMap.put(AdminServiceConstants.CONSISTENT_ERROR_TYPE, errorType.name());
        Boolean result = requestForce(paramMap);
        return result;
    }

    @Override
    public Boolean convert(String response) {
        return Boolean.valueOf(response);
    }

    @Override
    public String getActionName() {
        return AdminServiceConstants.CONSISTENT_REPORT_ACTION;
    }

}

package com.alibaba.doris.common.adminservice;

import com.alibaba.doris.common.adminservice.impl.CheckFailServiceImpl;
import com.alibaba.doris.common.adminservice.impl.CommonConfigServiceImpl;
import com.alibaba.doris.common.adminservice.impl.ConsistentErrorReportServiceImpl;
import com.alibaba.doris.common.adminservice.impl.MigrateReportServiceImpl;
import com.alibaba.doris.common.adminservice.impl.MonitorServiceImpl;
import com.alibaba.doris.common.adminservice.impl.NamespaceServiceImpl;
import com.alibaba.doris.common.adminservice.impl.PostMigrateReportServiceImpl;
import com.alibaba.doris.common.adminservice.impl.RouteConfigServiceImpl;
import com.alibaba.doris.common.adminservice.impl.StoreNodeServiceImpl;
import com.alibaba.doris.common.adminservice.impl.UserAuthServiceImpl;
import com.alibaba.doris.common.adminservice.impl.VirtualNumberServiceImpl;

public class AdminServiceFactory {

    public static RouterConfigService getRouterConfigService() {
        return RouteConfigServiceImpl.getInstance();
    }

    public static NamespaceService getNamespaceService() {
        return NamespaceServiceImpl.getInstance();
    }

    public static CommonConfigService getCommonConfigService() {
        return CommonConfigServiceImpl.getInstance();
    }

    public static CheckFailService getCheckFailService() {
        return CheckFailServiceImpl.getInstance();
    }

    public static MonitorService getMonitorService() {
        return MonitorServiceImpl.getInstance();
    }

    public static MigrateReportService getMigrateReportService() {
        return MigrateReportServiceImpl.getInstance();
    }

    public static StoreNodeService getStoreNodeService() {
        return StoreNodeServiceImpl.getInstance();
    }

    public static VirtualNumberService getVirtualNumberService() {
        return VirtualNumberServiceImpl.getInstance();
    }

    public static PostMigrateReportService getPostMigrateReportService() {
        return PostMigrateReportServiceImpl.getInstance();
    }
    
    public static UserAuthService getUserAuthService() {
        return UserAuthServiceImpl.getInstance();
    }
    
    public static ConsistentErrorReportService getConsistentErrorReportService() {
        return ConsistentErrorReportServiceImpl.getInstance();
    }
}

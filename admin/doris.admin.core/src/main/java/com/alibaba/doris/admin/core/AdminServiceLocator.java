package com.alibaba.doris.admin.core;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.doris.admin.service.AdminNodeService;
import com.alibaba.doris.admin.service.AdminService;
import com.alibaba.doris.admin.service.ConsistentReportService;
import com.alibaba.doris.admin.service.MonitorService;
import com.alibaba.doris.admin.service.NamespaceService;
import com.alibaba.doris.admin.service.NodeValidatorService;
import com.alibaba.doris.admin.service.PropertiesService;
import com.alibaba.doris.admin.service.RouteConfigService;
import com.alibaba.doris.admin.service.SystemLogService;
import com.alibaba.doris.admin.service.UserService;
import com.alibaba.doris.admin.service.VirtualNodeService;

public class AdminServiceLocator {

    private static ApplicationContext context;

    static {
        context = new ClassPathXmlApplicationContext("classpath:spring/doris_service_context.xml");
    }

    public static AdminService getAdminService() {
        return (AdminService) context.getBean("adminService");
    }

    public static AdminNodeService getAdminNodeService() {
        return (AdminNodeService) context.getBean("adminNodeService");
    }

    public static RouteConfigService getRouteConfigService() {
        return (RouteConfigService) context.getBean("routeConfigService");
    }

    public static NamespaceService getNamespaceService() {
        return (NamespaceService) context.getBean("namespaceService");
    }

    public static VirtualNodeService getVirtualNodeService() {

        return (VirtualNodeService) context.getBean("virtualNodeService");

    }

    public static UserService getUserService() {

        return (UserService) context.getBean("userService");

    }

    public static MonitorService getMonitorService() {
        return getService(MonitorService.class);
    }

    public static PropertiesService getPropertiesService() {

        return (PropertiesService) context.getBean("propertiesService");

    }

    public static NodeValidatorService getNodeValidatorService() {
        return (NodeValidatorService) context.getBean("nodeValidatorService");
    }

    public static SystemLogService getSystemLogService() {
        return (SystemLogService) context.getBean("systemLogService");
    }

    public static ConsistentReportService getConsistentReportService() {
        return (ConsistentReportService) context.getBean("consistentReportService");
    }

    /**
     * if not init use new ClassPathXmlApplicationContext( "classpath:/spring/doris_service_context.xml");
     * 
     * @param <T>
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> clazz) {

        String beanName = StringUtils.uncapitalize(clazz.getSimpleName());

        if (!beanName.endsWith("Service")) {
            return null;
        }

        return (T) context.getBean(beanName);
    }

}

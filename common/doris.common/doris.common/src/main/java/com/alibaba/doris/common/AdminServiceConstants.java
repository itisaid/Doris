package com.alibaba.doris.common;

/**
 * 请求admin service参数相关的key
 * 
 * @author frank
 */
public class AdminServiceConstants {

    public static final String ADMIN_SERVICE_ACTION_NAME              = "actionName";
    public static final String CONFIG_VERSION                         = "configVersion";

    public static final String NAMESPACE_CONFIG                       = "namespaceConfig";

    // 迁移后处理状态报告
    public static final String POST_MIGRATE_REPORT_ACTION             = "postMigrateReport";
    public static final String POST_MIGRATE_REPORT_NODE_PORT          = "nodePort";
    public static final String POST_MIGRATE_REPORT_SCHEDULE           = AdminServiceConstants.MIGRATE_REPORT_SCHEDULE;
    public static final String POST_MIGRATE_REPORT_STATUS             = AdminServiceConstants.MIGRATE_REPORT_STATUS;
    public static final String POST_MIGRATE_REPORT_MESSAGE            = AdminServiceConstants.MIGRATE_REPORT_MESSAGE;

    // 迁移状态报告 MigrateReportService
    public static final String MIGRATE_REPORT_ACTION                  = "migrateReport";
    public static final String MIGRATE_REPORT_SOURCE_NODE_PORT        = "sourceNodeId";
    public static final String MIGRATE_REPORT_TARGET_NODE_PHYSICAL_ID = "targetNodeId";
    public static final String MIGRATE_REPORT_SCHEDULE                = "schedule";
    public static final String MIGRATE_REPORT_STATUS                  = "status";
    public static final String MIGRATE_REPORT_MESSAGE                 = "message";

    // StoreNodeService
    public static final String STORE_NODE_ACTION                      = "storeNode";
    public static final String STORE_NODE_PHYSICAL_ID                 = "physicalId";

    // Route config
    public static final String ROUTE_CONFIG_ACTION                    = "routeConfig";

    // node check action
    public static final String NODE_CHECK_ACTION                      = "nodeCheck";

    // virtual node number action
    public static final String VIRTUAL_NUMBER_ACTION                  = "virtualNumber";

    // name space action
    public static final String NAME_SPACE_ACTION                      = "nameSpace";
    public static final String NAME_SPACE_NAME                        = "nameSapceName";

    // monitor report
    public static final String MONITOR_ACTION                         = "perfReport";
    public static final String MONITOR_REPORT_PREF_OBJECT             = "perfObject";

    // time unit: mills
    public static final long   NODE_DEFAULT_FOREVER_FAIL_TIME         = 7200000;

    // heart beat interval
    public static final long   NODE_DEFAULT_HEART_BEAT_INTERVAL       = 5000;

    // node reload internal
    public static final long   NODE_DEFAULT_RELOAD_INTERVAL           = 3000;

    // node reload internal
    public static final long   ROUTER_SCAN_DEFAULT_RELOAD_INTERVAL    = 3000;

    // Common config name which includes all configurations, for example route config, namespace cnofig, etc.
    public static final String COMMON_CONFIG_ACTION                   = "commonConfig";
    public static final String COMMON_CONFIG_ACTION_ITEMS             = "commonConfigActionItems";
    public static final String COMMON_CONFIG_VERSION_ITEMS            = "commonConfigVersionItems";

    public static final String ADMIN_SERVICE_ERROR                    = "NG";

    // time unit: mills
    public static final long   NODE_CHECK_TIMEOUT_DEFAULT             = 10000;
    public static final int    NODE_CHECK_RETRIES_DEFAULT             = 3;
    public static final String REMOTE_IP                              = "REMOTE_IP";
    public static final String REMOTE_PORT                            = "REMOTE_PORT";
    public static final String NODE_CHECK_THREAD_POOL_SIZE            = "5";

    public static final String USER_AUTH_ACTION                       = "userAuth";
    public static final String USER_AUTH_USER_NAME                    = "userName";
    public static final String USER_AUTH_PASSWORD                     = "password";
    
    // 一致性问题报告
    public static final String CONSISTENT_REPORT_ACTION               = "consistentReport";
    public static final String CONSISTENT_NAMESPACE_ID                = "namespaceId";
    public static final String CONSISTENT_PHISICAL_IPS                = "phisicalIps";
    public static final String CONSISTENT_CLIENT_IP                   = "clientIp";
    public static final String CONSISTENT_KEY                         = "key";
    public static final String CONSISTENT_EXCEPTION_MSG               = "exceptionMsg";
    public static final String CONSISTENT_ERROR_TYPE                  = "errorType";
    public static final String CONSISTENT_TIMESTAMP                   = "timestamp";
    
}

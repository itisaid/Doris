/**
 * Project: doris.admin.service.common-0.1.0-SNAPSHOT File Created at 2011-5-22 $Id$ Copyright 1999-2100 Alibaba.com
 * Corporation Limited. All rights reserved. This software is the confidential and proprietary information of Alibaba
 * Company. ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.doris.admin.service.main;

import com.alibaba.doris.admin.service.common.AdminServiceAction;
import com.alibaba.doris.admin.service.common.consistent.ConsistentReportAction;
import com.alibaba.doris.admin.service.common.migrate.AdminMigrateStatusReportAction;
import com.alibaba.doris.admin.service.common.migrate.AdminPostMigrateStatusReportAction;
import com.alibaba.doris.admin.service.common.namespace.AdminNameSpaceAction;
import com.alibaba.doris.admin.service.common.node.StoreNodeAction;
import com.alibaba.doris.admin.service.common.user.UserAction;
import com.alibaba.doris.admin.service.common.virtual.VirtualNumberAction;
import com.alibaba.doris.admin.service.config.service.impl.AdminRouteConfigAction;
import com.alibaba.doris.admin.service.failover.check.AdminNodeCheckAction;
import com.alibaba.doris.admin.service.monitor.PrefReportAction;
import com.alibaba.doris.common.AdminServiceConstants;

/**
 * TODO Comment of AdminServiceActionFactory
 * 
 * @author mian.hem
 */
public class AdminServiceActionFactory {

    public static AdminServiceAction getAdminServiceAction(String actionName) {
        if (AdminServiceConstants.ROUTE_CONFIG_ACTION.equals(actionName)) {
            return AdminRouteConfigAction.getInstance();
        } else if (AdminServiceConstants.NODE_CHECK_ACTION.equals(actionName)) {
            return AdminNodeCheckAction.getInstance();
        } else if (AdminServiceConstants.COMMON_CONFIG_ACTION.equals(actionName)) {
            return CommonConfigServiceAction.getInstance();
        } else if (AdminServiceConstants.MIGRATE_REPORT_ACTION.equals(actionName)) {
            return AdminMigrateStatusReportAction.getInstance();
        } else if (AdminServiceConstants.NAME_SPACE_ACTION.equals(actionName)) {
            return AdminNameSpaceAction.getInstance();
        } else if (AdminServiceConstants.VIRTUAL_NUMBER_ACTION.equals(actionName)) {
            return VirtualNumberAction.getInstance();
        } else if (AdminServiceConstants.STORE_NODE_ACTION.equals(actionName)) {
            return StoreNodeAction.getInstance();
        } else if (AdminServiceConstants.MONITOR_ACTION.equals(actionName)) {
            return PrefReportAction.getInstance();
        } else if (AdminServiceConstants.POST_MIGRATE_REPORT_ACTION.endsWith(actionName)) {
            return AdminPostMigrateStatusReportAction.getInsatance();
        } else if (AdminServiceConstants.USER_AUTH_ACTION.equals(actionName)) {
            return UserAction.getInstance();
        } else if (AdminServiceConstants.CONSISTENT_REPORT_ACTION.equals(actionName)) {
            return ConsistentReportAction.getInstance();
        }
        return null;
    }

}

/**
 * Project: doris.common-0.1.0-SNAPSHOT
 * 
 * File Created at 2011-7-28
 * $Id$
 * 
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.alibaba.doris.common;

/**
 * The constants is used for Dragoon warning. all the values should be
 * consistent with the value configured in dragoon system, please refer to
 * dragoon monitor rule settings. BE CAUTION to MODIFY THE CONSTENTS VALUE.
 * 
 * @author mian.hem
 */
public final class MonitorWarnConstants {

    // warning constants
    public static final String NODE_TEMP_FAILED                    = "Node Temp Failed";

    public static final String NODE_TEMP_FAILURE_RESOLVE_FAILED    = "TempFailure Resolve Failed";

    public static final String NODE_FOREVER_FAILED                 = "Node Forever Failed";

    public static final String NODE_FOREVER_FAILURE_RESOLVE_FAILED = "ForeverFailure Resolve Failed";

    public static final String RE_GEN_ROUTE_FAILED                 = "Generate Route Failed";
    
    public static final String ROUTE_NO_TEMP_NODES                 = "No Temp Nodes";

    public static final String FAIL_TO_SEND_MIGRATE_COMMAND        = "Fail to Send Migrate Command";

    public static final String FAIL_TO_SEND_CLEAN_DATA_COMMAND     = "Fail to Send Clean Data Command";

    public static final String FAIL_TO_SEND_MIGRATE_FINISH_COMMAND = "Fail to Send Migrate Finish Command";

    public static final String INCORRECT_MIGRATE_PROGRESS          = "Incorrect Migrate Progress";

    public static final String MIGRATE_REPORT_TIMEOUT              = "Migrate Report Timeout";

    // system log constants 
    public static final String NODE_TEMP_FAILURE_RESOLVED          = "Node Temp-Failure Resolved:";
    public static final String NODE_EXPANSION_MIGRATION_FINISHED   = "Node Expansion Migration Finished:";
    public static final String NODE_FOREVER_FAILURE_RESOLVED       = "Node Forever-Failure Resolved:";

    public static final String NODE_HEALTH_CHANGE_TO_NG            = "Node Health Status is CHANGED to NG:";
    public static final String NODE_HEALTH_CHANGE_TO_OK            = "Node Health Status is CHANGED to OK:";

}

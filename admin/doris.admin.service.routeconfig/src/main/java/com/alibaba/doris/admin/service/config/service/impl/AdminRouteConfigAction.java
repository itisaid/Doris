/**
 * Project: doris.admin.service.common-0.1.0-SNAPSHOT
 * 
 * File Created at 2011-5-22
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
package com.alibaba.doris.admin.service.config.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.dataobject.RouterConfigInstanceDO;
import com.alibaba.doris.admin.monitor.MonitorEnum;
import com.alibaba.doris.admin.monitor.SystemLogMonitor;
import com.alibaba.doris.admin.service.common.AdminServiceAction;
import com.alibaba.doris.admin.service.common.route.DorisConfigServiceException;
import com.alibaba.doris.admin.service.common.route.RouteConfigProcessor;
import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.MonitorWarnConstants;
import com.alibaba.doris.common.RouteConfigInstance;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * TODO Comment of AdminRouteConfigAction
 * 
 * @author mian.hem
 */
public class AdminRouteConfigAction implements AdminServiceAction {
    private static final Log              logger             = LogFactory
                                                                     .getLog(AdminRouteConfigAction.class);

    private static AdminRouteConfigAction instance           = new AdminRouteConfigAction();

    private RouteConfigProcessor          configProcessor    = RouteConfigProcessor.getInstance();

    private AdminRouteConfigAction() {
        super();
    }

    public static AdminRouteConfigAction getInstance() {
        return instance;
    }

    public String execute(Map<String, String> params) {

        String configVersion = params.get(AdminServiceConstants.CONFIG_VERSION);

        boolean noVersion = false;
        if (StringUtils.isEmpty(configVersion) || !StringUtils.isNumeric(configVersion)) {
            noVersion = true;
        }

        if (noVersion) {
            if (logger.isInfoEnabled()) {
                logger.info("there is no route config version specified. Using 0 as default.");
            }
            configVersion = "0";
        }

        Long version = Long.valueOf(configVersion);

        RouterConfigInstanceDO configInstanceDO = null;

        try {
            if (version == 0) {
                configProcessor.refresh();
            }
            configInstanceDO = configProcessor.getCurrentConfigInstanceDo();
        } catch (DorisConfigServiceException e) {
            logger.error("Cannot get valida route configuration", e);
            SystemLogMonitor.error(MonitorEnum.ROUTER, MonitorWarnConstants.RE_GEN_ROUTE_FAILED, e);
        }

        if (configInstanceDO != null && version == configInstanceDO.getId()) {
            //相等， 没有变更
            configInstanceDO = null;
        }

        if (configInstanceDO != null && version > configInstanceDO.getId()) {
            try {
                // 如果传进来的版本号比Admin （configProcessor#urrentConfigInstanceDo）中
                // 持有高， 那么刷数据库：load数据库中最新的路由配置
                configProcessor.refreshWithDbLatest();
                
                RouterConfigInstanceDO refreshedConfigInstanceDO = configProcessor
                        .getCurrentConfigInstanceDo();
                
                if (refreshedConfigInstanceDO.getId() > version) {
                    configInstanceDO = refreshedConfigInstanceDO;
                } else if (refreshedConfigInstanceDO.getId() == version){
                    //客户端已经拥有最新版本， 不需要更新路由
                    configInstanceDO = null;
                } else{
                    // 告警：[重要]客户端传进来的版本号比数据库中最新的还要大，返回admin中的版本
                    configInstanceDO = refreshedConfigInstanceDO;

                    logger.error("Client route version is greater than the version of admin and db; client version:"
                            + version + ", admin version:" + configInstanceDO.getId());
                    SystemLogMonitor.error(MonitorEnum.ROUTER,
                            "Clietn route version is greater than admin");
                }
            } catch (DorisConfigServiceException e) {
                logger.error("Cannot get valida route configuration", e);
                SystemLogMonitor.error(MonitorEnum.ROUTER,
                        MonitorWarnConstants.RE_GEN_ROUTE_FAILED, e);
            }
        }

        RouteConfigInstance routeConfigInstance = null;
        if (configInstanceDO != null) {
            routeConfigInstance = new RouteConfigInstance();
            routeConfigInstance.setVersion(configInstanceDO.getId());
            List<StoreNode> storeNodes = JSON.parseArray(configInstanceDO.getContent(),
                    StoreNode.class);
            routeConfigInstance.setStoreNodes(storeNodes);
        }

        return JSON.toJSONString(routeConfigInstance, SerializerFeature.WriteEnumUsingToString);
    }
}

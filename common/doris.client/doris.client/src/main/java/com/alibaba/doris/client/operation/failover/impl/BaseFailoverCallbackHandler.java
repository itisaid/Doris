/**
 * Project: doris.client-0.1.0-SNAPSHOT
 * 
 * File Created at 2011-7-13
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
package com.alibaba.doris.client.operation.failover.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.client.AccessException;
import com.alibaba.doris.client.net.DataSource;
import com.alibaba.doris.client.operation.failover.PeerCallback;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.adminservice.AdminServiceFactory;
import com.alibaba.doris.common.route.DorisRouterException;

/**
 * TODO Comment of BaseFailoverCallbackHandler
 * @author luyi.huangly
 *
 */
public class BaseFailoverCallbackHandler extends AbstractFailoverCallbackHandler{
    private Log        log = LogFactory.getLog(BaseFailoverCallbackHandler.class);
    protected void processAccessException(int i, PeerCallback peerCallback) throws AccessException {

//        if (log.isWarnEnabled()) {
//            log.warn("error occur in executing " + this.getOperationData().getOperation().getName() + " in "
//                     + peerCallback.getDataSource().getSequence() + "." + peerCallback.getDataSource().getNo()
//                     + " for " + this.getOperationData().getKey() + " " + i + "times");
//        }
        if (i % 3 == 0) {
            StoreNode node = this.getDataSourceManager().getDataSourceRouter().getStoreNodeOf(
                                                                                              peerCallback.getDataSource());
            if (!AdminServiceFactory.getCheckFailService().checkFailNode(node)) {
                try {
                    StoreNode failoverNode = this.getDataSourceManager().getDataSourceRouter().getRouteStrategy().findFailoverNode(peerCallback.getOperationData().getOperation().getOperationType(peerCallback.getOperationData()),
                                                                                                                                   peerCallback.getOperationData().getNamespace().getCopyCount(),
                                                                                                                                   peerCallback.getOperationData().getKey().getPhysicalKey(),
                                                                                                                                   node);
                    log.error("admin check it was failed."+node);
                    // switch datasource
                    DataSource ds = this.getDataSourceManager().getDataSourceRouter().getDataSourceOf(failoverNode);
                    peerCallback.setDataSource(ds);
                    if (log.isInfoEnabled()) {
                        log.info( "fail over " + node + " to " + failoverNode);
                    }
                } catch (DorisRouterException e1) {
                    if (log.isErrorEnabled()) {
                        log.error("Route error:" + e1);
                    }
                    throw new AccessException(e1);
                }
            }

        }
        // data server返回路由版本异常，从异常中获取路由表，更新本地路由，并根据新路由、新版本重新请求

    }
}

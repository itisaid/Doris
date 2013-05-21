/*
 * Copyright(C) 1999-2010 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client.operation.failover.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.client.AccessException;
import com.alibaba.doris.client.DataSourceManager;
import com.alibaba.doris.client.net.DataSource;
import com.alibaba.doris.client.net.NetException;
import com.alibaba.doris.client.net.exception.ClientConnectionException;
import com.alibaba.doris.client.net.exception.RouteVersionOutOfDateException;
import com.alibaba.doris.client.operation.DataSourceOpFuture;
import com.alibaba.doris.client.operation.OperationDataSourceGroup;
import com.alibaba.doris.client.operation.failover.LogicCallback;
import com.alibaba.doris.client.operation.failover.LogicCallbackHandler;
import com.alibaba.doris.client.operation.failover.PeerCallback;
import com.alibaba.doris.client.operation.result.DataSourceOpResult;
import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.route.DorisRouterException;

/**
 * PeerFailoverCallbackHandler
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-25
 */
public class LogicFailoverCallbackHandler extends BaseFailoverCallbackHandler implements LogicCallbackHandler {

    private Log log = LogFactory.getLog(LogicFailoverCallbackHandler.class);

    @Override
    public void setDataSourceManager(DataSourceManager dataSourceManager) {
        super.setDataSourceManager(dataSourceManager);
        this.timeoutOfOperation = dataSourceManager.getConfigManager().getClientConfiguration().getTimeoutOfOperation();
    }

    /**
     * @see com.alibaba.doris.CallbackHandler.operation.failover.FailoverHandler#doFailover(com.alibaba.doris.Callback.operation.failover.FailoverCallback)
     */
    public List<List<DataSourceOpResult>> doLogicExecute(List<LogicCallback> callbacks,
                                                         List<OperationDataSourceGroup> operationDataSourceGroups)
                                                                                                                  throws AccessException {

        List<List<DataSourceOpFuture>> futuresList = new ArrayList<List<DataSourceOpFuture>>();
        for (LogicCallback callback : callbacks) {
            callback.execute();
            List<DataSourceOpFuture> dataSourceOpFutures = callback.getDataSourceOpFutures();
            futuresList.add(dataSourceOpFutures);
        }

        List<List<DataSourceOpResult>> dataSourceOpResultsList = new ArrayList<List<DataSourceOpResult>>();
        int size = futuresList.size();
        for (int k = 0; k < size; k++) {
            List<DataSourceOpFuture> dataSourceOpFutures = futuresList.get(k);
            OperationDataSourceGroup group = operationDataSourceGroups.get(k);
            List<DataSourceOpResult> dataSourceOpResults = new ArrayList<DataSourceOpResult>();
            for (DataSourceOpFuture dsOpfuture : dataSourceOpFutures) {
                boolean failed = true;
                // try 5 times for one node, try 3 nodes total
                PeerCallback peerCallback = dsOpfuture.getPeerCallback();
                DataSource dataSource = peerCallback.getDataSource();
                for (int i = 1; i <= 5; i++) {
                    try {
                        Object value = peerCallback.getOperationFuture().get(timeoutOfOperation, TimeUnit.MILLISECONDS);

                        DataSourceOpResult dataSourceOpResult = new DataSourceOpResult();
                        dataSourceOpResult.setDataSource(dataSource);
                        dataSourceOpResult.setResult(value);

                        dataSourceOpResults.add(dataSourceOpResult);
                        failed = false;

                        break;
                    } catch (RouteVersionOutOfDateException routeException) {
                        proceeRouteException(routeException, peerCallback, group);
                    } catch (ClientConnectionException cce) {
                        processClientConnectionException(peerCallback);
                    } catch (InterruptedException e) {
                        throw new AccessException(e);
                    } catch (ExecutionException e) {
                        throw new AccessException(e);
                    } catch (TimeoutException e) {
                        processClientConnectionException(peerCallback);
                    }
                    sleep(i);
                }
                
                if (failed) {
                    throw new AccessException("LogicFailoverCallbackHandler could not process.");
                }
            }
            dataSourceOpResultsList.add(dataSourceOpResults);
        }

        return dataSourceOpResultsList;
    }
    
    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void proceeRouteException(RouteVersionOutOfDateException routeException, PeerCallback peerCallback,
                                      OperationDataSourceGroup group) throws AccessException {

        // 从异常中获取路由表，更新本地路由
        String routeConfig = routeException.getNewRouteTable();
        if (log.isWarnEnabled()) {
            log.warn("Process RouteVersionOutOfDateException: currentDataSource:"
                     + peerCallback.getDataSource().getSequence() + "." + peerCallback.getDataSource().getNo()
                     + "get new route config from data server:::" + routeConfig);
        }
        Map<String, String> configs = new HashMap<String, String>();
        configs.put(AdminServiceConstants.ROUTE_CONFIG_ACTION, routeConfig);
        this.getDataSourceManager().getConfigManager().refreshConfig(configs);

        // 将新版本写入请求
        long routeVersion = this.getDataSourceManager().getDataSourceRouter().getRouteTableConfiger().getRouteTable().getVersion();
        Key phKey = (Key) peerCallback.getOperationData().getKey();
        phKey.setRouteVersion(routeVersion);

        if (log.isWarnEnabled()) {
            log.warn("Get new route version:" + routeVersion);
        }
        // 根据新路由重新构造OperationDataSourceGroup的datasource
        try {
            // 获取新路由，重构OperationDataSourceGroup
            String physicalKey = peerCallback.getOperationData().getKey().getPhysicalKey();

            List<StoreNode> nodes = this.getDataSourceManager().getDataSourceRouter().getRouteStrategy().findNodes(peerCallback.getOperationData().getOperation().getOperationType(peerCallback.getOperationData()),
                                                                                                                   peerCallback.getOperationData().getNamespace().getCopyCount(),
                                                                                                                   physicalKey);

            if (log.isInfoEnabled()) {
                log.info("find new route by key:" + physicalKey);
            }

            if (log.isInfoEnabled()) {
                log.info("new route nodes - " + nodes);
            }

            List<DataSource> oldDsList = group.getDataSources();
            if (log.isInfoEnabled()) {
                log.info("old route datasource:" + oldDsList);
            }

            for (int x = 0; x < oldDsList.size(); x++) {// 遍历旧路由数据源List
                if (oldDsList.get(x).equals(peerCallback.getDataSource())) {// 找到当前数据源在旧数据源List中的下标
                    StoreNode oldSn = this.getDataSourceManager().getDataSourceRouter().getStoreNodeOf(
                                                                                                       peerCallback.getDataSource());// 找到旧数据源对应节点
                    StoreNode newSn = nodes.get(x);// 找到新节点对应节点，新旧节点在路由List中的下标是一样的

                    // 新旧路由节点不一样
                    if (!newSn.getPhId().equals(oldSn.getPhId())) {
                        // !newSn.getPhId().equals(oldSn.getPhId()) || !newSn.getSequence().equals(oldSn.getPhId())) {
                        DataSource ds = this.getDataSourceManager().getDataSourceRouter().getDataSourceOf(newSn);

                        if (log.isWarnEnabled()) {
                            log.warn("New store node != Old store node, new one is " + newSn + "," + newSn.getPhId()
                                     + ". key=" + physicalKey);
                        }
                        peerCallback.setDataSource(ds);// 使用新节点的数据源为当前数据源，准备重试
                    } else {
                        if (log.isWarnEnabled()) {
                            log.warn("New store node == Old store node, new one is also " + newSn + ","
                                     + newSn.getPhId() + ". key=" + physicalKey);
                        }
                    }
                }
            }
            if (log.isWarnEnabled()) {
                log.warn("Redirect access after route version exception:" + peerCallback.getDataSource());
            }
            processCallbackExecute(peerCallback);

        } catch (DorisRouterException e1) {
            if (log.isErrorEnabled()) {
                log.error("Route error:" + e1);
            }
            throw new AccessException(e1);
        }

    }

    private void processClientConnectionException(PeerCallback peerCallback) throws AccessException {

        // processAccessException(i, peerCallback);
        processCallbackExecute(peerCallback);
    }

    private void processCallbackExecute(PeerCallback peerCallback) throws AccessException {
        boolean failed = true;
        for (int j = 1; j <= 10; j++) {
            try {
                peerCallback.execute();
                failed = false;
                break;
            } catch (AccessException e) {
                if (e.getCause() instanceof NetException) {
                    processAccessException(j, peerCallback);
                } else {
                    throw e;
                }
            }
        }
        if (failed) {
            throw new AccessException("Coundn't connect data server:" + peerCallback.getDataSource());
        }
    }

    private long timeoutOfOperation;
}

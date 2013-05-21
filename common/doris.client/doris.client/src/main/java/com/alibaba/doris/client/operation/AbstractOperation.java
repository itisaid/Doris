/*
 * Copyright(C) 1999-2010 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client.operation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.client.AccessException;
import com.alibaba.doris.client.DataSourceManager;
import com.alibaba.doris.client.DataSourceRouter;
import com.alibaba.doris.client.DorisClientException;
import com.alibaba.doris.client.cn.OperationDataConverter;
import com.alibaba.doris.client.net.DataSource;
import com.alibaba.doris.client.operation.failover.CallbackHandlerFactory;
import com.alibaba.doris.client.operation.failover.LogicCallback;
import com.alibaba.doris.client.operation.failover.LogicCallbackHandler;
import com.alibaba.doris.client.operation.failover.PeerCallback;
import com.alibaba.doris.client.operation.failover.PeerCallbackHandler;
import com.alibaba.doris.client.operation.failover.impl.DefaultLogicCallback;
import com.alibaba.doris.client.operation.result.DataSourceOpResult;
import com.alibaba.doris.common.ConsistentErrorType;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.adminservice.AdminServiceFactory;
import com.alibaba.doris.common.configer.RouteTableConfiger;
import com.alibaba.doris.common.data.CompareStatus;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.route.DorisRouterException;
import com.alibaba.doris.common.route.RouteTable;
import com.alibaba.doris.common.route.VirtualRouter;

/**
 * Operation
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-22
 */
public abstract class AbstractOperation implements Operation {

    private Log                      logger = LogFactory.getLog(AbstractOperation.class);

    protected DataSourceManager      dataSourceManager;

    protected CallbackHandlerFactory callbackHandlerFactory;

    protected OperationDataConverter operationDataConverter;

    public abstract String getName();

    public void setDataSourceManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    public DataSourceManager getDataSourceManager() {
        return dataSourceManager;
    }

    public void setFailoverHandlerFactory(CallbackHandlerFactory callbackHandlerFactory) {
        this.callbackHandlerFactory = callbackHandlerFactory;
    }

    public void setOperationDataConverter(OperationDataConverter operationDataConverter) {
        this.operationDataConverter = operationDataConverter;
    }

    public List<List<DataSourceOpResult>> doLogicExecute(List<OperationDataSourceGroup> operationDataSourceGroups,
                                                         List<OperationData> operationDatas) throws AccessException {
        if (operationDataSourceGroups.isEmpty() || operationDatas.isEmpty()) {
            throw new AccessException("operationDataSourceGroups or operationDatas is empty");
        }
        int size = operationDataSourceGroups.size();

        List<LogicCallback> logicCallbacks = new ArrayList<LogicCallback>(size);
        LogicCallbackHandler callbackHandler = callbackHandlerFactory.getLogicFailoverHandler();
        callbackHandler.setDataSourceManager(dataSourceManager);

        for (int i = 0; i < size; i++) {
            // callbackHandler.setOperationData(operationDatas.get(i));
            final OperationDataSourceGroup group = operationDataSourceGroups.get(i);
            // callbackHandler.setOperationDataSourceGroup(group);

            LogicCallback logicCallback = new DefaultLogicCallback(operationDatas.get(i)) {

                @Override
                public LogicCallback execute() throws AccessException {
                    List<DataSourceOpFuture> dsFutureList = new ArrayList<DataSourceOpFuture>(
                                                                                              group.getDataSources().size());

                    for (final DataSource dataSource : group.getDataSources()) {
                        // distributed computing logic here.
                        PeerCallback peerCallback = doPeerExecute(dataSource, operationData);
                        // OperationFuture<?> future = peerCallback.getOperationFuture();

                        DataSourceOpFuture dsopFuture = new DataSourceOpFuture(dataSource, peerCallback);
                        dsFutureList.add(dsopFuture);
                    }
                    this.dataSourceOpFutures = dsFutureList;
                    return this;
                }
            };
            logicCallbacks.add(logicCallback);

        }
        List<List<DataSourceOpResult>> dataSourceOpResults = callbackHandler.doLogicExecute(logicCallbacks,
                                                                                            operationDataSourceGroups);
        // resultList.add(dataSourceOpResults);
        return dataSourceOpResults;
    }

    /**
     * Operation on single node datasource.
     * 
     * @param dataSource
     * @param operationData
     * @throws AccessException
     */
    public PeerCallback doPeerExecute(final DataSource dataSource, final OperationData operationData)
                                                                                                     throws AccessException {

        final PeerCallbackHandler callbackHandler = callbackHandlerFactory.getPeerFailoverHandler();
        callbackHandler.setDataSourceManager(dataSourceManager);
        callbackHandler.setOperationData(operationData);
        callbackHandler.setDataSource(dataSource);

        PeerCallback callback = generatePeerCallback(dataSource, operationData);

        return callbackHandler.doPeerExecute(callback);
    }

    protected abstract PeerCallback generatePeerCallback(final DataSource dataSource, final OperationData operationData);

    public void execute(OperationData operationData) {

        List<Object> args = operationData.getArgs();
        if (args.get(0) == null) {
            throw new IllegalArgumentException("Argument 0 ( key) can't be null");
        }

        DataSourceRouter dataSourceRouter = dataSourceManager.getDataSourceRouter();
        RouteTableConfiger routeTableConfiger = dataSourceRouter.getRouteTableConfiger();
        RouteTable routeTable = routeTableConfiger.getRouteTable();

        if (routeTable == null) {
            throw new DorisClientException("Can't get RouteTable or connect to AdminServer.");
        }
        long routeVersion = routeTable.getVersion();

        VirtualRouter virtualRouter = dataSourceManager.getDataSourceRouter().getVirtualRouter();
        operationDataConverter.setVirtualRouter(virtualRouter);

        List<OperationDataSourceGroup> operationDataSourceGroups = null;
        try {

            operationDataSourceGroups = new ArrayList<OperationDataSourceGroup>();
            List<OperationData> operationDatas = new ArrayList<OperationData>();

            buildLogicParam(operationDataSourceGroups, operationDatas, operationData, operationDataConverter,
                            routeVersion);

            List<List<DataSourceOpResult>> dsOpResults = doLogicExecute(operationDataSourceGroups, operationDatas);

            mergeOperationResult(dsOpResults, operationDatas);

            if (operationDatas.size() > 1) {
                List<Object> rl = new ArrayList<Object>(operationDatas.size());
                for (OperationData od : operationDatas) {
                    rl.add(od.getResult());
                }
                operationData.setResult(rl);
            }

        } catch (AccessException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            reportConsisitentError(operationData, operationDataSourceGroups, e.getMessage());
            throw new DorisClientException(e);
        } catch (DorisRouterException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            reportConsisitentError(operationData, operationDataSourceGroups, e.getMessage());
            throw new DorisClientException(e);
        }
    }

    protected void reportConsisitentError(OperationData operationData,
                                          List<OperationDataSourceGroup> operationDataSourceGroups, String exceptionMsg) {
    }

    protected void doReportConsistentError(OperationData operationData,
                                           List<OperationDataSourceGroup> operationDataSourceGroups, String exceptionMsg) {
        for (OperationDataSourceGroup dataSourceGroup : operationDataSourceGroups) {
            StringBuilder phisicalIpsBuilder = new StringBuilder();
            List<StoreNode> storeNodes = dataSourceGroup.getNodes();
            for (StoreNode storeNode : storeNodes) {
                String ip = storeNode.getIp();
                int port = storeNode.getPort();
                phisicalIpsBuilder.append(ip).append(":").append(port).append(";");
            }

            String timestampStr = "";
            Value v = (Value) operationData.getArgs().get(1);
            if (v != null) {
                timestampStr = Long.toString(v.getTimestamp());
            }
            AdminServiceFactory.getConsistentErrorReportService().report(operationData.getNamespace().getId(),
                                                                         operationData.getKey().getKey(),
                                                                         phisicalIpsBuilder.toString(), exceptionMsg,
                                                                         ConsistentErrorType.write, timestampStr);
        }
    }

    protected abstract void buildLogicParam(List<OperationDataSourceGroup> operationDataSourceGroups,
                                            List<OperationData> operationDatas, OperationData operationData,
                                            OperationDataConverter operationDataConverter, long routeVersion)
                                                                                                             throws DorisRouterException;

    protected void buildSimpleLogicParam(List<OperationDataSourceGroup> operationDataSourceGroups,
                                         List<OperationData> operationDatas, OperationData operationData,
                                         OperationDataConverter operationDataConverter, long routeVersion)
                                                                                                          throws DorisRouterException {
        int opCount = getOperationCount(operationData);
        Key phKey = operationDataConverter.buildKey(operationData, routeVersion);
        operationData.setKey(phKey);
        OperationDataSourceGroup operationDataSourceGroup = dataSourceManager.getOperationDataSourceGroup(getOperationType(operationData),
                                                                                                          opCount,
                                                                                                          phKey.getPhysicalKey());

        operationDataSourceGroups.add(operationDataSourceGroup);
        operationDatas.add(operationData);
    }

    /**
     * 合并结果
     * 
     * @param dsOpResults
     * @param operationData
     */
    protected void mergeOperationResult(List<List<DataSourceOpResult>> dsOpResults, List<OperationData> operationDatas) {

        int size = dsOpResults.size();
        for (int i = 0; i < size; i++) {
            for (DataSourceOpResult dataSourceOpResult : dsOpResults.get(i)) {
                if (dataSourceOpResult.getResult() != null) {
                    operationDatas.get(i).setResult(dataSourceOpResult.getResult());
                    break;
                }
            }

        }
    }

    /**
     * 多读场景下，合并put，puts，delete操作结果<br/>
     * put操作需要比较所有节点，只要有一个节点put成功即返回成功。<br/>
     * 为防止put时一个node成功一个node失败，如果返回失败，但是get的时候却能成功返回结果。
     * 
     * @param dsOpResults
     * @param operationData
     */
    protected void needOneNodeSuccess(List<List<DataSourceOpResult>> dsOpResults, List<OperationData> operationDatas) {

        int size = dsOpResults.size();
        for (int i = 0; i < size; i++) {
            boolean success = false;

            for (DataSourceOpResult dataSourceOpResult : dsOpResults.get(i)) {
                if (dataSourceOpResult.getResult() != null && (Boolean) dataSourceOpResult.getResult()) {
                    success = true;
                    break;
                }
            }

            if (success) {
                operationDatas.get(i).setResult(Boolean.TRUE);
            } else {
                operationDatas.get(i).setResult(Boolean.FALSE);
            }

        }
    }

    /**
     * 单读场景下合并put，puts，delete操作结果<br/>
     * put操作需要比较所有节点，只有所有你哦的都操作成功才返回成功。<br/>
     * 为防止put时一个node成功一个node失败，如果返回成功，但是get的时候恰好路由到put操作失败的节点上，就会有问题。
     * 
     * @param dsOpResults
     * @param operationData
     */
    protected void needAllNodeSuccess(List<List<DataSourceOpResult>> dsOpResults, List<OperationData> operationDatas) {
        int size = dsOpResults.size();
        for (int i = 0; i < size; i++) {
            boolean success = true;

            for (DataSourceOpResult dataSourceOpResult : dsOpResults.get(i)) {
                if (dataSourceOpResult.getResult() == null
                    || !((Boolean) dataSourceOpResult.getResult()).booleanValue()) {
                    success = false;
                    break;
                }
            }

            if (success) {
                operationDatas.get(i).setResult(Boolean.TRUE);
            } else {
                operationDatas.get(i).setResult(Boolean.FALSE);
            }

        }
    }

    /**
     * 多读场景下，比较get结果，并用最新的value去更新过时节点数据
     * 
     * @param dsOpResults
     * @param operationDatas
     */
    protected void compareAndUpdateResult(List<List<DataSourceOpResult>> dsOpResults, List<OperationData> operationDatas) {

        int size = dsOpResults.size();
        for (int i = 0; i < size; i++) {

            List<DataSourceOpResult> latestResults = new ArrayList<DataSourceOpResult>(dsOpResults.get(i).size());
            List<DataSourceOpResult> overdueResults = new ArrayList<DataSourceOpResult>(dsOpResults.get(i).size());

            // 比较所有节点的返回值
            for (DataSourceOpResult dataSourceOpResult : dsOpResults.get(i)) {
                if (latestResults.isEmpty()) {
                    latestResults.add(dataSourceOpResult);
                } else {
                    Value value = (Value) dataSourceOpResult.getResult();
                    CompareStatus compareVersion = value.compareVersion((Value) latestResults.get(0).getResult());
                    if (CompareStatus.AFTER.equals(compareVersion)) {
                        overdueResults.addAll(latestResults);
                        latestResults.clear();
                        latestResults.add(dataSourceOpResult);
                    } else if (CompareStatus.BEFORE.equals(compareVersion)) {
                        overdueResults.add(dataSourceOpResult);
                    } else {
                        latestResults.add(dataSourceOpResult);
                    }
                }
            }

            // 设置结果为最近更新的value
            Value latestValue = (Value) latestResults.get(0).getResult();
            operationDatas.get(i).setResult(latestValue);

            if (overdueResults.size() > 0) {
                Key key = operationDatas.get(i).getKey();

                // 更新过期数据节点为最近的value
                try {
                    for (DataSourceOpResult overdule : overdueResults) {
                        overdule.getDataSource().getConnection().cas(key, latestValue);
                    }

                } catch (Exception e) {
                    logger.error("failed to fix key:" + key.getKey() + ", with latest value:" + latestValue, e);
                } finally {

                    StringBuilder phisicalIpsBuilder = new StringBuilder();
                    for (DataSourceOpResult dataSourceOpResult : dsOpResults.get(i)) {
                        String ip = dataSourceOpResult.getDataSource().getIp();
                        int port = dataSourceOpResult.getDataSource().getPort();
                        phisicalIpsBuilder.append(ip).append(":").append(port).append(";");

                    }
                    // send report to admin
                    AdminServiceFactory.getConsistentErrorReportService().report(operationDatas.get(i).getNamespace().getId(),
                                                                                 key.getKey(),
                                                                                 phisicalIpsBuilder.toString(),
                                                                                 "read unconsistent data",
                                                                                 ConsistentErrorType.read,
                                                                                 Long.toString(latestValue.getTimestamp()));
                }

            }

        }
    }

    public int getOperationCount(OperationData operationData) {
        return operationData.getNamespace().getCopyCount();
    }
}

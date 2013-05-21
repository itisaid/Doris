/*
 * Copyright(C) 1999-2010 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client.operation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.client.AccessException;
import com.alibaba.doris.client.cn.OperationDataConverter;
import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.DataSource;
import com.alibaba.doris.client.net.NetException;
import com.alibaba.doris.client.operation.AbstractOperation;
import com.alibaba.doris.client.operation.Operation;
import com.alibaba.doris.client.operation.OperationData;
import com.alibaba.doris.client.operation.OperationDataSourceGroup;
import com.alibaba.doris.client.operation.failover.PeerCallback;
import com.alibaba.doris.client.operation.failover.impl.DefaultPeerCallback;
import com.alibaba.doris.client.operation.result.DataSourceOpResult;
import com.alibaba.doris.common.Namespace;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.operation.OperationEnum;
import com.alibaba.doris.common.route.DorisRouterException;

/**
 * PutOperation
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-22
 */
public class PutsOperation extends AbstractOperation {

    private static final Logger logger = LoggerFactory.getLogger(PutsOperation.class);

    /**
     * @see com.alibaba.doris.framework.operation.AbstractOperation#getName()
     */
    public String getName() {
        return "puts";
    }

    public OperationEnum getOperationType(OperationData operationData) {
        return OperationEnum.WRITE;
    }

    @Override
    public int getOperationCount(OperationData operationData) {
        return operationData.getNamespace().getCopyCount();
    }

    @Override
    protected PeerCallback generatePeerCallback(DataSource dataSource, OperationData operationData) {
        return new DefaultPeerCallback(dataSource, operationData) {

            @Override
            public PeerCallback execute() throws AccessException {
                try {
                    Connection connection = dataSource.getConnection();

                    Key commuKey = (Key) operationData.getKey();
                    Value commuValue = (Value) operationData.getArgs().get(1);

                    this.future = connection.put(commuKey, commuValue);
                } catch (NetException e) {
                    throw new AccessException("put connection exception.", e);
                } catch (Throwable t) {
                    throw new AccessException("put operation exception." + t, t);
                }
                return this;
            }
        };
    }

    @Override
    protected void buildLogicParam(List<OperationDataSourceGroup> operationDataSourceGroups,
                                   List<OperationData> operationDatas, OperationData operationData,
                                   OperationDataConverter operationDataConverter, long routeVersion)
                                                                                                    throws DorisRouterException {
        int opCount = getOperationCount(operationData);

        Map<Object, Object> kvs = (Map<Object, Object>) operationData.getArgs().get(0);
        Operation operation = operationData.getOperation();
        Namespace namespace = operationData.getNamespace();
        for (Entry<Object, Object> e : kvs.entrySet()) {
            List<Object> args = new ArrayList<Object>(1);
            args.add(e.getKey());
            args.add(e.getValue());
            OperationData tempOperationData = new OperationData(operation, namespace, args);
            Key phKey = operationDataConverter.buildKey(tempOperationData, routeVersion);
            tempOperationData.setKey(phKey);
            OperationDataSourceGroup operationDataSourceGroup = dataSourceManager.getOperationDataSourceGroup(tempOperationData.getOperation().getOperationType(operationData),
                                                                                                              opCount,
                                                                                                              phKey.getPhysicalKey());

            operationDataSourceGroups.add(operationDataSourceGroup);
            operationDatas.add(tempOperationData);
        }

    }

    @Override
    protected void reportConsisitentError(OperationData operationData,
                                          List<OperationDataSourceGroup> operationDataSourceGroups, String exceptionMsg) {
        super.doReportConsistentError(operationData, operationDataSourceGroups, exceptionMsg);
    }

    @Override
    protected void mergeOperationResult(List<List<DataSourceOpResult>> dsOpResults, List<OperationData> operationDatas) {
        if (operationDatas.get(0).getNamespace().isMultiRead()) {
            super.needOneNodeSuccess(dsOpResults, operationDatas);
        } else {
            super.needAllNodeSuccess(dsOpResults, operationDatas);
        }
    }
}

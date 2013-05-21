/*
 * Copyright(C) 1999-2010 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client.operation.failover.impl;

import com.alibaba.doris.client.AccessException;
import com.alibaba.doris.client.net.DataSource;
import com.alibaba.doris.client.net.OperationFuture;
import com.alibaba.doris.client.operation.ExecutionStatus;
import com.alibaba.doris.client.operation.OperationData;
import com.alibaba.doris.client.operation.failover.PeerCallback;

/**
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-7-11
 */
public class DefaultPeerCallback implements PeerCallback {

    protected ExecutionStatus    executionStatus = new ExecutionStatus();
    protected OperationFuture<?> future;
    protected OperationData      operationData;
    protected DataSource         dataSource;

    public DefaultPeerCallback() {
    }
    
    public DefaultPeerCallback(DataSource  dataSource,OperationData operationData) {
        this.dataSource = dataSource;
        this.operationData = operationData;
    }
    
    public DefaultPeerCallback(OperationData operationData) {
        this.operationData = operationData;
    }

    public OperationData getOperationData() {
        return operationData;
    }

    public ExecutionStatus getExecutionStatus() {
        return executionStatus;
    }

    public OperationFuture<?> getOperationFuture() {
        return future;
    }

    public void setOperationData(OperationData operationData) {
        this.operationData = operationData;
    }

    public PeerCallback execute() throws AccessException {
        return null;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;

    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

}

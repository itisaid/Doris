/*
 * Copyright(C) 1999-2010 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client.operation.failover;

import java.util.List;

import com.alibaba.doris.client.AccessException;
import com.alibaba.doris.client.DataSourceManager;
import com.alibaba.doris.client.operation.OperationData;
import com.alibaba.doris.client.operation.OperationDataSourceGroup;
import com.alibaba.doris.client.operation.result.DataSourceOpResult;

/**
 * CallbackHandler
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-25
 */
public interface LogicCallbackHandler {

    public void setDataSourceManager(DataSourceManager dataSourceManager);

    //
    // public void setOperationData(OperationData operationData);
    //
    // public void setOperationDataSourceGroup(OperationDataSourceGroup group);

    public List<List<DataSourceOpResult>> doLogicExecute(List<LogicCallback> callbacks,
                                                         List<OperationDataSourceGroup> operationDataSourceGroups)
                                                                                                                  throws AccessException;

}

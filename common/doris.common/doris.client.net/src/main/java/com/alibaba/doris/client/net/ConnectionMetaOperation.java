/*
 * Copyright(C) 1999-2010 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client.net;

import java.util.Map;
import java.util.Set;

import com.alibaba.doris.client.net.command.CheckCommand.Type;
import com.alibaba.doris.client.net.command.result.CheckResult;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;

/**
 * ConnectionMetaOperation
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-21
 */
public interface ConnectionMetaOperation {

    OperationFuture<Boolean> put(Key key, Value value);

    OperationFuture<Boolean> cas(Key key, Value value);

    OperationFuture<Boolean> puts(Map<Key, Value> map);

    OperationFuture<Value> get(Key key);

    OperationFuture<Map<Key, Value>> gets(Set<Key> keys);

    OperationFuture<Boolean> delete(Key key);

    OperationFuture<Boolean> cad(Key key, Value value);

    OperationFuture<String> migrate(String subcommand, String migrateRoute);

    OperationFuture<String> stats(String viewType, int namespace);

    OperationFuture<CheckResult> check(Type checkType);
}

/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.common.route;

import java.util.List;

import com.alibaba.doris.algorithm.RouteAlgorithm;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.config.Configurable;
import com.alibaba.doris.common.event.RouteConfigListener;
import com.alibaba.doris.common.operation.OperationEnum;

/**
 * RouteStrategy
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-5
 */
public interface RouteStrategy extends RouteConfigListener, Configurable {

    /**
     * Set route table.
     * 
     * @param routeTable
     */
    void setRouteTable(RouteTable routeTable);

    /**
     * find operation node.
     * 
     * @param type
     * @param copyCount
     * @param key
     * @return
     * @throws DorisRouterException
     */
    List<StoreNode> findNodes(OperationEnum type, int copyCount, String key) throws DorisRouterException;

    StoreNode findFailoverNode(OperationEnum type, int copyCount, String key, StoreNode sn) throws DorisRouterException;

    /**
     * Set algorithm
     * 
     * @param routeAlgorithm
     */
    public void setRouteAlgorithm(RouteAlgorithm routeAlgorithm);

    RouteTable getRouteTable();
}

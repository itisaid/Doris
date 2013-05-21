/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.dataserver.migrator.filter;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.algorithm.HashFunction;
import com.alibaba.doris.algorithm.KetamaHashFunction;
import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.NetException;
import com.alibaba.doris.client.net.OperationFuture;
import com.alibaba.doris.client.net.exception.RouteVersionOutOfDateException;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.route.VirtualRouter;
import com.alibaba.doris.dataserver.Module;
import com.alibaba.doris.dataserver.ModuleContext;
import com.alibaba.doris.dataserver.action.ActionType;
import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.action.data.BaseActionType;
import com.alibaba.doris.dataserver.action.data.ErrorActionData;
import com.alibaba.doris.dataserver.config.ModuleConstances;
import com.alibaba.doris.dataserver.core.Request;
import com.alibaba.doris.dataserver.core.RequestFilter;
import com.alibaba.doris.dataserver.core.RequestFilterChian;
import com.alibaba.doris.dataserver.core.Response;
import com.alibaba.doris.dataserver.migrator.MigrationManager;
import com.alibaba.doris.dataserver.migrator.connection.ConnectionManager;
import com.alibaba.doris.dataserver.migrator.task.MigrationTask;

/**
 * 数据迁移. MigrationFilter
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-25
 */
public class MigrationFilter implements RequestFilter {

    protected static final Logger logger       = LoggerFactory.getLogger(MigrationFilter.class);

    private MigrationManager      migrationManager;

    private VirtualRouter         virtualRouter;

    private Object                lock         = new Object();

    protected HashFunction        hashFunction = new KetamaHashFunction();

    public void setHashFunction(HashFunction hashFunction) {
        this.hashFunction = hashFunction;
    }

    /**
     * 迁移过滤器。 迁移状态中，使用代理写模式向 key 的迁移目标机多写一份.
     * 
     * @see com.alibaba.doris.dataserver.core.RequestFilter#doFilter(com.alibaba.doris.dataserver.core.Request,
     * com.alibaba.doris.dataserver.core.Response, com.alibaba.doris.dataserver.core.RequestFilterChian)
     */
    public void doFilter(Request request, Response response, RequestFilterChian filterChain) {
        OperationFuture<Boolean> proxyOperateFuture = null;
        ActionData actionData = request.getActionData();
        if (isNeedProxyOperation(actionData)) {
            initMigrationManager(request);

            // 如果正在迁移, 使用代理写模式
            // 判断当前节点的迁移状态 MIGRATING 或 MIGRATE_NODE_FINISHED 表示正在迁移中,需要开启写代理. 当 MigrationManager
            // 获取到最新的路由后，migrationManager.getMigrateStatus() 变为 ALL_FINISHED 状态，可以关闭代理写状态.
            if (migrationManager.haveMigrationTask()) {
                try {
                    proxyOperateFuture = handleProxyOperate(request, actionData);
                    if (logger.isDebugEnabled()) {
                        logger.debug("After handleProxyOperate. Do local storage operation.");
                    }

                    // 迁移过程中需要执行本地写，其它情况都直接代理到远程。
                    // if (NodeMigrateStatus.MIGRATING == migrationManager.getMigrateStatus()) {
                    // filterChain.doFilter(request, response);
                    // checkProxyOperationResult(proxyOperateFuture, actionData);
                    // return;
                    // }
                } catch (NetException e) {
                    // 代理写过程中出现网络异常，忽略，因为底层迁移逻辑也会发生异常，此时迁移状态应该变成迁移失败(NORMAL)
                    // try {
                    // Thread.sleep(10);
                    // } catch (InterruptedException e1) {
                    // }
                    logger.error("The remote proxy operation is failed. " + request.getKey(), e);
                }
            }
        }

        filterChain.doFilter(request, response);

        // 延迟获取远程代理操作的结果，使得远程操作和本地操作并行执行。
        try {
            checkProxyOperationResult(proxyOperateFuture, actionData);
        } catch (RouteVersionOutOfDateException e) {
            // 如果client的路由版本号过期，返回调用异常。
            ErrorActionData error = new ErrorActionData(ErrorActionData.VERSION_OUT_OF_DATE);
            // 设置server端的路由版本号
            error.setErrorMessage(((RouteVersionOutOfDateException) e).getNewRouteTable());
            // Note:此处覆盖action中写入response的结果；
            response.write(error);
        }
    }

    private void initMigrationManager(Request request) {
        if (migrationManager == null) {
            synchronized (lock) {
                if (migrationManager == null) {
                    Module module = request.getApplicationContext().getModuleByName(ModuleConstances.MIGRATION_MODULE);
                    ModuleContext moduleContext = module.getModuleContext();
                    migrationManager = (MigrationManager) moduleContext.getAttribute(MigrationManager._MigrationManager);
                    virtualRouter = migrationManager.getVirtualRouter();
                }
            }
        }
    }

    private void checkProxyOperationResult(OperationFuture<Boolean> proxyOperateFuture, ActionData actionData) {
        try {
            if (proxyOperateFuture != null) {
                Boolean result = proxyOperateFuture.get();
                if (null != result) {
                    // 远程代理操作失败，会导致当前请求返回失败信息。
                    if (result.booleanValue() == false) {
                        throw new ProxyOperationException("The proxy " + actionData.getActionType().getName()
                                                          + " operation is failed.");
                    }
                }
            }
        } catch (NetException e) {
            // 代理写过程中发生的网络写通信异常忽略；
            logger.error("Proxy " + actionData.getActionType().getName() + " error", e);
            if (e instanceof RouteVersionOutOfDateException) {
                throw e;
            }
        } catch (InterruptedException e) {
            throw new ProxyOperationException("The proxy " + actionData.getActionType().getName()
                                              + " operation is failed. Error message:" + e.getLocalizedMessage());
        } catch (ExecutionException e) {
            throw new ProxyOperationException("The proxy " + actionData.getActionType().getName()
                                              + " operation is failed. Error message:" + e.getLocalizedMessage());
        }
    }

    /**
     * @param request
     * @param actionData
     */
    private OperationFuture<Boolean> handleProxyOperate(Request request, ActionData actionData) {
        Key key = request.getKey();

        // 遍历所有的在迁移中的任务，查看当前请求是否需要代理；
        Map<String, MigrationTask> activeTaskMap = migrationManager.getMigrationTaskScheduler().getActiveTaskMap();
        Iterator<MigrationTask> taskIterator = activeTaskMap.values().iterator();
        while (taskIterator.hasNext()) {
            MigrationTask task = taskIterator.next();
            // 根据key，计算出是否需要执行代理；
            String targetPhId = computeTargetPhNo(key, task);
            if (null != targetPhId) {
                // 如果需要代理，则获取连接，代理操作；
                ConnectionManager connectionManager = task.getConnectionManager();
                if (!connectionManager.isOpen()) {
                    logger.error("Prepare to Proxy operate, but connection manager is close. Give up proxy operate, data loss may occur.");
                    throw new ProxyOperationException("The target connection of migration is closed.");
                    // return null;
                }

                return proxyOperation(connectionManager, actionData.getActionType(), key, request.getValue(),
                                      targetPhId);
            }
        }

        return null;
    }

    /**
     * getActionType
     * 
     * @param actionData
     * @return
     */
    public boolean isNeedProxyOperation(ActionData actionData) {
        ActionType type = actionData.getActionType();
        return BaseActionType.SET == type || BaseActionType.DELETE == type;
    }

    /**
     * @param connectionManager
     * @param actionData
     * @param key
     * @param value
     */
    private OperationFuture<Boolean> proxyOperation(ConnectionManager connectionManager, ActionType actionType,
                                                    Key key, Value value, String targetPhId) {
        OperationFuture<Boolean> future = null;

        if (actionType == BaseActionType.SET) {
            future = proxySet(connectionManager, targetPhId, key, value);
            if (logger.isDebugEnabled()) {
                logger.debug("Proxy set to " + targetPhId + ", key=" + key);
            }
        } else if (actionType == BaseActionType.DELETE) {
            future = proxyDelete(connectionManager, targetPhId, key);
            if (logger.isDebugEnabled()) {
                logger.debug("Proxy delete to " + targetPhId + ", key=" + key);
            }
        }

        return future;
    }

    /**
     * proxyDelete
     * 
     * @param connectionManager
     * @param targetPhId
     * @param key
     */
    private OperationFuture<Boolean> proxyDelete(ConnectionManager connectionManager, String targetPhId, Key key) {
        OperationFuture<Boolean> future = null;
        Connection connection = connectionManager.getConnection(targetPhId);
        try {
            if (null != connection) {
                future = connection.delete(key);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Proxy delete - key=" + key.getPhysicalKey());
            }
        } catch (Exception e) {
            logger.error("Fail to proxy delete, key=" + key.getPhysicalKey() + ", cause:" + e, e);
            throw new ProxyOperationException(e);
        }

        return future;
    }

    /**
     * proxySet
     * 
     * @param connectionManager
     * @param targetPhId
     * @param key
     * @param value
     */
    private OperationFuture<Boolean> proxySet(ConnectionManager connectionManager, String targetPhId, Key key,
                                              Value value) {
        OperationFuture<Boolean> future = null;
        Connection connection = connectionManager.getConnection(targetPhId);
        try {
            if (null != connection) {
                future = connection.put(key, value);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Proxy set - key=" + key.getPhysicalKey());
            }
        } catch (Exception e) {
            logger.error("Fail to proxy set, key=" + key.getPhysicalKey() + ", cause:" + e.getMessage(), e);
            logger.error("Retrying to set.");
            key.setRouteVersion(0);
            // set key vnode = -1.
            future = connection.put(key, value);
        }

        return future;
    }

    /**
     * 计算目标机器节点编号
     * 
     * @return
     */
    private String computeTargetPhNo(Key key, MigrationTask task) {
        String phKey = key.getPhysicalKey();

        int targetVNo = key.getVNode();
        if (targetVNo == -1) {
            targetVNo = virtualRouter.findVirtualNode(phKey);
        }

        return task.getProxyTarget(targetVNo);
    }
}

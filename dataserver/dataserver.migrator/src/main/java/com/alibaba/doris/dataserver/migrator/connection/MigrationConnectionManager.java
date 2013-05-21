/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.dataserver.migrator.connection;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.NetException;
import com.alibaba.doris.common.route.MigrationRoutePair;

/**
 * MigrationConnectionManager
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-6-1
 */
public class MigrationConnectionManager implements ConnectionManager {

    private static final Logger       logger      = LoggerFactory.getLogger(MigrationConnectionManager.class);

    protected Map<String, Connection> connections = new HashMap<String, Connection>();
    protected volatile boolean        ok          = false;

    protected ReentrantLock           lock        = new ReentrantLock();

    /**
     * 初始化一组连接
     * 
     * @param routePairs
     */
    public MigrationConnectionManager() {
    }

    /**
     * @param routePairs
     */
    public void connect(List<MigrationRoutePair> routePairs) {
        lock.lock();
        try {
            connect0(routePairs);

            logConnection();

        } catch (Exception e) {

            logger.error("Fail to create migration connection manager, and give up migration. Cause:" + e);

            close();

            throw new NetException(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * @param routePairs
     */
    void connect0(List<MigrationRoutePair> routePairs) {

        if (logger.isDebugEnabled()) logger.debug("Prepare to get migrate connections. ");

        for (int i = 0; i < routePairs.size(); i++) {
            MigrationRoutePair routePair = routePairs.get(i);

            String[] pId = routePair.getTargetPhysicalId().split(":");
            String ip = pId[0];
            int port = Integer.valueOf(pId[1]).intValue();

            // 和目标机器建立连接
            InetSocketAddress address = new InetSocketAddress(ip, port);

            String adsKey = address.toString();

            adsKey = adsKey.startsWith("/") ? adsKey.substring(1) : adsKey;

            Connection connection = null;

            if (logger.isDebugEnabled()) logger.debug("VNode " + routePair.getVnode() + ",Target address: " + adsKey);

            if (!connections.containsKey(adsKey)) {

                createConnection(address, adsKey);

            } else {
                connection = connections.get(adsKey);
                if (!connection.isConnected()) {
                    connection.close();
                    connection = null;
                    createConnection(address, adsKey);
                }
            }
        }

        ok = true;
    }

    protected void createConnection(InetSocketAddress address, String adsKey) {
        Connection connection;
        connection = createConnection(address);
        connections.put(adsKey, connection);

        if (logger.isDebugEnabled()) logger.debug("Create a migrate connection " + adsKey);
    }

    /**
     * 创建到Target Node的实际的物理连接
     * 
     * @param address
     * @return
     */
    protected Connection createConnection(InetSocketAddress address) {
        Connection connection = new ThreadLocalConnection(address);
        return connection;
    }

    private void logConnection() {
        if (logger.isInfoEnabled()) {
            logger.info("Succeed to get migrate connections. Connections: " + connections.size());
            int i = 0;
            Iterator<String> itor = connections.keySet().iterator();
            while (itor.hasNext()) {
                String cnKey = itor.next();
                logger.info("No." + (i++) + ": migration Connection:" + cnKey);
            }
        }
    }

    /**
     * (non-Javadoc)
     * 
     * @see com.alibaba.doris.dataserver.migrator.connection.ConnectionManager#getConnection(java.net.InetSocketAddress)
     */
    public Connection getConnection(String address) {
        lock.lock();
        try {
            return connections.get(address);
        } finally {
            lock.unlock();
        }
    }

    public boolean isOpen() {
        lock.lock();
        try {
            return ok;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        lock.lock();
        try {
            close0();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 释放所有连接
     */
    public void close0() {
        for (Entry<String, Connection> entry : connections.entrySet()) {
            Connection connection = entry.getValue();
            if (connection != null) {
                connection.close();

                if (logger.isDebugEnabled()) logger.debug("Migrate connection closed: " + connection);
            }
        }
        connections.clear();

        if (logger.isDebugEnabled()) logger.debug("Migrate connection manager clear all connections");

        ok = false;
    }

    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }
}

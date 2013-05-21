/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.client.net.DataSource;
import com.alibaba.doris.client.operation.OperationDataSourceGroup;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.config.ConfigManager;
import com.alibaba.doris.common.configer.RouteTableConfiger;
import com.alibaba.doris.common.event.RouteConfigChangeEvent;
import com.alibaba.doris.common.event.RouteConfigListener;
import com.alibaba.doris.common.operation.OperationEnum;
import com.alibaba.doris.common.route.DorisRouterException;
import com.alibaba.doris.common.route.RouteStrategy;
import com.alibaba.doris.common.route.RouteTable;
import com.alibaba.doris.common.route.VirtualRouter;

/**
 * DataSourceRouterImpl
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-5
 */
public class DataSourceRouterImpl implements DataSourceRouter {

    private static final Logger                 logger = LoggerFactory.getLogger(DataSourceRouterImpl.class);

    private Class<? extends DataSource>         dataSourceClass;
    private Class<? extends RouteTableConfiger> nodeConfigerClass;
    private Class<? extends RouteStrategy>      routeStrategyClass;

    private RouteTableConfiger                  routeTableConfiger;
    private volatile RouteStrategyHolder        routeStrategyHolder;
    private Properties                          configProperties;
    private RouteConfigListenerWrapper          routeConfigListener;

    private ConfigManager                       configManager;

    public VirtualRouter                        virtualRouter;

    public void setDataSourceClass(Class<? extends DataSource> dataSourceClass) {
        this.dataSourceClass = dataSourceClass;
    }

    public void setNodeConfigerClass(Class<? extends RouteTableConfiger> nodeConfigerClass) {
        this.nodeConfigerClass = nodeConfigerClass;
    }

    public void setNodeRouterClass(Class<? extends RouteStrategy> nodeRouterClass) {
        this.routeStrategyClass = nodeRouterClass;
    }

    /**
     * @see com.alibaba.doris.client.DataSourceRouter#getRouteTableConfiger()
     */
    public RouteTableConfiger getRouteTableConfiger() {
        return routeTableConfiger;
    }

    /**
     * @see com.alibaba.doris.client.DataSourceRouter#getRouteStrategy()
     */
    public RouteStrategy getRouteStrategy() {
        return routeStrategyHolder.routeStrategy;
    }

    public Map<String, List<DataSource>> getAllDataSources() {
        return routeStrategyHolder.allDataSources;
    }

    public void setConfigProperties(Properties properties) {
        this.configProperties = properties;
    }

    public void setVirtualRouter(VirtualRouter virtualRouter) {
        this.virtualRouter = virtualRouter;
    }

    public VirtualRouter getVirtualRouter() {
        return virtualRouter;
    }

    /**
     * Get operation datasources group by key and write/read count.
     * <p/>
     * e.g.
     * <li>getOperationDataSourceGroup("001", 2), return datasource 2,3
     * <li>getOperationDataSourceGroup("001", 1), return datasource 2
     * <li>getOperationDataSourceGroup("001", 3), return datasource 2,3,5
     * 
     * @throws DorisRouterException
     * @see com.alibaba.doris.client.DataSourceRouter#getOperationDataSourceGroup(java.lang.Object, int)
     */
    public OperationDataSourceGroup getOperationDataSourceGroup(OperationEnum operationEnum, int count, String key)
                                                                                                                   throws DorisRouterException {
        RouteStrategyHolder rHolder = routeStrategyHolder;
        List<StoreNode> storeNodes = rHolder.routeStrategy.findNodes(operationEnum, count, key);

        List<DataSource> dataSourceGroup = new ArrayList<DataSource>();
        for (StoreNode storeNode : storeNodes) {

            String seqNo = String.valueOf(storeNode.getSequence().getValue());
            List<DataSource> seqDataSources = rHolder.allDataSources.get(seqNo);
            DataSource dataSource = seqDataSources.get(storeNode.getLogicId());
            dataSourceGroup.add(dataSource);
        }

        OperationDataSourceGroup group = new OperationDataSourceGroup(key, dataSourceGroup, storeNodes);
        return group;
    }

    /**
     * Init config after setting values.
     * 
     * @throws DataSourceException
     */
    public void refresh() throws DataSourceException {
        // refreshDataSource();
    }

    /**
     * Recreate all the datasource according to the lastest node list.
     * 
     * @throws DataSourceException
     */
    protected void refreshDataSource(RouteConfigChangeEvent event) throws DataSourceException {
        RouteTable routeTable = event.getRouteTable();
        List<StoreNode> nodeList = routeTable.getNodeList();

        RouteStrategyHolder rHolder = generateRouteStrategyHolder(routeTable);

        rHolder.allDataSources = new HashMap<String, List<DataSource>>();
        Map<String, List<DataSource>> tobeClosedDataSources = null;
        if (null != this.routeStrategyHolder) {
            tobeClosedDataSources = cloneDataSources(this.routeStrategyHolder.allDataSources);
        }

        for (int i = 0; i < nodeList.size(); i++) {
            StoreNode storeNode = nodeList.get(i);

            try {
                int seqNo = storeNode.getSequence().getValue();
                int logicId = storeNode.getLogicId();
                String strSeqNo = String.valueOf(seqNo);
                ArrayList<DataSource> seqDatasources = (ArrayList<DataSource>) rHolder.allDataSources.get(strSeqNo);

                if (seqDatasources == null) {
                    seqDatasources = new ArrayList<DataSource>();
                    rHolder.allDataSources.put(strSeqNo, seqDatasources);
                }

                DataSource dataSource = (DataSource) dataSourceClass.newInstance();
                dataSource.setSequence(seqNo);
                dataSource.setNo(logicId);
                dataSource.setIp(storeNode.getIp());
                dataSource.setPort(storeNode.getPort());
                dataSource.setConfigProperties(configManager.getProperties());
                dataSource.initConfig();

                if (null != tobeClosedDataSources) {
                    List<DataSource> tobeClosedDataSourceList = tobeClosedDataSources.get(strSeqNo);

                    // 扩容情况时，后加入的节点的logicId会大于tobeClosedDataSourceList的size
                    if (tobeClosedDataSourceList.size()>logicId) {
                        DataSource ds = tobeClosedDataSourceList.get(logicId);
                        if (dataSource.equals(ds)) {
                            // 设置原有连接不释放。
                            tobeClosedDataSourceList.set(logicId, null);
                            // 当原有连接已经存在，则直接重用原来的连接。
                            dataSource = ds;
                        }
                    }
                }

                seqDatasources.ensureCapacity(storeNode.getLogicId() + 1);
                seqDatasources.add(storeNode.getLogicId(), dataSource);
                if (logger.isDebugEnabled()) {
                    logger.debug("Create datasource: " + dataSource + "  " + storeNode + ", " + storeNode.getPhId());
                }
            } catch (Exception e) {
                throw new DataSourceException("Fail to create DataSource." + e.getMessage(), e);
            }
        }

        // Everything is okay,
        routeConfigListener.setRouteConfigListener(rHolder.routeStrategy);
        rHolder.routeStrategy.onConfigChange(event);

        this.routeStrategyHolder = rHolder;

        // close all connection.
        releaseDatasourcesThread.release(tobeClosedDataSources);
    }

    /**
     * 获取某个数据源对应的 StoreNode.
     */
    public StoreNode getStoreNodeOf(DataSource dataSource) {

        int seqNo = dataSource.getSequence();
        int logicId = dataSource.getNo();

        StoreNode storeNode = routeTableConfiger.getRouteTable().getStoreNode(seqNo, logicId);
        return storeNode;
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<DataSource>> cloneDataSources(Map<String, List<DataSource>> source) {
        Map<String, List<DataSource>> result = new HashMap<String, List<DataSource>>();

        Iterator<String> keyIterator = source.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            ArrayList<DataSource> sourceDsList = (ArrayList<DataSource>) source.get(key);
            result.put(key, (ArrayList<DataSource>) sourceDsList.clone());
        }

        return result;
    }

    /**
     * 获取某个StoreNode对应的DataSource.
     */
    public DataSource getDataSourceOf(StoreNode storeNode) {
        int seqNo = storeNode.getSequence().getValue();
        int logicId = storeNode.getLogicId();

        String seqNoStr = String.valueOf(seqNo);

        List<DataSource> seqDataSources = routeStrategyHolder.allDataSources.get(seqNoStr);

        if (logicId < seqDataSources.size()) {
            DataSource dataSource = seqDataSources.get(logicId);
            return dataSource;
        } else {
            throw new IllegalArgumentException("Cant't find DataSource of storeNode " + storeNode);
        }
    }

    public void initConfig() {
        try {
            if (null == releaseDatasourcesThread) {
                releaseDatasourcesThread = new ReleaseDatasourcesThread();
                releaseDatasourcesThread.start();
            }

            routeTableConfiger = nodeConfigerClass.newInstance();
            // routeStrategy = routeStrategyClass.newInstance();

            routeTableConfiger.setConfigManager(configManager);
            // routeStrategy.setConfigManager(configManager);

            // routeStrategyHolder = generateRouteStrategyHolder();
            routeConfigListener = new RouteConfigListenerWrapper();

            routeTableConfiger.addConfigListener(this);
            // routeTableConfiger.addConfigListener(routeConfigListener);

            configManager.addConfigListener(routeTableConfiger);

            routeTableConfiger.initConfig();
        } catch (Exception e) {
            logger.error("initConfig", e);
        }
    }

    private RouteStrategyHolder generateRouteStrategyHolder(RouteTable routeTable) {
        RouteStrategyHolder holder = new RouteStrategyHolder();

        try {
            holder.routeStrategy = routeStrategyClass.newInstance();
            holder.routeStrategy.setConfigManager(configManager);
            holder.routeStrategy.setRouteTable(routeTable);
            holder.routeStrategy.initConfig();
        } catch (Exception e) {
            logger.error("generateRouteStrategyHolder", e);
        }

        return holder;
    }

    public void onConfigChange(RouteConfigChangeEvent event) {
        try {
            refreshDataSource(event);
        } catch (DataSourceException e) {
            logger.error("refreshDataSource", e);
        }
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    private static class RouteStrategyHolder {

        RouteStrategy                 routeStrategy;
        Map<String, List<DataSource>> allDataSources;
    }

    private static class RouteConfigListenerWrapper implements RouteConfigListener {

        public void onConfigChange(RouteConfigChangeEvent event) {
            if (null != routeConfigListener) {
                routeConfigListener.onConfigChange(event);
            }
        }

        public void setRouteConfigListener(RouteConfigListener routeConfigListener) {
            this.routeConfigListener = routeConfigListener;
        }

        private volatile RouteConfigListener routeConfigListener;
    }

    private static class ReleaseDatasourcesThread extends Thread {

        public ReleaseDatasourcesThread() {
            setName("Release-Datasources-Thread cleanup");
            setDaemon(true);
        }

        public void run() {
            try {
                while (true) {
                    Map<String, List<DataSource>> dataSources = releaseDatasourceQueue.take();
                    if (null != dataSources) {
                        Thread.sleep(10000);
                        Collection<List<DataSource>> datasources = dataSources.values();
                        for (List<DataSource> datasourceList : datasources) {
                            for (DataSource datasource : datasourceList) {
                                if (datasource != null) {
                                    close(datasource);
                                }
                            }
                        }
                    }
                }
            } catch (InterruptedException ignore) {
            }
        }

        private void close(DataSource datasource) {
            try {
                datasource.close();
            } catch (Exception e) {
                logger.error("Close datasource failed! " + datasource, e);
            }
        }

        public void release(Map<String, List<DataSource>> dataSources) {
            if (null != dataSources) {
                try {
                    releaseDatasourceQueue.put(dataSources);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        private BlockingQueue<Map<String, List<DataSource>>> releaseDatasourceQueue = new ArrayBlockingQueue<Map<String, List<DataSource>>>(
                                                                                                                                            100);
    }

    private ReleaseDatasourcesThread releaseDatasourcesThread;
}

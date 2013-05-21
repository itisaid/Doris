/**
 * Project: doris.config.server-1.0-SNAPSHOT
 * 
 * File Created at 2011-4-27
 * $Id$
 * 
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.alibaba.doris.admin.service.common.route;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.dataobject.RouterConfigInstanceDO;
import com.alibaba.doris.admin.monitor.MonitorEnum;
import com.alibaba.doris.admin.monitor.SystemLogMonitor;
import com.alibaba.doris.admin.service.AdminNodeService;
import com.alibaba.doris.admin.service.PropertiesService;
import com.alibaba.doris.admin.service.RouteConfigService;
import com.alibaba.doris.admin.service.common.Managerable;
import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.MonitorWarnConstants;
import com.alibaba.doris.common.NodeRouteStatus;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.StoreNodeSequenceEnum;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author mianhe
 */
public class RouteConfigProcessor implements Managerable {

    private static final Log                      logger                  = LogFactory
                                                                                  .getLog(RouteConfigProcessor.class);

    private volatile boolean                      initialized             = false;
    private Thread                                configLoadThread;

    private volatile boolean                      stopped                 = false;
    private AdminNodeService                      nodeService             = AdminServiceLocator
                                                                                  .getAdminNodeService();

    private volatile RouterConfigInstanceDO       currentConfigInstanceDo = null;
    private volatile Map<Integer, PhysicalNodeDO> currentPhysicalNodeMap  = new ConcurrentHashMap<Integer, PhysicalNodeDO>();

    private static final RouteConfigProcessor     instance                = new RouteConfigProcessor();
    private RouteConfigService                    routeConfigService      = AdminServiceLocator
                                                                                  .getRouteConfigService();

    private static PropertiesService              propertyService         = AdminServiceLocator
                                                                                  .getPropertiesService();
    private static final long                     routeConfigScanInterval = propertyService
                                                                                  .getProperty(
                                                                                          "routeConfigScanInterval",
                                                                                          Long.TYPE,
                                                                                          AdminServiceConstants.ROUTER_SCAN_DEFAULT_RELOAD_INTERVAL);

    private RouteConfigProcessor() {
        try {
            init();
        } catch (DorisConfigServiceException e) {
            SystemLogMonitor.error(MonitorEnum.ROUTER, MonitorWarnConstants.RE_GEN_ROUTE_FAILED, e);
            logger.error("failed to init " + RouteConfigProcessor.class, e);
        }
    }

    public static RouteConfigProcessor getInstance() {
        return instance;
    }

    private synchronized void init() throws DorisConfigServiceException {
        if (initialized == true) {
            if (logger.isInfoEnabled()) {
                logger.warn("RouteConfigCachableProcessor is already initialized.");
            }
            return;
        }

        //启动的时候，load配置数据库中的实时配置
        load();

        //启动定时刷新配置线程
        configLoadThread = new Thread(new LoadTask(), "load-config-instance-thread");
        configLoadThread.setDaemon(true);
        //configLoadThread.start();

        this.initialized = true;
    }

    /**
     * @throws DorisConfigServiceException
     */
    private void load() throws DorisConfigServiceException {
        //刚启动的时候，内存中没有缓存
        Collection<PhysicalNodeDO> latestPhysicalNodeList = loadValidNodes();

        // fill map
        boolean filled = fillPhysicalNodeMap(latestPhysicalNodeList, false);
        
        if (filled) {
            currentConfigInstanceDo = routeConfigService.loadLatestConfigInstance();
        }

        if (currentConfigInstanceDo == null) {
            insertNewConfigInstance(false);
        }

    }

    private Collection<PhysicalNodeDO> loadValidNodes() throws DorisConfigServiceException {
        Collection<PhysicalNodeDO> latestPhysicalNodeList = nodeService.queryAllPhysicalNodes();
        //filter
        this.filterNodes(latestPhysicalNodeList);

        //valid
        this.validateNodes(latestPhysicalNodeList);
        
        return latestPhysicalNodeList;
    }

    private boolean fillPhysicalNodeMap(Collection<PhysicalNodeDO> latestPhysicalNodeList, boolean clearmap) {
        
        if (clearmap) {
            currentPhysicalNodeMap.clear();
        }
        
        if (currentPhysicalNodeMap.isEmpty()) {
            if (latestPhysicalNodeList != null && !latestPhysicalNodeList.isEmpty()) {
                for (PhysicalNodeDO latestNode : latestPhysicalNodeList) {
                    if (isValidRoutableNode(latestNode)) {
                        currentPhysicalNodeMap.put(latestNode.getId(), latestNode);
                    }
                }
            } else {
                //正常情况下配置肯定不能是null／empty,警告，后续会定时刷新，　这里不需要抛异常
                logger
                        .warn("Cannot load doris config, DORIS config database may not have config data.");
            }
            return true;
        }
        return false;
    }

    private void insertNewConfigInstance(boolean needCheckDB) {
        if (needCheckDB) {
            RouterConfigInstanceDO newConfigInstance = buildNewConfigInstance(currentPhysicalNodeMap);
            // 重数据库中取最新的Config Instance， 如果路由一致则没有必要生成新的。
            RouterConfigInstanceDO latestConfigFromDb = routeConfigService.loadLatestConfigInstance();
            List<StoreNode> newStoreNodes = JSON.parseArray(newConfigInstance.getContent(),
                    StoreNode.class);

            List<StoreNode> dbStoreNodes = JSON.parseArray(latestConfigFromDb.getContent(),
                    StoreNode.class);
            
            long begin = System.currentTimeMillis();
            boolean identical = checkIdentical(newStoreNodes, dbStoreNodes);
            if (logger.isWarnEnabled()) {
                logger.warn("indential compared cost:" + (System.currentTimeMillis() - begin));
            }
            
            if (identical) {
                currentConfigInstanceDo = latestConfigFromDb;
                if (logger.isWarnEnabled()) {
                    logger.warn("new config has already generated!!!");
                }
            } else {
                int id = routeConfigService.insertConfigInstance(newConfigInstance);
                newConfigInstance.setId(id);
                currentConfigInstanceDo = routeConfigService.loadLatestConfigInstance();

                if (logger.isWarnEnabled()) {
                    int loadedid = currentConfigInstanceDo.getId();
                    if (id != loadedid) {
                        logger.warn("Confilict route version occurs, use loaded id from db.[insert id:"
                                + id + ", loaded id:" + loadedid + "]");
                    }
                    logger.warn("current config id:" + id);
                }
            }

        } else {
            RouterConfigInstanceDO newConfigInstance = buildNewConfigInstance(currentPhysicalNodeMap);
            int id = routeConfigService.insertConfigInstance(newConfigInstance);
            newConfigInstance.setId(id);
            currentConfigInstanceDo = routeConfigService.loadLatestConfigInstance();
            if (logger.isWarnEnabled()) {
                int loadedid = currentConfigInstanceDo.getId();
                if (id != loadedid) {
                    logger.warn("Confilict route version occurs, use loaded id from db.[insert id:"
                            + id + ", loaded id:" + loadedid + "]");
                }
                logger.warn("system init, new config id:" + loadedid);
            }
        }
    }

    private boolean checkIdentical(List<StoreNode> newStoreNodes, List<StoreNode> dbStoreNodes) {
       
        if ((newStoreNodes == null && dbStoreNodes != null)
                || (newStoreNodes != null && dbStoreNodes == null)) {
            return false;
        }

        if (newStoreNodes != null && dbStoreNodes != null) {
            if (newStoreNodes.size() != dbStoreNodes.size()) {
                return false;
            }
            
            Comparator<StoreNode> snComparator = new Comparator<StoreNode>() {

                public int compare(StoreNode o1, StoreNode o2) {
                    return o1.getPhId().compareTo(o2.getPhId());
                }
                
            };
            Collections.sort(dbStoreNodes, snComparator);
            Collections.sort(newStoreNodes, snComparator);
            
            for (int i = 0; i < dbStoreNodes.size(); i++) {
                StoreNode dbNode = dbStoreNodes.get(i);
                StoreNode newNode = newStoreNodes.get(i);
                boolean isDifferent = isDifferent(dbNode, newNode);
                if (isDifferent) {
                    return false;
                }
            }
        }

        return true;
    }

    public void close() {

        if (!initialized) {
            if (logger.isInfoEnabled()) {
                logger.warn("The RouteConfigCachableProcessor is not initialized yet.");
            }
            return;
        }

        if (logger.isInfoEnabled()) {
            logger.info("RouteConfigCachableProcessor is closing!!!");
        }
        stopped = true;

        if (logger.isInfoEnabled()) {
            logger.info("Thread of config refresh is stopping!!!");
        }
        
        if (configLoadThread != null) {
            configLoadThread.interrupt();
        }
    }

    /**
     * @return the currentConfigInstanceDo
     * @throws DorisConfigServiceException
     */
    public RouterConfigInstanceDO getCurrentConfigInstanceDo() throws DorisConfigServiceException {
        if (!initialized) {
            if (logger.isInfoEnabled()) {
                logger.warn("The RouteConfigCachableProcessor is not initialized yet.");
            }
        }

        if (currentConfigInstanceDo == null) {
            //refresh 
            if (logger.isDebugEnabled()) {
                logger.debug("cannot find configinstance:refresh.");
            }
            refresh();
        }

        return currentConfigInstanceDo;
    }

    
    public synchronized void refreshWithDbLatest() throws DorisConfigServiceException {
        if (logger.isWarnEnabled()) {
            logger.warn("forced refresh starts...");
        }

        RouterConfigInstanceDO dbConfigInstanceDo = routeConfigService.loadLatestConfigInstance();
        if (currentConfigInstanceDo == null
                || (dbConfigInstanceDo.getId() > currentConfigInstanceDo.getId())) {
            
            currentConfigInstanceDo = dbConfigInstanceDo;

            Collection<PhysicalNodeDO> latestPhysicalNodeList = loadValidNodes();
            
            fillPhysicalNodeMap(latestPhysicalNodeList, true);
            
            if (logger.isWarnEnabled()) {
                logger.warn("forced refreshed..., db config id :" + dbConfigInstanceDo.getId());
            }
        }
    }

    public synchronized void refresh() throws DorisConfigServiceException {
        if (!initialized) {
            if (logger.isWarnEnabled()) {
                logger.warn("The RouteConfigCachableProcessor is not initialized yet.");
            }
            return;
        }

        Collection<PhysicalNodeDO> latestPhysicalNodeList = loadValidNodes();

        boolean hasNodeChanged = checkNodeChanges(latestPhysicalNodeList);

        if (hasNodeChanged) {
            insertNewConfigInstance(true);
        } else if (currentConfigInstanceDo == null) {
            //系统没有初始化，或者初始化失败的补偿机制
            if (logger.isWarnEnabled()) {
                logger.warn("The RouteConfigCachableProcessor is not initialized, reload.");
            }
            currentConfigInstanceDo = routeConfigService.loadLatestConfigInstance();
        } else {
            // 没有变更，无需更新currentConfigInstanceDo。
        }

        if (currentConfigInstanceDo == null) {
            logger.warn("Cannot load doris config, DORIS config database may not have config data.");
        }
    }

    private boolean checkNodeChanges(Collection<PhysicalNodeDO> latestPhysicalNodeList) {
        
        int latestPhysicalNodesNum = (latestPhysicalNodeList == null) ? 0 : latestPhysicalNodeList.size();

        // 数据库没有可用节点，
        if (latestPhysicalNodesNum == 0) {
            if (currentPhysicalNodeMap.isEmpty()) {
                return false;   
            } else {
                //清空：currentPhysicalNodeMap
                currentPhysicalNodeMap.clear();
                return true;
            }
        }
        
        // 数据库有可用节点, 检查node并且更新currentPhysicalNodeMap
        boolean hasNodeChanged = false;
        if (latestPhysicalNodeList != null && !latestPhysicalNodeList.isEmpty()) {
            List<Integer> allLatestPhysicalNodeIds = new ArrayList<Integer>();
            for (PhysicalNodeDO latestNode : latestPhysicalNodeList) {
                allLatestPhysicalNodeIds.add(latestNode.getId());
                PhysicalNodeDO currentNode = currentPhysicalNodeMap.get(latestNode.getId());
                if (currentNode == null || isChanged(latestNode, currentNode)) {
                    currentPhysicalNodeMap.put(latestNode.getId(), latestNode);
                    hasNodeChanged = true;
                }
            }

            for (Integer nodeId : currentPhysicalNodeMap.keySet()) {
                if (!allLatestPhysicalNodeIds.contains(nodeId)) {
                    currentPhysicalNodeMap.remove(nodeId);
                    hasNodeChanged = true;
                }
            }
        }
        
        return hasNodeChanged;
    }

    private void validateNodes(Collection<PhysicalNodeDO> allPhyNodes)
            throws DorisConfigServiceException {

        //for validate logical id
        Map<StoreNodeSequenceEnum, List<PhysicalNodeDO>> nodesGroup = new HashMap<StoreNodeSequenceEnum, List<PhysicalNodeDO>>();
        // for validate nodes.
        Map<String, Set<StoreNodeSequenceEnum>> ipGroups = new HashMap<String, Set<StoreNodeSequenceEnum>>();
        for (PhysicalNodeDO node : allPhyNodes) {
            StoreNodeSequenceEnum seq = StoreNodeSequenceEnum.getTypeByValue(node.getSerialId());
            if (seq == null) {
                throw new InvalidNodeException("invalid sequence number for node with id :"
                        + node.getId());
            }

            if (StringUtils.isBlank(node.getIp())) {
                throw new InvalidNodeException("ip for node with id :" + node.getId());
            }

            if (StringUtils.isBlank(node.getPhysicalId())) {
                throw new InvalidNodeException("invalid sequence number for node with id :"
                        + node.getId());
            }

            if (node.getPort() <= 0 || node.getPort() > 65535) {
                throw new InvalidNodeException("port is not valid for node with id :"
                        + node.getId());
            }

            if (StringUtils.isBlank(node.getMachineId())) {
                throw new InvalidNodeException("machine id is empty for node with id :"
                        + node.getId());
            }

            List<PhysicalNodeDO> nodes = nodesGroup.get(seq);
            if (nodes == null) {
                nodes = new ArrayList<PhysicalNodeDO>();
                nodesGroup.put(seq, nodes);
            }

            nodes.add(node);

            Set<StoreNodeSequenceEnum> ips = ipGroups.get(node.getIp());
            if (ips == null) {
                ips = new HashSet<StoreNodeSequenceEnum>();
            }

            if (StoreNodeSequenceEnum.isNormalSequence(seq)) {
                ips.add(seq);
            }

            ipGroups.put(node.getIp(), ips);
        }

        // validate logic id in one sequence:
        for (Map.Entry<StoreNodeSequenceEnum, List<PhysicalNodeDO>> oneSequenceNodes : nodesGroup
                .entrySet()) {
            //valid the logical id in one sequence.
            List<PhysicalNodeDO> nodes = oneSequenceNodes.getValue();
            List<Integer> allLogicalIds = new ArrayList<Integer>();
            for (PhysicalNodeDO node : nodes) {
                allLogicalIds.add(node.getLogicalId());
            }
            Collections.sort(allLogicalIds);

            for (int i = 0; i < allLogicalIds.size(); i++) {
                Integer logicalId = allLogicalIds.get(i);
                if (!logicalId.equals(i)) {
                    throw new InvalidNodeException("invalid logical id: " + logicalId
                            + " in sequece :" + oneSequenceNodes.getKey());
                }
            }

        }

        // the nodes in one machine cannot allocated to different normal sequence.
        for (Map.Entry<String, Set<StoreNodeSequenceEnum>> entry : ipGroups.entrySet()) {
            if (entry.getValue().size() > 1) {
                throw new InvalidNodeException("the nodes in machine (ip=" + entry.getKey()
                        + ") is allocated in different sequence" + entry.getValue());
            }
        }

        //Warning if there is no temp sequence.
        List<PhysicalNodeDO> tempNodes = nodesGroup.get(StoreNodeSequenceEnum.TEMP_SEQUENCE);
        if (tempNodes == null || tempNodes.isEmpty()) {
            SystemLogMonitor.error(MonitorEnum.ROUTER, MonitorWarnConstants.ROUTE_NO_TEMP_NODES);
        }
    }

    private void filterNodes(Collection<PhysicalNodeDO> latestPhysicalNodeList) {
        Iterator<PhysicalNodeDO> entryIter = latestPhysicalNodeList.iterator();
        while (entryIter.hasNext()) {
            PhysicalNodeDO pNode = entryIter.next();
            if (!isValidRoutableNode(pNode)) {
                entryIter.remove();
            }
        }
    }

    private class LoadTask implements Runnable {

        public void run() {
            try {
                while (!stopped) {
                    try {
                        Thread.sleep(routeConfigScanInterval);
                    } catch (Exception e) {
                        logger.warn("Exception when the key fetch task sleep: " + e.toString());
                    }

                    if (stopped) {
                        return;
                    }

                    try {
                        if (logger.isDebugEnabled()) {
                            logger.debug("refresh route schedully.");
                        }
                        refresh();
                    } catch (Exception e) {
                        logger.error("Exception when fetch doris config: ", e);
                        SystemLogMonitor.error(MonitorEnum.ROUTER,
                                MonitorWarnConstants.RE_GEN_ROUTE_FAILED, e);
                    }
                }
            } finally {
                logger.warn("the key fetch thread exit!");
            }

        }

    }

    private RouterConfigInstanceDO buildNewConfigInstance(
                                                          Map<Integer, PhysicalNodeDO> currentPhysicalNodeMap) {
        RouterConfigInstanceDO configInstanceDO = new RouterConfigInstanceDO();
        configInstanceDO.setGmtCreate(new Date());
        String content = buildConfigInstanceContent(currentPhysicalNodeMap);
        configInstanceDO.setContent(content);
        return configInstanceDO;
    }

    private String buildConfigInstanceContent(Map<Integer, PhysicalNodeDO> currentPhysicalNodeMap) {
        List<StoreNode> storeNodeList = new ArrayList<StoreNode>();
        if (currentPhysicalNodeMap == null || currentPhysicalNodeMap.isEmpty()) {
            logger.warn("Build config instance content, but the content might not be correct.");
        } else {
            for (PhysicalNodeDO node : currentPhysicalNodeMap.values()) {
                StoreNode storeNode = new StoreNode();

                storeNode.setURL(node.getIp() + ":" + node.getPort());
                storeNode.setIp(node.getIp());
                storeNode.setPort(node.getPort());
                storeNode.setLogicId(node.getLogicalId());
                storeNode.setPhId(node.getPhysicalId());
                storeNode.setSequence(StoreNodeSequenceEnum.getTypeByValue(node.getSerialId()));
                storeNode.setStatus(NodeRouteStatus.getTypeByValue(node.getStatus()));
                storeNodeList.add(storeNode);
            }
        }
        return JSON.toJSONString(storeNodeList, SerializerFeature.WriteEnumUsingToString);
    }

    private boolean isValidRoutableNode(PhysicalNodeDO latestNode) {
        if (latestNode == null)
            return false;

        NodeRouteStatus status = NodeRouteStatus.getTypeByValue(latestNode.getStatus());
        StoreNodeSequenceEnum sequence = StoreNodeSequenceEnum.getTypeByValue(latestNode
                .getSerialId());

        return isValidRoutableSequence(sequence) && isValidRoutableStatus(status);
    }

    private boolean isValidRoutableStatus(NodeRouteStatus status) {
        if (status == null) {
            return false;
        }

        return status == NodeRouteStatus.OK || status == NodeRouteStatus.TEMP_FAILED;
    }

    private boolean isValidRoutableSequence(StoreNodeSequenceEnum sequence) {
        if (sequence == null)
            return false;

        return (sequence != StoreNodeSequenceEnum.STANDBY_SEQUENCE)
                && (sequence != StoreNodeSequenceEnum.UNUSE_SEQUENCE);
    }

    private boolean isChanged(PhysicalNodeDO latestNode, PhysicalNodeDO currentNode) {
        // 任何一个属性不一样就认为有变更。 
        return !latestNode.getMachineId().equals(currentNode.getMachineId())
                //|| !latestNode.getGmtCreate().equals(currentNode.getGmtCreate())
                //|| !latestNode.getGmtModified().equals(currentNode.getGmtModified())
                || latestNode.getStatus() != currentNode.getStatus()
                || latestNode.getLogicalId() != currentNode.getLogicalId()
                || !latestNode.getPhysicalId().equals(currentNode.getPhysicalId())
                || latestNode.getSerialId() != currentNode.getSerialId()
                || !latestNode.getIp().equals(currentNode.getIp())
                || latestNode.getPort() != currentNode.getPort();
    }
    
    private boolean isDifferent(StoreNode sn1, StoreNode sn2) {
        // 任何一个属性不一样就认为有变更。 
        return (!sn1.getPhId().equals(sn2.getPhId()))
                || (sn1.getStatus() != sn2.getStatus())
                || (sn1.getLogicId() != sn2.getLogicId())
                || (sn1.getSequence() != sn2.getSequence())
                || (!sn1.getIp().equals(sn2.getIp()))
                || (sn1.getPort() != sn2.getPort());
    }

    public void start() {
    
        if(configLoadThread != null) {
            configLoadThread.start();
        }
        
    }

    public void stop() {
        this.close();
    }
}

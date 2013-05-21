package com.alibaba.doris.admin.service.common.node;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.service.AdminNodeService;
import com.alibaba.doris.admin.service.common.Managerable;
import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.ConnectionFactory;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.StoreNodeSequenceEnum;

/**
 * 集群所有节点的管理器，定时从DB载入全部Node<br>
 * 可以看作Node在内存的只读缓存
 * 
 * @author frank
 */
public class NodesManager implements Managerable {

    private static final Log            log                       = LogFactory
                                                                          .getLog(NodesManager.class);

    private Map<String, PhysicalNodeDO> nodeMap                   = new ConcurrentHashMap<String, PhysicalNodeDO>();
    private Map<String, StoreNode>      storeNodesMap             = new ConcurrentHashMap<String, StoreNode>();
    private static NodesManager         instance                  = new NodesManager();
    private AdminNodeService            nodeService               = AdminServiceLocator
                                                                          .getAdminNodeService();
    private Map<String, Connection>     nodeConnections           = new HashMap<String, Connection>();

    private Thread                      reloadThread              = null;

    private List<PhysicalNodeDO>        nodeList                  = null;

    private Object                      newAndCacheConnectionLock = new Object();

    private NodesManager() {
        reloadThread = new NodeReloadThread(this);
        reLoadNodes();
    }

    public static NodesManager getInstance() {
        return instance;
    }

    public Collection<PhysicalNodeDO> getAllNodeList() {
        List<PhysicalNodeDO> cloneNodes = new ArrayList<PhysicalNodeDO>();
        cloneNodes.addAll(nodeList);
        return cloneNodes;
    }

    public PhysicalNodeDO getNode(String physicalId) {
        return nodeMap.get(physicalId);
    }

    public List<String> getNodePhysicalIdListBySequence(StoreNodeSequenceEnum sequence) {
        List<String> physicalIdList = new ArrayList<String>();
        for (PhysicalNodeDO node : getAllNodeList()) {
            if (node.getSerialId() == sequence.getValue()) {
                physicalIdList.add(node.getPhysicalId());
            }
        }
        return physicalIdList;
    }

    public List<PhysicalNodeDO> getNodeListBySequence(StoreNodeSequenceEnum sequence) {
        List<PhysicalNodeDO> tempList = new ArrayList<PhysicalNodeDO>();
        for (PhysicalNodeDO node : getAllNodeList()) {
            if (node.getSerialId() == sequence.getValue()) {
                tempList.add(node);
            }
        }
        return tempList;
    }

    /**
     * 获得一个序列的最大逻辑id，如果序列为空，返回-1
     * 
     * @param sequence
     * @return
     */
    public int getLargestLogicId(StoreNodeSequenceEnum sequence) {
        int largestId = -1;
        for (PhysicalNodeDO node : getAllNodeList()) {
            if (node.getSerialId() == sequence.getValue()) {
                if (node.getLogicalId() > largestId) {
                    largestId = node.getLogicalId();
                }
            }
        }
        return largestId;
    }

    /**
     * 所有临时节点。
     * 
     * @return
     */
    public List<PhysicalNodeDO> getAllTempNodeList() {
        List<PhysicalNodeDO> physicalIdList = new ArrayList<PhysicalNodeDO>();
        for (PhysicalNodeDO node : getAllNodeList()) {
            if (node.getSerialId() == StoreNodeSequenceEnum.TEMP_SEQUENCE.getValue()) {
                physicalIdList.add(node);
            }
        }
        return physicalIdList;
    }

    /**
     * 获得一个永久失效节点的备用节点
     * 
     * @param physicalNodeId
     * @return
     */
    public PhysicalNodeDO getStandbyNodeId(String failPhysicalNodeId) {
        for (PhysicalNodeDO node : getAllNodeList()) {
            if (node.getSerialId() == StoreNodeSequenceEnum.STANDBY_SEQUENCE.getValue()) {
                int sequence = NodesManager.getInstance().getNode(failPhysicalNodeId).getSerialId();
                if (nodeService.checkLegalMigrateByPhysicalId(node.getPhysicalId(),
                        StoreNodeSequenceEnum.getTypeByValue(sequence))) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * 根据序列号和逻辑id查找节点
     * 
     * @param sequence
     * @param logicId
     * @return
     */
    public PhysicalNodeDO getNode(int sequence, int logicId) {
        for (PhysicalNodeDO node : getAllNodeList()) {
            if (node.getSerialId() == sequence && node.getLogicalId() == logicId) {
                return node;
            }
        }
        return null;
    }

    public String getLogFormatNodeId(String physicalId) {
        PhysicalNodeDO node = getNode(physicalId);
        if (node == null) {
            return null;
        }
        return node.getSerialId() + "." + node.getLogicalId();
    }

    public int getSequenceLenght(int sequence) {
        int length = 0;
        for (PhysicalNodeDO node : getAllNodeList()) {
            if (node.getSerialId() == sequence) {
                length++;
            }
        }
        return length;
    }

    /**
     * 从数据库载入所有节点
     */
    public synchronized void reLoadNodes() {
        List<PhysicalNodeDO> nodeList = nodeService.queryAllPhysicalNodes();
        this.nodeList = nodeList;
        this.nodeMap = l2m(nodeList);
        this.storeNodesMap = refreshStoreNodes(nodeList);
    }

    /**
     * 用输入的参数载入节点
     * 
     * @param nodeMap
     */
    public synchronized void reLoadNodes(List<PhysicalNodeDO> nodeList) {
        this.nodeList = nodeList;
        this.nodeMap = l2m(nodeList);
        this.storeNodesMap = refreshStoreNodes(nodeList);
    }

    private Map<String, StoreNode> refreshStoreNodes(List<PhysicalNodeDO> nodeList) {
        Map<String, StoreNode> tempNodeMap = new ConcurrentHashMap<String, StoreNode>();
        for (PhysicalNodeDO pNode : nodeList) {
            StoreNode sn = NodeHelper.buildStoreNode(pNode);
            tempNodeMap.put(pNode.getPhysicalId(), sn);
        }
        return tempNodeMap;
    }

    private Map<String, PhysicalNodeDO> l2m(List<PhysicalNodeDO> nodeList) {

        Map<String, PhysicalNodeDO> tempNodeMap = new ConcurrentHashMap<String, PhysicalNodeDO>();
        if (nodeList == null) {
            if (log.isDebugEnabled()) {
                log.debug("no nodes found in DB...");
            }
            return tempNodeMap;
        }

        if (log.isDebugEnabled()) {
            log.debug("the nodes' number from db:" + nodeList.size());
        }
        for (PhysicalNodeDO node : nodeList) {
            tempNodeMap.put(node.getPhysicalId(), node);
        }
        return tempNodeMap;
    }

    public StoreNode getStoreNode(String physicalId) {
        PhysicalNodeDO pNode = this.getNode(physicalId);
        if (pNode == null) {
            return null;
        }
        return storeNodesMap.get(pNode.getPhysicalId());
    }

    public StoreNode getStoreNode(PhysicalNodeDO pNode) {
        return storeNodesMap.get(pNode.getPhysicalId());
    }

    /**
     * One new connection will be established and cached when first time access,
     * otherwise, the cached connection returns. If the cached connection is not
     * connected, and one new connection will be established and cached.
     * 
     * @param physicalId the physical node id for one data server node.
     */
    public Connection getNodeConnection(String physicalId) {
        StoreNode storeNode = this.getStoreNode(physicalId);
        return getNodeConnection(storeNode, false);
    }

    /**
     * <p>
     * One new connection will be established and cached when first time access,
     * otherwise, the cached connection returns. If the cached connection is not
     * connected, and one new connection will be established and cached.
     * </p>
     * <p>
     * Please note, the connection must be closed if not use again when
     * <code>bNewConnection</code> is true. Recommand to set
     * <code>bNewConnection</code> as false, then {@code NodesManager} will
     * manager the connection;
     * </p>
     * 
     * @param physicalId the physical node id for one data server node.
     * @param bNewConnection One new connection will be established always if
     *            <code></code> is true. Otherwise, The behavior is same as
     *            {@link #getNodeConnection(String)}.
     * @see #getNodeConnection(String)
     */
    public Connection getNodeConnection(String physicalId, boolean bNewConnection) {
        StoreNode storeNode = this.getStoreNode(physicalId);
        return getNodeConnection(storeNode, bNewConnection);
    }

    /**
     * One new connection will be established and cached when first time access,
     * otherwise, the cached connection returns. If the cached connection is not
     * connected, and one new connection will be established and cached.
     * 
     * @param node the store node instance.
     */
    public Connection getNodeConnection(StoreNode node) {
        return getNodeConnection(node, false);
    }

    /**
     * <p>
     * One new connection will be established and cached when first time access,
     * otherwise, the cached connection returns. If the cached connection is not
     * connected, and one new connection will be established and cached.
     * </p>
     * <p>
     * Please note, the connection must be closed if not use again when
     * <code>bNewConnection</code> is true. Recommand to set
     * <code>bNewConnection</code> as false, then {@code NodesManager} will
     * manager the connection;
     * </p>
     * 
     * @param physicalId the physical node id for one data server node.
     * @param bNewConnection One new connection will be established always if
     *            <code></code> is true. Otherwise, The behavior is same as
     *            {@link #getNodeConnection(StoreNode)}
     * @see #getNodeConnection(StoreNode)
     */
    public Connection getNodeConnection(StoreNode storeNode, boolean bNewConnection) {
        if (bNewConnection) {
            return newAndOpenConnection(storeNode);
        } else {
            return getAndCacheConnection(storeNode);
        }
    }

    private void cacheConnection(String physicalId, Connection conn) {
        nodeConnections.put(physicalId, conn);
    }

    private Connection newAndOpenConnection(StoreNode storeNode) {
        Connection conn;
        InetSocketAddress inetSocketAddress = new InetSocketAddress(storeNode.getIp(), storeNode
                .getPort());
        conn = ConnectionFactory.getInstance().getConnection(inetSocketAddress);
        conn.open();
        return conn;
    }

    private Connection getAndCacheConnection(StoreNode storeNode) {
        Connection conn = nodeConnections.get(storeNode.getPhId());
        if (conn == null) {
            synchronized (newAndCacheConnectionLock) {
                conn = newAndOpenConnection(storeNode);
                cacheConnection(storeNode.getPhId(), conn);
            }
        } else if (conn != null && !conn.isConnected()) {
            conn.close();
            if (log.isInfoEnabled()) {
                log.info("old connection is closed:" + conn.toString());
            }

            // create new connection and cache it.
            synchronized (newAndCacheConnectionLock) {
                conn = newAndOpenConnection(storeNode);
                cacheConnection(storeNode.getPhId(), conn);
            }
            if (log.isInfoEnabled()) {
                log.info("new connection is closed:" + conn.toString());
            }
        }
        return conn;
    }

    public void start() {
        reloadThread.start();
    }

    public void stop() {
        //关闭所有连接
        for (Connection conn : nodeConnections.values()) {
            conn.close();
        }
        
        //关闭reload线程
        reloadThread.interrupt();
    }
}

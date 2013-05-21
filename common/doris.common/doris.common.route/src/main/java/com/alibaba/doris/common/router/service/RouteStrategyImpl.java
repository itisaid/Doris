package com.alibaba.doris.common.router.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.algorithm.RouteAlgorithm;
import com.alibaba.doris.algorithm.util.RandomNumUtil;
import com.alibaba.doris.algorithm.vpm.VpmRouterAlgorithm;
import com.alibaba.doris.common.NodeRouteStatus;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.StoreNodeSequenceEnum;
import com.alibaba.doris.common.adminservice.AdminServiceFactory;
import com.alibaba.doris.common.config.ConfigManager;
import com.alibaba.doris.common.event.RouteConfigChangeEvent;
import com.alibaba.doris.common.operation.OperationEnum;
import com.alibaba.doris.common.route.DorisRouterException;
import com.alibaba.doris.common.route.RouteStrategy;
import com.alibaba.doris.common.route.RouteTable;

/**
 * doris路由算法实现
 * 
 * @author frank
 */
public class RouteStrategyImpl implements RouteStrategy {

    private static final Log log        = LogFactory.getLog(RouteStrategyImpl.class);
    RouterListContainer      rlc        = new RouterListContainer();                                       // 路由相关对象的容器，这些对象需要同时更新，保证一致

    private RouteTable       routeTable;

    private ConfigManager    configManager;

    private int              virtualNum = AdminServiceFactory.getVirtualNumberService().getVirtualNumber();

    public void setRouteTable(RouteTable routeTable) {
        this.routeTable = routeTable;
    }

    public RouteTable getRouteTable() {
        return routeTable;
    }

    public List<StoreNode> findNodes(OperationEnum type, int copyCount, String key) throws DorisRouterException {
        if (rlc.getVpmrList() == null || rlc.getVpmrList().isEmpty()) {
            log.error("Current RouterListContainer is:" + rlc);
            throw new DorisRouterException("There is no store node.");
        }
        if (copyCount > rlc.getVpmrList().size()) {
            log.error("Current RouterListContainer is:" + rlc);
            throw new DorisRouterException("There is only " + rlc.getVpmrList().size() + " sequence, Can't support "
                                           + copyCount + " copy count!");
        }
        List<StoreNode> snList = new ArrayList<StoreNode>();

        for (int i = 0; i < copyCount; i++) {
            if (type.equals(OperationEnum.READ) || type.equals(OperationEnum.WRITE)
                || type.equals(OperationEnum.MULTIREAD)) {// 写操作一定要取得一个node，读操作只有在可读时才取node
                int logicId = rlc.getVpmrList().get(i).getNodeByKey(key);

                List<List<StoreNode>> mainNodeList = rlc.getMainStoreNodeList();
                List<StoreNode> seqNodeList = mainNodeList.get(i);
                StoreNode sn = seqNodeList.get(logicId);
                snList.add(sn);

            }
        }

        if (snList.isEmpty()) {
            throw new DorisRouterException("No store node can be used!");
        }

        // //////////////////////////////////////////////////////////TODO for debug
        // List<StoreNode> tl = new ArrayList<StoreNode>();
        // for (StoreNode tsn : snList) {
        // tl.add(tsn);
        // }
        // //////////////////////////////////////////////////////

        anylizeNode(type, snList, key);

        if (snList.isEmpty()) {
            // log.error("No store node for operation:" + tl + " become:" + snList);
            throw new DorisRouterException("No store node for operation");
        }
        if (type.equals(OperationEnum.WRITE) && snList.size() < copyCount) {
            // log.error("No enough store node for write:" + tl + " become:" + snList);
            throw new DorisRouterException("No enough store node for write");
        }

        if (OperationEnum.MULTIREAD.equals(type)) {
            return snList;
        }

        // if read ,then find one node
        if (OperationEnum.READ.equals(type)) {
            StoreNode sn = snList.get(getHashIndex(snList.size(), key));
            List<StoreNode> tempList = new ArrayList<StoreNode>(1);
            tempList.add(sn);
            return tempList;
        }

        int size = snList.size();
        // if write, must not be all backup nodes,
        if (OperationEnum.WRITE.equals(type)) {
            boolean backTag = true;
            List<StoreNode> tempList = new ArrayList<StoreNode>(size);
            // 保证节点在访问时，同一个key会以相同的序列来访问所有node；访问顺序必须同read的顺序一致；
            // 以便保证写操作的原子性；
            for (int i = 0; i < size; i++) {
                int index = getHashIndex(size - i, key);
                StoreNode sn = snList.get(index);
                snList.remove(index);
                if (!sn.getSequence().equals(StoreNodeSequenceEnum.TEMP_SEQUENCE)) {// 只要有一个节点不是备用节点
                    backTag = false;
                }
                tempList.add(sn);
            }
            if (backTag) {
                throw new DorisRouterException("All nodes is backup node.");
            }
            return tempList;
        }
        return snList;
    }

    public void onConfigChange(RouteConfigChangeEvent event) {

        buildConfig(event.getRouteTable().getMainStoreNodeList(), event.getRouteTable().getBackupStoreNodeList());
    }

    private void buildConfig(List<List<StoreNode>> mainStoreNodeList, List<StoreNode> backupStoreNodeList) {
        if (mainStoreNodeList == null || mainStoreNodeList.isEmpty()) {
            if (log.isErrorEnabled()) {
                log.error("normal sequence is null.");
            }
            return;
        }
        for (List<StoreNode> snList : mainStoreNodeList) {
            if (snList == null || snList.isEmpty()) {
                if (log.isErrorEnabled()) {
                    log.error("normal sequence is null.");
                }
                return;
            }
        }
        List<RouteAlgorithm> tempList = new ArrayList<RouteAlgorithm>();

        // 对正常序列进行排序，这个顺序用于路由版本更新时请求重定向。
        for (int i = 1; i < mainStoreNodeList.size(); i++) {
            for (int j = i; j < mainStoreNodeList.size(); j++) {
                if (mainStoreNodeList.get(j).get(0).getSequence().getValue() < mainStoreNodeList.get(j - 1).get(0).getSequence().getValue()) {
                    List<StoreNode> ls = mainStoreNodeList.get(j - 1);
                    mainStoreNodeList.set(j - 1, mainStoreNodeList.get(j));
                    mainStoreNodeList.set(j, ls);
                }
            }
        }

        for (List<StoreNode> list : mainStoreNodeList) {
            RouteAlgorithm vpmr = new VpmRouterAlgorithm(list.size(), virtualNum);
            tempList.add(vpmr);
        }

        // 这几个List要同时完成变更，需要一个容器包装
        RouterListContainer tc = new RouterListContainer();

        if (backupStoreNodeList != null && !backupStoreNodeList.isEmpty()) {
            RouteAlgorithm tempVpmr = new VpmRouterAlgorithm(backupStoreNodeList.size(), virtualNum);
            tc.setBackupVpmr(tempVpmr);
            tc.setBackupStoreNodeList(backupStoreNodeList);

        }

        tc.setVpmrList(tempList);
        tc.setMainStoreNodeList(mainStoreNodeList);

        // log.error("old: " + rlc);
        rlc = tc;
        // log.error("new: " + rlc);

    }

    /**
     * client各种情况的路由策略
     * 
     * @param type
     * @param snList
     * @param key
     * @throws DorisRouterException
     */
    private void anylizeNode(OperationEnum type, List<StoreNode> snList, String key) throws DorisRouterException {

        if (type.equals(OperationEnum.READ) || type.equals(OperationEnum.MULTIREAD)) {
            Iterator<StoreNode> i = snList.iterator();
            while (i.hasNext()) {
                NodeRouteStatus status = i.next().getStatus();
                if (status.equals(NodeRouteStatus.TEMP_FAILED)) {
                    i.remove();
                }
            }
        }

        if (type.equals(OperationEnum.WRITE)) {
            for (int i = 0; i < snList.size(); i++) {
                NodeRouteStatus status = snList.get(i).getStatus();
                if (status.equals(NodeRouteStatus.TEMP_FAILED)) {
                    // 用备份节点写
                    snList.set(i, findBackupNode(key));
                }
            }
        }

    }

    /**
     * 查找失效节点的替代节点,调用前该节点经过admin仲裁为临时失效
     * 
     * @param type
     * @param copyCount
     * @param key
     * @param sn 失效节点
     * @return
     * @throws DorisRouterException
     */

    public StoreNode findFailoverNode(OperationEnum type, int copyCount, String key, StoreNode sn)
                                                                                                  throws DorisRouterException {

        // log.error(sn + " is failed by admin check.");
        sn.setStatus(NodeRouteStatus.TEMP_FAILED);// 一定是临时失效，这个修改是client的临时修改，在新的配置实例生效前使用

        List<StoreNode> snList = findNodes(type, copyCount, key);// 重新获取节点列表，因为刚修改过一个节点状态，这里可能因为都是备用节点而导致写操作路由异常。

        if (snList == null || snList.isEmpty()) {
            log.error("Current RouterListContainer is:" + rlc);
            throw new DorisRouterException("can't find failover node.");
        }

        // 读操作,重新获取读节点
        if (type.equals(OperationEnum.READ) || type.equals(OperationEnum.MULTIREAD)) {
            return snList.get(getHashIndex(snList.size(), key));
        }

        // 写操作，查找备用节点
        if (type.equals(OperationEnum.WRITE)) {
            return findBackupNode(key);
        }

        log.error("not support this type of operation:" + type.name());
        throw new DorisRouterException("not support this type of operation:" + type.name());
    }

    /**
     * @deprecated
     * @param sn
     * @param key
     * @return
     * @throws DorisRouterException
     */
    private StoreNode findBackupNode(StoreNode sn, String key) throws DorisRouterException {

        // 该失效节点本身是备用节点
        if (sn.getSequence().equals(StoreNodeSequenceEnum.TEMP_SEQUENCE)) {
            return findNextStoreNode(rlc.getBackupStoreNodeList(), sn);
        }

        // 该失效节点是正常节点，返回备用节点
        if (sn.getSequence().equals(StoreNodeSequenceEnum.TEMP_SEQUENCE)) {
            return findBackupNode(key);
        }

        return null;
    }

    private int getRandomIndex(int size) throws DorisRouterException {
        if (size <= 0) {
            log.error("Current RouterListContainer is:" + rlc);
            throw new DorisRouterException("Fatal error! size is " + size);
        }

        return RandomNumUtil.getRandomNumMath() % size;
    }

    /**
     * 确保指定的key，每次都会路由到相同的节点；<br>
     * 对于写多份的场景，也每次都以相同的顺序写入多个节点；
     * 
     * @param size
     * @param key
     * @return
     */
    public static int getHashIndex(int size, String key) throws DorisRouterException {
        if (size <= 0) {
            // log.error("Current RouterListContainer is:" + rlc);
            throw new DorisRouterException("Fatal error! size is " + size);
        }

        int hash = key.hashCode() % size;
        return hash < 0 ? -hash : hash;
    }

    /**
     * 在节点列表中查找失效节点的下一个节点
     * 
     * @deprecated
     * @param snList
     * @param sn
     * @return
     */
    private StoreNode findNextStoreNode(List<StoreNode> snList, StoreNode sn) throws DorisRouterException {
        for (int i = 0; i < snList.size(); i++) {
            StoreNode snt = snList.get(i);
            if (snt.equals(sn)) {// 找到失效节点
                if (snList.size() == 1) {
                    log.error("Current RouterListContainer is:" + rlc);
                    throw new DorisRouterException("no store node can be read!");// 列表只有一个节点，且是当前失效节点，再无可用节点
                }

                // 把节点列表当作一个环，获得失效节点的下一个节点
                StoreNode snr = null;
                if (i < (snList.size() - 1)) {
                    snr = snList.get(i + 1);//
                } else {
                    snr = snList.get(0);
                }
                return snr;
            }
        }
        // 节点列表不包含失效节点，路由表发生变化，返回新路由表随机节点
        return snList.get(getRandomIndex(snList.size()));
    }

    /**
     * 查找备用节点<br>
     * 需要处理备用节点失效的情况，返回下一OK节点<br>
     * 当对等序列在两个以上时，同时失效节点在一个以上时，临时节点存在失效节点时，多个失效节点数据可能会写在一个临时节点上，暂不考虑
     * 
     * @param key
     * @return
     */
    private StoreNode findBackupNode(String key) throws DorisRouterException {
        if (rlc.getBackupStoreNodeList() == null || rlc.getBackupVpmr() == null
            || rlc.getBackupStoreNodeList().isEmpty()) {
            log.error("Current RouterListContainer is:" + rlc);
            throw new DorisRouterException("rlc's temp node is null. can't find useful backup node.");
        }
        int k = rlc.getBackupVpmr().getNodeByKey(key);
        StoreNode sn = rlc.getBackupStoreNodeList().get(k);
        if (sn.getStatus().equals(NodeRouteStatus.OK)) {
            return sn;
        }
        int x = rlc.getBackupStoreNodeList().size();
        for (int i = 0; i < x - 1; i++) {
            if (k < x - 1) {
                k++;
            } else {
                k = 0;
            }
            sn = rlc.getBackupStoreNodeList().get(k);
            if (sn.getStatus().equals(NodeRouteStatus.OK)) {
                return sn;
            }
        }
        log.error("Current backup list is:" + rlc.getBackupStoreNodeList());
        for (int i = 0; i < rlc.getBackupStoreNodeList().size(); i++) {
            log.error("all backup nodes were failed:" + sn + ":" + rlc.getBackupStoreNodeList().get(i).getStatus());
        }
        throw new DorisRouterException("at last. can't find useful backup node." + sn.getStatus());
    }

    public void setRouteAlgorithm(RouteAlgorithm routeAlgorithm) {
        // TODO: use configed routeAlgorithm from configManager.getProperties()

    }

    public void initConfig() {

    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

}

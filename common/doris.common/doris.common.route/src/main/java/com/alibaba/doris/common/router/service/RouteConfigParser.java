package com.alibaba.doris.common.router.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.common.RouteConfigInstance;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.StoreNodeSequenceEnum;
import com.alibaba.doris.common.route.RouteTable;
import com.alibaba.doris.common.route.RouteTableImpl;
import com.alibaba.fastjson.JSON;

public class RouteConfigParser // implements ConfigListener
{

    Log log = LogFactory.getLog(RouteConfigParser.class);

    //
    // /**
    // * @param configInstanceContent:单个Node内的字段信息以<code>(char)2</code>间隔，Node与Node间的信息以<code>(char)3</code>间隔 ;
    // * 请一定保证字段的顺序，依次为Node节点的URL标识,所属物理机的ID,Node节点所属的GroupID标识,Node节点的逻辑标识ID,Node节点的状态信息, eg:
    // *
    // *
    // <code>URL1+(char)2+phId1+(char)2+groupId1+(char)2+logicId1+(char)2+status1+(char)3+URL2+(char)2+phId2+(char)2+groupId2+(char)2+logicId2+(char)2+status2</code>
    // */
    // public void changeConfig(String configInstanceContent) {
    // parse(configInstanceContent);
    //
    // for (List<StoreNode> nodelist : mainStoreNodeList) {
    // for (StoreNode node : nodelist) {
    // System.out.println("mainStoreNodeList>>>" + node.getSequence().getValue() + ">>" + node.getLogicId()
    // + ">>" + node.getStatus().getValue());
    // }
    // System.out.println("<<<<<<<<<<<<<<mainStoreNodeList>>>>>>>>>>>>>>>>>>>>>");
    // }
    // for (StoreNode node : backupStoreNodeList) {
    // System.out.println("backupStoreNodeList>>>" + node.getSequence().getValue() + ">>" + node.getLogicId()
    // + ">>" + node.getStatus().getValue());
    // }
    // System.out.println("<<<<<<<<<<<<<<backupStoreNodeList>>>>>>>>>>>>>>>>>>>>>");
    // System.out.println("\r\n\r\n\r\n\r\n");
    //
    // notifyListener();
    // }

    /**
     * <code>URL1+(char)2+phId1+(char)2+groupId1+(char)2+logicId1+(char)2+status1+(char)3+URL2+(char)2+phId2+(char)2+groupId2+(char)2+logicId2+(char)2+status2</code>
     * configInstanceContent is defined as json style.
     * 
     * @author len.liu|2011-4-27 下午06:58:52
     * @throws Exception
     */
    public static RouteTable parse(String configInstanceContent) {

        if (configInstanceContent == null || configInstanceContent.trim().length() == 0)
            return null;

        RouteTable routeTable = new RouteTableImpl();
        RouteConfigInstance routeConfig = JSON.parseObject(configInstanceContent, RouteConfigInstance.class);
        
        routeTable.setVersion(routeConfig.getVersion());
        
        List<List<StoreNode>> mainStoreNodeList = new ArrayList<List<StoreNode>>();
        List<StoreNode> backupStoreNodeList = new ArrayList<StoreNode>();

        // XXME 是否备份最近一次的路由配置
        mainStoreNodeList.clear();
        backupStoreNodeList.clear();
        Map<StoreNodeSequenceEnum, List<StoreNode>> groupMap = new HashMap<StoreNodeSequenceEnum, List<StoreNode>>();

        for (StoreNode node : routeConfig.getStoreNodes()) {
            if (StoreNodeSequenceEnum.TEMP_SEQUENCE.equals(node.getSequence())) {
                backupStoreNodeList.add(node);
            } else if (groupMap.containsKey(node.getSequence())) {
                groupMap.get(node.getSequence()).add(node);
            } else {
                List<StoreNode> mainStoreList = new ArrayList<StoreNode>();
                mainStoreList.add(node);
                groupMap.put(node.getSequence(), mainStoreList);
            }
        }

        Comparator<StoreNode> nodeComparator = new Comparator<StoreNode>() {

            public int compare(StoreNode node1, StoreNode node2) {

                return node1.getLogicId() - node2.getLogicId();

            }
        };

        for (StoreNodeSequenceEnum sequence : groupMap.keySet()) {
            Collections.sort(groupMap.get(sequence), nodeComparator);
            mainStoreNodeList.add(groupMap.get(sequence));
        }

        Comparator<List<StoreNode>> nodeListComparator = new Comparator<List<StoreNode>>() {

            public int compare(List<StoreNode> nodeList1, List<StoreNode> nodeList2) {

                return nodeList1.get(0).getSequence().getValue() - nodeList2.get(0).getSequence().getValue();

            }
        };

        Collections.sort(backupStoreNodeList, nodeComparator);
        Collections.sort(mainStoreNodeList, nodeListComparator);
        routeTable.setBackupStoreNodeList(backupStoreNodeList);
        routeTable.setMainStoreNodeList(mainStoreNodeList);
        return routeTable;
    }
}

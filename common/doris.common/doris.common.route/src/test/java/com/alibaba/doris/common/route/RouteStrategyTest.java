package com.alibaba.doris.common.route;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import com.alibaba.doris.common.NodeRouteStatus;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.StoreNodeSequenceEnum;
import com.alibaba.doris.common.event.RouteConfigChangeEvent;
import com.alibaba.doris.common.operation.OperationEnum;
import com.alibaba.doris.common.router.service.RouteStrategyImpl;

public class RouteStrategyTest extends TestCase{

    /**
     * @param args
     * @throws DorisRouterException 
     */
    @Test
    public void test() throws DorisRouterException {
//      XXU reopen this case later
//        /************************ backupStoreNodeList *******************************/
//        List<StoreNode> backupStoreNodeList = new ArrayList<StoreNode>();
//
//        StoreNode snBackup1 = new StoreNode();
//        snBackup1.setLogicId(0);
//        snBackup1.setSequence(StoreNodeSequenceEnum.TEMP_SEQUENCE);
//        snBackup1.setStatus(NodeRouteStatus.OK);
//
//        StoreNode snBackup2 = new StoreNode();
//        snBackup2.setLogicId(1);
//        snBackup2.setSequence(StoreNodeSequenceEnum.TEMP_SEQUENCE);
//        snBackup2.setStatus(NodeRouteStatus.OK);
//
//        backupStoreNodeList.add(snBackup1);
//        backupStoreNodeList.add(snBackup2);
//        /*************************** backupStoreNodeList ****************************/
//
//        /********************* mainStoreNodeList *********************/
//        List<List<StoreNode>> mainStoreNodeList = new ArrayList<List<StoreNode>>();
//
//        List<StoreNode> mainNodeList1 = new ArrayList<StoreNode>();
//
//        StoreNode main11 = new StoreNode();
//        main11.setLogicId(0);
//        main11.setSequence(StoreNodeSequenceEnum.NORMAL_SEQUENCE_1);
//        main11.setStatus(NodeRouteStatus.OK);
//
//        StoreNode main12 = new StoreNode();
//        main12.setLogicId(1);
//        main12.setSequence(StoreNodeSequenceEnum.NORMAL_SEQUENCE_1);
//        main12.setStatus(NodeRouteStatus.OK);
//
//        mainNodeList1.add(main11);
//        mainNodeList1.add(main12);
//        mainStoreNodeList.add(mainNodeList1);
//
//        List<StoreNode> mainNodeList2 = new ArrayList<StoreNode>();
//
//        StoreNode main21 = new StoreNode();
//        main21.setLogicId(0);
//        main21.setSequence(StoreNodeSequenceEnum.NORMAL_SEQUENCE_2);
//        main21.setStatus(NodeRouteStatus.OK);
//
//        StoreNode main22 = new StoreNode();
//        main22.setLogicId(1);
//        main22.setSequence(StoreNodeSequenceEnum.NORMAL_SEQUENCE_2);
//        main22.setStatus(NodeRouteStatus.OK);
//
//        mainNodeList2.add(main21);
//        mainNodeList2.add(main22);
//        mainStoreNodeList.add(mainNodeList2);
//        /*********************** mainStoreNodeList *******************/
//
//        RouteConfigChangeEvent event = new RouteConfigChangeEvent();
//        RouteTableImpl rt = new RouteTableImpl();
//        rt.setBackupStoreNodeList(backupStoreNodeList);
//        rt.setMainStoreNodeList(mainStoreNodeList);
//        event.setRouteTable(rt);
//        RouteStrategyImpl rs = new RouteStrategyImpl();
//        rs.onConfigChange(event);
//
//        List<StoreNode> sn = rs.findNodes(OperationEnum.READ, 2, "tt");
//        StoreNode snr = rs.findFailoverNode(OperationEnum.READ, 2, "tt",sn.get(0));
//        System.out.println(sn);
//        System.out.println(snr);
    }

}

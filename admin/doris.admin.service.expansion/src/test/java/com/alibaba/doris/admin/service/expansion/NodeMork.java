package com.alibaba.doris.admin.service.expansion;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.service.common.node.NodesManager;
import com.alibaba.doris.common.NodeRouteStatus;
import com.alibaba.doris.common.StoreNodeSequenceEnum;

public class NodeMork {

    public static void morkNodes() {
        List<PhysicalNodeDO> nodeList = new ArrayList<PhysicalNodeDO>();

        for (int i = 0; i < 5; i++) {
            PhysicalNodeDO node = new PhysicalNodeDO();
            node.setPhysicalId("normal1." + i);
            node.setLogicalId(i);
            node.setSerialId(StoreNodeSequenceEnum.NORMAL_SEQUENCE_1.getValue());
            node.setStatus(NodeRouteStatus.OK.getValue());
            nodeList.add(node);
        }
        
        for (int i = 0; i < 5; i++) {
            PhysicalNodeDO node = new PhysicalNodeDO();
            node.setPhysicalId("normal2." + i);
            node.setLogicalId(i);
            node.setSerialId(StoreNodeSequenceEnum.NORMAL_SEQUENCE_2.getValue());
            node.setStatus(NodeRouteStatus.OK.getValue());
            nodeList.add(node);
        }

        for (int i = 0; i < 3; i++) {
            PhysicalNodeDO node = new PhysicalNodeDO();
            node.setPhysicalId("temp" + i);
            node.setLogicalId(i);
            node.setSerialId(StoreNodeSequenceEnum.TEMP_SEQUENCE.getValue());
            node.setStatus(NodeRouteStatus.OK.getValue());
            nodeList.add(node);
        }

        for (int i = 0; i < 2; i++) {
            PhysicalNodeDO node = new PhysicalNodeDO();
            node.setPhysicalId("standby" + i);
            node.setLogicalId(i);
            node.setSerialId(StoreNodeSequenceEnum.STANDBY_SEQUENCE.getValue());
            node.setStatus(NodeRouteStatus.OK.getValue());
            nodeList.add(node);
        }
        
        for (int i = 0; i < 2; i++) {
            PhysicalNodeDO node = new PhysicalNodeDO();
            node.setPhysicalId("unuse" + i);
            node.setLogicalId(i);
            node.setSerialId(StoreNodeSequenceEnum.UNUSE_SEQUENCE.getValue());
            node.setStatus(NodeRouteStatus.OK.getValue());
            nodeList.add(node);
        }

        NodesManager.getInstance().reLoadNodes(nodeList);
    }

    public static List<String> getNewPhysicalIdList() {
        List<String> newList = new ArrayList<String>();
        for (PhysicalNodeDO node : NodesManager.getInstance().getAllNodeList()) {
            if (node.getSerialId() == StoreNodeSequenceEnum.UNUSE_SEQUENCE.getValue()) {
                newList.add(node.getPhysicalId());
            }
        }
        return newList;
    }
}

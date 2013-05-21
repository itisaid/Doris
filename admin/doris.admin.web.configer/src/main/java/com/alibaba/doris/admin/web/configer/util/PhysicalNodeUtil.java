package com.alibaba.doris.admin.web.configer.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.service.expansion.processor.ExpansionMigrateProcessor;
import com.alibaba.doris.admin.service.failover.node.check.NodeAdminStatusWrapper;
import com.alibaba.doris.admin.service.failover.node.check.NodeStatusManager;
import com.alibaba.doris.admin.web.configer.support.NodeForView;
import com.alibaba.doris.admin.web.configer.support.SequenceForView;
import com.alibaba.doris.common.MigrateStatusEnum;
import com.alibaba.doris.common.NodeRouteStatus;
import com.alibaba.doris.common.StoreNodeSequenceEnum;

public class PhysicalNodeUtil {

    public static Map<Integer, SequenceForView> getSortedNodeMap(List<PhysicalNodeDO> physicalNodeList) {
        Map<Integer, SequenceForView> nodemap = new LinkedHashMap<Integer, SequenceForView>();
        Comparator<PhysicalNodeDO> nodeComparator = new Comparator<PhysicalNodeDO>() {

            public int compare(PhysicalNodeDO node1, PhysicalNodeDO node2) {
                if (node1.getSerialId() < 0) return 1;
                if (node2.getSerialId() < 0) return -1;
                if (node1.getSerialId() == 0 && node2.getSerialId() > 0) return 1;
                if (node1.getSerialId() > 0 && node2.getSerialId() == 0) return -1;
                return node1.getSerialId() - node2.getSerialId() == 0 ? node1.getLogicalId() - node2.getLogicalId() : node1.getSerialId()
                                                                                                                      - node2.getSerialId();

            }
        };
        Collections.sort(physicalNodeList, nodeComparator);

        for (PhysicalNodeDO physicalNodeDO : physicalNodeList) {
            int storeNodeSequence = physicalNodeDO.getSerialId();
            SequenceForView sequenceForView = nodemap.get(storeNodeSequence);
            boolean isSequenceMigrating = ExpansionMigrateProcessor.getInstance().isMigrating(
                                                                                              StoreNodeSequenceEnum.getTypeByValue(storeNodeSequence));

            if (sequenceForView == null) {
                sequenceForView = new SequenceForView();
            }
            sequenceForView.setSequenceMigrating(isSequenceMigrating);
            List<NodeForView> servicelist = sequenceForView.getNodeViewList();
            NodeForView view = new NodeForView();
            view.setPhysicalNodeDO(physicalNodeDO);

            NodeAdminStatusWrapper statusWrapper = NodeStatusManager.getInstance().getNodeAdminStatus(
                                                                                                      view.getPhysicalId());

            view.setHealthStatus(statusWrapper.getNodeHealth().toString());
            MigrateStatusEnum migrateStatus = statusWrapper.getMigrateStatus();
            view.setMigrationStatus(migrateStatus == null ? null : migrateStatus.getValue());
            view.setMigrationStatusDetail(statusWrapper.getMigrateStatusDetail());
            view.setMigrateProgress(statusWrapper.getMigrateProgress());
            view.setRouteStatus(NodeRouteStatus.OK.equals(statusWrapper.getNodeRouteStatus()) ? "OK" : "Temp Failed");
            if (servicelist == null) {
                List<NodeForView> nodeList = new ArrayList<NodeForView>();
                nodeList.add(view);
                sequenceForView.setNodeViewList(nodeList);
                nodemap.put(physicalNodeDO.getSerialId(), sequenceForView);
            } else {
                servicelist.add(view);
            }
        }
        return nodemap;
    }

    public static boolean isAllNodesOk(List<StoreNodeSequenceEnum> exceptSequences) {
        NodeStatusManager nodeStatusManager = NodeStatusManager.getInstance();
        List<NodeAdminStatusWrapper> nodesStatus = nodeStatusManager.getAllNodeAdminStatus();
        if (nodesStatus == null || nodesStatus.size() == 0) return false;

        for (NodeAdminStatusWrapper wrapper : nodesStatus) {
            if (wrapper.getNodeRouteStatus().getValue() != NodeRouteStatus.OK.getValue()
                && !exceptSequences.contains(wrapper.getStoreNode().getSequence())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isLegalMigrateNodes(String[] newNodes) {
        Map<String, String> checkMap = new HashMap<String, String>();
        for (String node : newNodes) {
            if (StringUtils.isBlank(node)) continue;
            String[] item = node.split("#");
            if (NumberUtils.isDigits(item[1])) {
                if (!checkMap.containsKey(item[2])) {
                    checkMap.put(item[2], item[1]);
                } else {
                    if (!checkMap.get(item[2]).equals(item[1])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}

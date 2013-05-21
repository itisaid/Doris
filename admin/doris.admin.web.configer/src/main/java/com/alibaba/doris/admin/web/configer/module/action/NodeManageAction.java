/**
 * Project: doris.admin.web.configer-0.1.0-SNAPSHOT File Created at 2011-5-20 $Id$ Copyright 1999-2100 Alibaba.com
 * Corporation Limited. All rights reserved. This software is the confidential and proprietary information of Alibaba
 * Company. ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.doris.admin.web.configer.module.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.service.AdminNodeService;
import com.alibaba.doris.admin.service.common.AdminServiceException;
import com.alibaba.doris.admin.service.common.node.NodesManager;
import com.alibaba.doris.admin.service.expansion.processor.ExpansionMigrateProcessor;
import com.alibaba.doris.admin.service.failover.processor.FailoverProcessor;
import com.alibaba.doris.admin.service.failover.processor.ForeverFailoverProcessor;
import com.alibaba.doris.admin.service.failover.processor.TempFailoverProcessor;
import com.alibaba.doris.admin.web.configer.util.PhysicalNodeUtil;
import com.alibaba.doris.admin.web.configer.util.WebConstant;
import com.alibaba.doris.common.StoreNodeSequenceEnum;

public class NodeManageAction {

    private static final Log  log                       = LogFactory.getLog(NodeManageAction.class);
    ExpansionMigrateProcessor expansionMigrateProcessor = ExpansionMigrateProcessor.getInstance();
    FailoverProcessor         foreverFailoverProcessor  = ForeverFailoverProcessor.getInstance();
    FailoverProcessor         tempFailoverProcessor     = TempFailoverProcessor.getInstance();

    NodesManager              nodesManager              = NodesManager.getInstance();
    AdminNodeService          adminNodeService          = AdminServiceLocator.getAdminNodeService();

    public boolean isLegalMigrate(String IP, String targetSequence) {
        Map<String, String> checkMap = new HashMap<String, String>();
        List<PhysicalNodeDO> nodeList = adminNodeService.queryNomalPhysicalNodesByIP(IP);
        if (nodeList != null && nodeList.size() > 0) {
            checkMap.put(IP, Integer.toString(nodeList.get(0).getSerialId()));
        }
        if (Integer.parseInt(targetSequence) > 0 && Integer.parseInt(targetSequence) < 9 && checkMap.get(IP) != null
            && !StringUtils.equals(checkMap.get(IP), targetSequence)) {
            return false;
        } else {
            checkMap.put(IP, targetSequence);
        }
        return true;
    }

    /**
     * 编辑节点信息：ip，port，physical id，machine id
     * 
     */
    public void doEdit(Context context, Navigator nav, HttpServletRequest request) {
        String editNodeId =  request.getParameter("editNodeId");
        nav.redirectTo(WebConstant.NODE_EDIT_LINK).withParameter("nodeId", editNodeId);
    }
    
    /**
     * 需要保证：同一台Machine下的Node不可以分配到不同的sequence里面去 Preview
     */
    public void doPreview(Context context, Navigator nav, HttpServletRequest request) {
        String[] newNodes = request.getParameterValues("newNode" );
        if (newNodes == null || newNodes.length == 0) return;
        boolean isLegalMigrateNodes = PhysicalNodeUtil.isLegalMigrateNodes(newNodes);
        if (!isLegalMigrateNodes) {
            nav.redirectTo(WebConstant.NODE_MANAGE_LIST_LINK).withParameter("errorResult",
                                                                            "同一台Machine下的Node不可以分配到不同的sequence里面去，请重新选择！");
            return;
        }
        StringBuilder nodes = new StringBuilder();
        for (String node : newNodes) {
            if (StringUtils.isNotBlank(node)) {
                String[] item = node.split("#");
                if (NumberUtils.isDigits(item[1])) {
                    boolean isLegal = adminNodeService.checkLegalMigrateByIP(
                                                                             item[2],
                                                                             StoreNodeSequenceEnum.getTypeByValue(Integer.parseInt(item[1])));
                    if (isLegal) {
                        nodes.append(node).append(",");
                    } else {
                        nav.redirectTo(WebConstant.NODE_MANAGE_LIST_LINK).withParameter("errorResult",
                                                                                        "同一台Machine下的Node不可以分配到不同的sequence里面去，请重新选择！");
                        return;
                    }
                }
            }
        }
        if (StringUtils.isBlank(nodes.toString())) {
            nav.redirectTo(WebConstant.NODE_MANAGE_LIST_LINK).withParameter("errorResult", "请选择扩容的Node！");
        } else {
            nav.redirectTo(WebConstant.NEW_NODES_PREVIEW_LINK).withParameter(
                                                                             "nodes",
                                                                             nodes.toString().substring(
                                                                                                        0,
                                                                                                        nodes.length() - 1)).withParameter(
                                                                                                                                           "isPreview",
                                                                                                                                           request.getParameter("isPreview"));
            ;
        }
    }

    /**
     * 点击扩容按钮
     */
    public void doAdd(Context context, Navigator nav, HttpServletRequest request) {
        String nodes = request.getParameter("nodes");
        // 新增的节点
        List<String> physicalIdList = null;
        Map<String, List<String>> nodeMap = new HashMap<String, List<String>>();
        if (StringUtils.isNotBlank(nodes)) {

            for (String pairStr : nodes.split(",")) {
                String[] pair = pairStr.split("#");

                if (nodeMap.get(pair[1]) == null || nodeMap.get(pair[1]).size() == 0) {
                    physicalIdList = new ArrayList<String>();
                    physicalIdList.add(pair[0]);
                    nodeMap.put(pair[1], physicalIdList);
                } else {
                    nodeMap.get(pair[1]).add(pair[0]);
                }
            }

        }

        try {
            for (String sequenceStr : nodeMap.keySet()) {
                StoreNodeSequenceEnum storeNodeSequence = StoreNodeSequenceEnum.getTypeByValue(Integer.valueOf(sequenceStr));

                // standby节点直接写数据库
                if (storeNodeSequence.equals(StoreNodeSequenceEnum.STANDBY_SEQUENCE)) {
                    List<PhysicalNodeDO> nodeList = new ArrayList<PhysicalNodeDO>();
                    for (String physicalId : nodeMap.get(sequenceStr)) {
                        PhysicalNodeDO nodeDo = adminNodeService.queryPhysicalNodeByPhysicalId(physicalId);
                        nodeDo.setSerialId(StoreNodeSequenceEnum.STANDBY_SEQUENCE.getValue());
                        nodeList.add(nodeDo);
                    }
                    adminNodeService.updatePhysicalNodeList(nodeList);
                } else {
                    // 正常序列和临时序列都走扩容处理
                    // 判断是否本序列是否在迁移中，如果在迁移中，则不可以再有扩容行为,发生迁移的序列可能为多个
                    boolean isMigerating = expansionMigrateProcessor.isMigrating(storeNodeSequence);

                    // ExpansionMigrateProcessor中增加Nomal序列和非Nomal序列的逻辑处理区分
                    if (!isMigerating) {
                        expansionMigrateProcessor.migerate(nodeMap.get(sequenceStr), storeNodeSequence);
                        //reload 任务由expansionMigrateProcessor 做。此处不需要
                        //nodesManager.reLoadNodes();
                        Thread.sleep(2000);
                    } else {
                        nav.redirectTo(WebConstant.NEW_NODES_PREVIEW_LINK).withParameter(
                                                                                         "errorResult",
                                                                                         "migratingError",
                                                                                         "第"
                                                                                                 + sequenceStr
                                                                                                 + "序列正在扩容迁移中，请等待其迁移完毕后再对其进行扩容操作。");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error happened when migrating new nodes.", e);
            // XXME Exception need to be resolved
            nav.redirectTo(WebConstant.NEW_NODES_PREVIEW_LINK).withParameter("errorResult", e.getMessage());
            return;
        }
        nav.redirectTo(WebConstant.NODE_MANAGE_LIST_LINK).withParameter("errorResult", "开始扩容迁移，请查看迁移进度（需要刷新页面获取最新进度数据）！");
    }

    /**
     * Re-Migrate button
     */
    public void doReMigrate(Context context, Navigator nav, HttpServletRequest request) {
        String reMigrateSequenceStr = request.getParameter("reMigrateSequence");
        StoreNodeSequenceEnum reMigrateSequence = StoreNodeSequenceEnum.getTypeByValue(Integer.parseInt(reMigrateSequenceStr));
        try {
            expansionMigrateProcessor.migerate(reMigrateSequence);
            //nodesManager.reLoadNodes();
            nav.redirectTo(WebConstant.NODE_MANAGE_LIST_LINK).withParameter("errorResult",
                                                                            "开始重迁，请查看迁移进度（需要刷新页面获取最新进度数据）！");
        } catch (AdminServiceException e) {
            log.error("Error happened when Re-Migrating new nodes of sequence " + reMigrateSequenceStr, e);
            nav.redirectTo(WebConstant.NODE_MANAGE_LIST_LINK).withParameter("errorResult", "重迁失败," + e.getMessage());
        }
    }

    /**
     * 解决临时失效按钮
     */
    public void doReplaceTempErrorNode(Context context, Navigator nav, HttpServletRequest request) {
        // 需要被替换的节点
        // String tempErrorNodeId = request.getParameter("errorNodeId");
        String tempErrorNodePhysicalId = request.getParameter("errorNodePhysicalId");

        try {
            tempFailoverProcessor.failResolve(tempErrorNodePhysicalId);
            //nodesManager.reLoadNodes();
            nav.redirectTo(WebConstant.NODE_MANAGE_LIST_LINK).withParameter("errorResult",
                                                                            "临时失效节点开始恢复，请查看恢复进度（需要刷新获取最新进度数据）");
        } catch (AdminServiceException e) {
            log.error("临时失效节点恢复失败", e);
            nav.redirectTo(WebConstant.NODE_MANAGE_LIST_LINK).withParameter("errorResult",
                                                                            "临时失效节点恢复失败," + e.getMessage());
        }

    }

    /*
     * 解决永久失效按钮
     */
    public void doReplaceForeverErrorNode(Context context, Navigator nav, HttpServletRequest request) {
        // 需要被替换的节点
        // String tempErrorNodeId = request.getParameter("errorNodeId");
        String tempErrorNodePhysicalId = request.getParameter("errorNodePhysicalId");

        try {
            foreverFailoverProcessor.failResolve(tempErrorNodePhysicalId);
            //nodesManager.reLoadNodes();
            nav.redirectTo(WebConstant.NODE_MANAGE_LIST_LINK).withParameter("errorResult",
                                                                            "永久失效节点开始恢复，请查看恢复进度（需要刷新获取最新进度数据）");
        } catch (AdminServiceException e) {
            log.error("永久失效节点恢复失败", e);
            nav.redirectTo(WebConstant.NODE_MANAGE_LIST_LINK).withParameter("errorResult",
                                                                            "永久失效节点恢复失败," + e.getMessage());
        }

    }

    /*
     * 删除Node按钮
     */
    public void doRemoveNode(Context context, HttpServletRequest request) {
        String removeSequence = request.getParameter("removeSequence");
        String removeNodePhysicalId = request.getParameter("removeNodePhysicalId");
        if (!NumberUtils.isNumber(removeSequence)) {

            return;
        }
        List<StoreNodeSequenceEnum> exceptSequences = new ArrayList<StoreNodeSequenceEnum>();
        exceptSequences.add(StoreNodeSequenceEnum.UNUSE_SEQUENCE);
        exceptSequences.add(StoreNodeSequenceEnum.STANDBY_SEQUENCE);
        exceptSequences.add(StoreNodeSequenceEnum.TEMP_SEQUENCE);
        // 如果是备用序列中的节点，直接删除；
        if (StoreNodeSequenceEnum.UNUSE_SEQUENCE.getValue() == Integer.parseInt(removeSequence)) {
            adminNodeService.deletePhysicalNode(removeNodePhysicalId);
            nodesManager.reLoadNodes();
            context.put("errorResult", "移除成功!");
        } else if (PhysicalNodeUtil.isAllNodesOk(exceptSequences)) {
            // 此处包含新增Node到临时序列、备用序列以及Nomal序列
            PhysicalNodeDO nodeDo = adminNodeService.queryPhysicalNodeByPhysicalId(removeNodePhysicalId);
            nodeDo.setSerialId(StoreNodeSequenceEnum.UNUSE_SEQUENCE.getValue());
            adminNodeService.updatePhysicalNode(nodeDo);
            nodesManager.reLoadNodes();
            context.put("errorResult", "移除成功!");
        } else {
            // nav.redirectTo(WebConstant.NODE_MANAGE_LIST_LINK).withParameter("errorResult",
            // "Nomal序列中有失效节点，请恢复到OK状态后再行操作。");
            context.put("errorResult", "Nomal序列中有失效节点，请恢复到OK状态后再行操作。");
        }

    }
}

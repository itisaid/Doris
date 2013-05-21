package com.alibaba.doris.admin.web.configer.module.screen;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.doris.admin.core.AdminServiceLocator;
import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.admin.service.AdminNodeService;
import com.alibaba.doris.admin.service.failover.node.check.NodeCheckManager;
import com.alibaba.doris.admin.service.failover.node.check.NodeHealth;
import com.alibaba.doris.admin.web.configer.support.NodeForView;
import com.alibaba.doris.admin.web.configer.util.SequenceUtil;
import com.alibaba.doris.admin.web.configer.util.WebConstant;
import com.alibaba.doris.common.StoreNode;
import com.alibaba.doris.common.StoreNodeSequenceEnum;

/**
 * @project :Doris
 * @author : len.liu
 * @datetime : 2011-6-2 下午04:54:00
 * @version :0.1
 * @Modification:
 */
public class NewNodesPreview {

    private AdminNodeService adminNodeService = null;
    private NodeCheckManager nodeCheckManager = null;

    private AdminNodeService getAdminNodeService() {
        if (adminNodeService == null) {
            adminNodeService = AdminServiceLocator.getAdminNodeService();
        }
        return adminNodeService;
    }

    private NodeCheckManager getNodeCheckManager() {
        if (nodeCheckManager == null) {
            nodeCheckManager = NodeCheckManager.getInstance();
        }
        return nodeCheckManager;
    }

    /**
     * @param context
     */
    public void execute(Context context, HttpServletRequest request) {
        String isPreview = request.getParameter("isPreview");
        String nodes = request.getParameter("nodes");
        Map<String, List<NodeForView>> nodemap = new LinkedHashMap<String, List<NodeForView>>();
        boolean hasNodeNG = false;
        if ("0".equals(isPreview) && StringUtils.isNotBlank(nodes)) {
            for (String pair : nodes.split(",")) {
                String[] sequeceAndNode = pair.split("#");
                PhysicalNodeDO nodeDo = getAdminNodeService().queryPhysicalNodeByPhysicalId(
                        sequeceAndNode[0]);

                StoreNode sn = new StoreNode();
                sn.setIp(nodeDo.getIp());
                sn.setLogicId(nodeDo.getLogicalId());
                sn.setPhId(nodeDo.getPhysicalId());
                sn.setPort(nodeDo.getPort());
                StoreNodeSequenceEnum seq = StoreNodeSequenceEnum.getTypeByValue(Integer
                        .parseInt(sequeceAndNode[1]));
                sn.setSequence(seq);

                NodeHealth nodeHealth = getNodeCheckManager().checkNode(sn, true);

                
                NodeForView view = new NodeForView();
                view.setPhysicalNodeDO(nodeDo);
                
                
                //临时借用这个字段， 只是显示用
                if (nodeHealth == NodeHealth.OK) {
                    view.setHealthStatus("OK");
                } else {
                    //NG
                    view.setHealthStatus("NG");
                    hasNodeNG = true;
                }
                
                nodeDo.setSerialId(Integer.parseInt(sequeceAndNode[1]));
                
                if (nodemap.get(sequeceAndNode[1]) == null) {
                    List<NodeForView> viewList = new ArrayList<NodeForView>();
                    viewList.add(view);
                    nodemap.put(sequeceAndNode[1], viewList);
                } else {
                    nodemap.get(sequeceAndNode[1]).add(view);
                }
            }

        }
        Map<Integer, String> allSequenceMap = SequenceUtil.getAllSequenceMap();
        context.put("nodes", nodes);
        context.put("nodemap", nodemap);
        context.put("isPreview", "1");
        context.put("hasNodeNG", hasNodeNG);
        if (hasNodeNG) {
            context.put("errorResult", "有状态为NG的节点，请检查");
        }
        context.put(WebConstant.ALL_SEQUENCE_IDS_KEY, allSequenceMap);
    }
}

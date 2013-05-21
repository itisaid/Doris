package com.alibaba.doris.algorithm.mirgate;

import java.util.List;

import com.alibaba.doris.algorithm.vpm.VpmMapping;

/**
 * 返回物理节点的虚拟节点列表
 * 临时失效恢复时，供临时节点迁移用
 * @author frank
 *
 */
public class TempFailResolveRouter {

    private List<List<Integer>> vpm;

    public TempFailResolveRouter(int virtualNodesNum, int physicalNodesNum) {
        vpm = VpmMapping.makeP2VMapping(physicalNodesNum, virtualNodesNum);
    }

    /**
     * 返回临时失效恢复物理节点的虚拟节点列表
     * @param physicalNodeId 临时失效恢复节点的逻辑ID
     * @return 虚拟节点列表
     */
    public List<Integer> getTempFailMigerateVirtialNodes(int nodeLogicId) {
        if (nodeLogicId < 0 || nodeLogicId > vpm.size()) {
            return null;
        }
        return vpm.get(nodeLogicId);
    }
}

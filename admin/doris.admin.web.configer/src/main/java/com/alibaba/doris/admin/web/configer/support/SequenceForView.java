package com.alibaba.doris.admin.web.configer.support;

import java.util.List;

public class SequenceForView {

    List<NodeForView> nodeViewList;

    boolean           isSequenceMigrating;

    public List<NodeForView> getNodeViewList() {
        return nodeViewList;
    }

    public void setNodeViewList(List<NodeForView> nodeViewList) {
        this.nodeViewList = nodeViewList;
    }

    public boolean isSequenceMigrating() {
        return isSequenceMigrating;
    }

    public void setSequenceMigrating(boolean isSequenceMigrating) {
        this.isSequenceMigrating = isSequenceMigrating;
    }

}

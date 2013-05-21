package com.alibaba.doris.admin.web.monitor.support;

import com.alibaba.doris.admin.dataobject.PhysicalNodeDO;
import com.alibaba.doris.common.RealtimeInfo;

public class NodeViewForMonitor {

    private PhysicalNodeDO physicalNode;

    private String         errorInfo;

    private RealtimeInfo   realtimeInfo;

    public NodeViewForMonitor(PhysicalNodeDO physicalNodeDO) {
        super();

        this.physicalNode = physicalNodeDO;
    }

    public String getPhysicalId() {
        return physicalNode.getPhysicalId();
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public RealtimeInfo getRealtimeInfo() {
        return realtimeInfo;
    }

    public void setRealtimeInfo(RealtimeInfo realtimeInfo) {
        this.realtimeInfo = realtimeInfo;
    }

}

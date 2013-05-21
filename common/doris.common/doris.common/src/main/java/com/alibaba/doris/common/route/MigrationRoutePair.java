package com.alibaba.doris.common.route;

public class MigrationRoutePair {

    private Integer vnode;
    private String  targetPhysicalId;

    public MigrationRoutePair() {
    }

    public MigrationRoutePair(int vnode, String targetPhysicalId) {
        this.vnode = vnode;
        this.targetPhysicalId = targetPhysicalId;
    }

    public Integer getVnode() {
        return vnode;
    }

    public void setVnode(Integer vnode) {
        this.vnode = vnode;
    }

    public String getTargetPhysicalId() {
        return targetPhysicalId;
    }

    public void setTargetPhysicalId(String targetPhysicalId) {
        this.targetPhysicalId = targetPhysicalId;
    }

    public String toString(){
        return "("+vnode+","+targetPhysicalId+")";
    }
}

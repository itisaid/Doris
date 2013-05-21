package com.alibaba.doris.admin.web.configer.support;

public class NewAddedNodeVO {

    private String   isPreview;

    private String[] newNode;

    public String[] getNewNode() {
        return newNode;
    }

    public void setNewNode(String[] newNode) {
        this.newNode = newNode;
    }

    public String getIsPreview() {
        return isPreview;
    }

    public void setIsPreview(String isPreview) {
        this.isPreview = isPreview;
    }

}

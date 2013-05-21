package com.alibaba.doris.dataserver.monitor.support;

public class PrefTrackerKey {
    private String actionName;
    private String nameSpace;

    public PrefTrackerKey(String actionName, String nameSpace) {
        super();
        this.actionName = actionName;
        this.nameSpace = nameSpace;
    }

    public String getActionName() {
        return actionName;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public String getkey() {
        return actionName + "|" + nameSpace;
    }

}

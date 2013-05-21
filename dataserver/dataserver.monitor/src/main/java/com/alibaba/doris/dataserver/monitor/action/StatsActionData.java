package com.alibaba.doris.dataserver.monitor.action;

import com.alibaba.doris.dataserver.action.ActionType;
import com.alibaba.doris.dataserver.action.data.ActionData;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class StatsActionData implements ActionData {

    /**
     * 视图类型 summary or detail
     */
    private String viewType;

    /**
     * query with name space
     */
    private String namespace;

    public ActionType getActionType() {
        return StatsActionType.STATS;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("{");
        if (viewType != null) {
            sb.append("[viewType=");
            sb.append(viewType);
            sb.append("]");
        }
        if (null != namespace) {
            sb.append("[namespace=");
            sb.append(namespace);
            sb.append("]");
        }
        sb.append("}");
        return sb.toString();
    }

}

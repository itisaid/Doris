package com.alibaba.doris.dataserver.extratools.replica.action;

import com.alibaba.doris.dataserver.action.ActionType;
import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.extratools.ExtraActionType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ExportActionData implements ActionData {

    public ActionType getActionType() {
        return ExtraActionType.EXPORT;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getVnodes() {
        return vnodes;
    }

    public void setVnodes(String vnodes) {
        this.vnodes = vnodes;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String namespace;
    private String vnodes;
    private String target;
    private String message;
}

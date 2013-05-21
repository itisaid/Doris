package com.alibaba.doris.dataserver.extratools.replica.action;

import com.alibaba.doris.dataserver.action.ActionType;
import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.extratools.ExtraActionType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ImportActionData implements ActionData {

    public ActionType getActionType() {
        return ExtraActionType.IMPORT;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCas() {
        return cas;
    }

    public void setCas(String cas) {
        this.cas = cas;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String source;
    private String cas;
    private String message;
}

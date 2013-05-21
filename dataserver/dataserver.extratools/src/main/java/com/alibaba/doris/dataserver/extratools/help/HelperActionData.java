package com.alibaba.doris.dataserver.extratools.help;

import com.alibaba.doris.dataserver.action.ActionType;
import com.alibaba.doris.dataserver.action.data.BaseActionData;
import com.alibaba.doris.dataserver.extratools.ExtraActionType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class HelperActionData extends BaseActionData {

    public HelperActionData(String subCommand) {
        this.subCommand = subCommand;
    }

    public HelperActionData() {
        this.subCommand = "all";
    }

    public ActionType getActionType() {
        return ExtraActionType.HELP;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubCommand() {
        return subCommand;
    }

    private String message;
    private String subCommand;
}

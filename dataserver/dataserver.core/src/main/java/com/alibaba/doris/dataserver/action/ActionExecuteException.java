package com.alibaba.doris.dataserver.action;

import com.alibaba.doris.dataserver.DataServerException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ActionExecuteException extends DataServerException {

    private static final long serialVersionUID = -9156680157399785010L;

    /**
     * Creates a new exception.
     */
    public ActionExecuteException() {
        super();
    }

    /**
     * Creates a new exception.
     */
    public ActionExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public ActionExecuteException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public ActionExecuteException(Throwable cause) {
        super(cause);
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    private Action     action;
    private ActionType actionType;
}

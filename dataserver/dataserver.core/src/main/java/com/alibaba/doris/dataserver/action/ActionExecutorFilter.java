package com.alibaba.doris.dataserver.action;

import com.alibaba.doris.dataserver.DataServerException;
import com.alibaba.doris.dataserver.action.data.ActionData;
import com.alibaba.doris.dataserver.core.Request;
import com.alibaba.doris.dataserver.core.RequestFilter;
import com.alibaba.doris.dataserver.core.RequestFilterChian;
import com.alibaba.doris.dataserver.core.Response;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ActionExecutorFilter implements RequestFilter {

    public void doFilter(Request request, Response response, RequestFilterChian filterChain) {
        ActionData actionData = request.getActionData();
        if (null != actionData) {
            Action action = ActionFactory.getAction(actionData.getActionType());
            if (null == action) {
                throw new DataServerException("Couldn't find the action for command :"
                                              + actionData.getActionType().getName());
            }

            try {
                action.execute(request, response);
            } catch (Throwable e) {
                ActionExecuteException exception = new ActionExecuteException(e);
                exception.setAction(action);
                exception.setActionType(actionData.getActionType());
                throw exception;
            }
        } else {
            ;// ???
        }

        filterChain.doFilter(request, response);
    }
}

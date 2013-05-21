package com.alibaba.doris.dataserver.action;

import com.alibaba.doris.dataserver.action.data.ExitActionData;
import com.alibaba.doris.dataserver.core.Request;
import com.alibaba.doris.dataserver.core.Response;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ExitServerAction implements Action {

    public void execute(Request request, Response response) {
        ExitActionData md = (ExitActionData) request.getActionData();
        if (null != md) {
            // close current connection.
            response.close();
        }
    }

}

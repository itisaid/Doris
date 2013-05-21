package com.alibaba.doris.dataserver.action;

import com.alibaba.doris.dataserver.action.data.ErrorActionData;
import com.alibaba.doris.dataserver.core.Request;
import com.alibaba.doris.dataserver.core.Response;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class CatchCommandErrorAction implements Action {

    public void execute(Request request, Response response) {
        ErrorActionData errorActionData = (ErrorActionData) request.getActionData();
        if (null != errorActionData) {
            response.write(errorActionData);
        }
    }

}

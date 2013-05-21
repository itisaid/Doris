package com.alibaba.doris.dataserver.action;

import com.alibaba.doris.dataserver.core.Request;
import com.alibaba.doris.dataserver.core.Response;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface Action {

    public void execute(Request request, Response response);
}

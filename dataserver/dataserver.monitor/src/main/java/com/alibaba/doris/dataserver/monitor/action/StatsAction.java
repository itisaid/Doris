package com.alibaba.doris.dataserver.monitor.action;

import com.alibaba.doris.dataserver.action.BaseAction;
import com.alibaba.doris.dataserver.core.Request;
import com.alibaba.doris.dataserver.core.Response;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class StatsAction extends BaseAction {

    public void execute(Request request, Response response) {
        StatsActionData ad = (StatsActionData) request.getActionData();
        response.write(ad);
    }

}

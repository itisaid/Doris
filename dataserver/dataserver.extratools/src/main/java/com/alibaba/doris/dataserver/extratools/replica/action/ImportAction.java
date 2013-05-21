package com.alibaba.doris.dataserver.extratools.replica.action;

import com.alibaba.doris.dataserver.core.Request;
import com.alibaba.doris.dataserver.core.Response;
import com.alibaba.doris.dataserver.store.Storage;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ImportAction extends ExportAction {

    @Override
    public void execute(Request request, Response response) {
        ImportActionData actionData = (ImportActionData) request.getActionData();
        Storage storage = getStorage(request);

        long start = System.currentTimeMillis();
        int num = replicaTool.importData(storage, actionData);
        long end = System.currentTimeMillis();
        actionData.setMessage("Processing [" + num + "] records! Time(ms):[" + (end - start) + "]");
        response.write(actionData);
    }

}

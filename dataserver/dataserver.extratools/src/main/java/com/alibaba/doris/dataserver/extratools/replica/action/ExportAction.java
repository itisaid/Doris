package com.alibaba.doris.dataserver.extratools.replica.action;

import com.alibaba.doris.dataserver.ApplicationContext;
import com.alibaba.doris.dataserver.action.Action;
import com.alibaba.doris.dataserver.config.ModuleConstances;
import com.alibaba.doris.dataserver.core.Request;
import com.alibaba.doris.dataserver.core.Response;
import com.alibaba.doris.dataserver.extratools.replica.ReplicaTool;
import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.StorageModule;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ExportAction implements Action {

    public void execute(Request request, Response response) {
        ExportActionData actionData = (ExportActionData) request.getActionData();
        Storage storage = getStorage(request);

        long start = System.currentTimeMillis();
        int num = replicaTool.exportData(storage, actionData);
        long end = System.currentTimeMillis();
        actionData.setMessage("Processing [" + num + "] records! Time(ms):[" + (end - start) + "]");
        response.write(actionData);
    }

    protected Storage getStorage(Request request) {
        if (null == storageModule) {
            ApplicationContext appContext = request.getApplicationContext();
            storageModule = (StorageModule) appContext.getModuleByName(ModuleConstances.STORAGE_MODULE);
        }

        return storageModule.getStorage();
    }

    private StorageModule storageModule;
    protected ReplicaTool replicaTool = new ReplicaTool();
}

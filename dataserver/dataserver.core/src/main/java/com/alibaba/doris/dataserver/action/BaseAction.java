package com.alibaba.doris.dataserver.action;

import com.alibaba.doris.dataserver.ApplicationContext;
import com.alibaba.doris.dataserver.config.ModuleConstances;
import com.alibaba.doris.dataserver.core.Request;
import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.StorageModule;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public abstract class BaseAction implements Action {

    protected Storage getStorage(Request request) {
        if (null != storage) {
            return storage;
        }

        // double check problem? who care?
        synchronized (this) {
            if (null != storage) {
                return storage;
            }

            ApplicationContext appContext = request.getApplicationContext();
            StorageModule module = (StorageModule) appContext.getModuleByName(ModuleConstances.STORAGE_MODULE);
            storage = module.getStorage();
        }

        return storage;
    }

    private Storage storage;
}

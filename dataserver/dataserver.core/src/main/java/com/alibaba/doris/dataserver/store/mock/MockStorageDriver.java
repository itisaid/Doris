package com.alibaba.doris.dataserver.store.mock;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.alibaba.doris.dataserver.config.data.ModuleConfigure;
import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.StorageConfig;
import com.alibaba.doris.dataserver.store.StorageDriver;
import com.alibaba.doris.dataserver.store.StorageType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class MockStorageDriver implements StorageDriver {

    public Storage createStorage() {
        return storage;
    }

    public StorageType getStorageType() {
        return MockStorageType.MOCK_STORAGE;
    }

    public void init(StorageConfig config) {
        ModuleConfigure storageModuleConfigure = config.getStorageModuleConfigure();
        Properties commandLine = storageModuleConfigure.getDataServerConfigure().getCommandLine();
        String isInmem = commandLine.getProperty("isInmem");
        if (StringUtils.isNotBlank(isInmem) && "false".equalsIgnoreCase(isInmem)) {
            storage = new MockStorage(false);
        } else {
            storage = new MockStorage();
        }
    }

    private Storage storage;
}

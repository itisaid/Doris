package com.alibaba.doris.dataserver.store.bdb;

import java.io.File;
import java.util.Date;

import junit.framework.TestCase;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.KeyImpl;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.StorageConfig;
import com.alibaba.doris.dataserver.store.StorageDriver;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public abstract class BaseBdbStorageTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        try {
            driver = (StorageDriver) BDBStorageDriver.class.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        StorageConfig config = getStorageConfig();
        driver.init(config);
        storage = driver.createStorage();
        storage.open();
    }

    @Override
    protected void tearDown() throws Exception {
        storage.close();
    }

    public StorageConfig getStorageConfig() {
        StorageConfig config = new StorageConfig();
        config.setPropertiesFile("bdb_storage_test_mock.properties");
        config.setSize(5000);
        config.setStorageDriverClass("com.alibaba.doris.dataserver.store.bdb.BDBStorageDriver");
        config.setStorageTypeClass("");
        return config;
    }

    protected StorageDriver getStorageDriver() {
        return driver;
    }

    protected Storage getStorage() {
        return storage;
    }

    protected Key createKey(String key) {
        return new KeyImpl(100, key, 0);
    }

    protected Value createValue(String value) {
        return new ValueImpl(ByteUtils.stringToByte(value), (new Date()).getTime());
    }

    protected void clear() {
        String path = BaseBdbStorageTest.class.getClassLoader().getResource("").getPath();
        File f = new File(path);
        for (File file : f.listFiles()) {
            if (file.getName().contains(".jdb")) {
                file.delete();
            } else if (file.getName().contains("je.")) {
                file.delete();
            }
        }
    }

    private StorageDriver driver;
    private Storage       storage;
}

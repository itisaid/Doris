package com.alibaba.doris.dataserver.store.bdb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.StorageConfig;
import com.alibaba.doris.dataserver.store.StorageDriver;
import com.alibaba.doris.dataserver.store.StorageTestUnit;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class BdbStorageCommonTest extends StorageTestUnit {

    @Override
    protected Storage getStorage() {
        return storage;
    }

    @Before
    public void setUp() throws Exception {
        clear();
        try {
            driver = (StorageDriver) BDBStorageDriver.class.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        StorageConfig config = getStorageConfig();
        driver.init(config);
        storage = driver.createStorage();
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    private void clear() {
        try {
            Thread.sleep(5);
            FileUtils.forceDelete(new File("./bdb_test"));
        } catch (FileNotFoundException ignore) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
        }

    }

    public StorageConfig getStorageConfig() {
        StorageConfig config = new StorageConfig();
        config.setPropertiesFile("bdb_storage_common_test.properties");
        config.setSize(5000);
        config.setStorageDriverClass("com.alibaba.doris.dataserver.store.bdb.BDBStorageDriver");
        config.setStorageTypeClass("");
        return config;
    }

    protected StorageDriver getStorageDriver() {
        return driver;
    }

    private StorageDriver driver;
    private Storage       storage;
}

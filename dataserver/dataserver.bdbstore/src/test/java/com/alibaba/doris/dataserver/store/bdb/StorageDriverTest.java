package com.alibaba.doris.dataserver.store.bdb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.KeyImpl;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.StorageConfig;
import com.alibaba.doris.dataserver.store.StorageDriver;
import com.alibaba.doris.dataserver.store.bdb.utils.BDBConfigUtil;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class StorageDriverTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        try {
            driver = (StorageDriver) BDBStorageDriver.class.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        StorageConfig config = getStorageConfig();
        driver.init(config);
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

    protected Key createKey(String key) {
        return new KeyImpl(100, key, 0);
    }

    protected Value createValue(String value) {
        return new ValueImpl(ByteUtils.stringToByte(value), (new Date()).getTime());
    }

    protected void clear() {
        String path = this.getClass().getClassLoader().getResource("").getPath();
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

    public void testLoadDriver() throws InstantiationException, IllegalAccessException {
        StorageDriver driver = getStorageDriver();
        Storage storage = driver.createStorage();
        assertNotNull(storage);
        StringBuilder sb = new StringBuilder(1024);
        for (int i = 0; i < 100; i++) {
            sb.append("1234567890");
        }
        Value value = new ValueImpl(ByteUtils.stringToByte(sb.toString()), (new Date()).getTime());
        int loopTimes = 100;
        try {
            storage.open();
            startRecordTime();
            Random r = new Random();
            for (int i = 0; i < loopTimes; i++) {
                Key key = new KeyImpl(100, "key" + r.nextInt(loopTimes), 1);
                storage.set(key, value);
                Value v = storage.get(key);
                assertNotNull(v);
                assertNotNull(v.getValueBytes());
            }
            endRecordTime();
            printStatisticInformation(loopTimes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            storage.close();
        }
    }

    public void testCreateBDBDataBase() throws IOException {
        String dbPath = BDBConfigUtil.getDataBaseDirectory();
        File f = new File(dbPath);
        if (f.exists()) {
            // FileUtils.deleteDirectory(f);
        }

        BDBStorage storage = null;
        try {
            StorageDriver driver = getStorageDriver();
            storage = (BDBStorage) driver.createStorage();
            startRecordTime();

            List<BDBDataBase> dbList = new ArrayList<BDBDataBase>(DB_NUMBER);
            for (int i = 0; i < DB_NUMBER; i++) {
                BDBDataBase db = storage.getDataBase(String.valueOf(i));
                dbList.add(db);
            }
            endRecordTime();
            printStatisticInformation(DB_NUMBER);

            startRecordTime();
            Value value = new ValueImpl(ByteUtils.stringToByte("valueissijejrer"), (new Date()).getTime());
            for (BDBDataBase db : dbList) {
                for (int i = 0; i < 10; i++) {
                    Key key = new KeyImpl(100, "key" + i, 1);
                    db.set(key, value);
                }
            }
            endRecordTime();
            printStatisticInformation(DB_NUMBER);
        } finally {
            if (null != storage) {
                storage.close();
            }
        }
    }

    private void startRecordTime() {
        startTime = System.currentTimeMillis();
    }

    private void endRecordTime() {
        endTime = System.currentTimeMillis();
    }

    private void printStatisticInformation(int num) {
        System.out.println("Create " + num + " db, total time(ms):" + (endTime - startTime) + ", everage time(ms):"
                           + ((endTime - startTime) / num));
    }

    private long             endTime;
    private long             startTime;
    private static final int DB_NUMBER = 10;

}

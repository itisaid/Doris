package com.alibaba.doris.dataserver.store.handlersocket;

import java.io.IOException;
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

public class HandlerSocketStorageTest extends TestCase {

	private StorageDriver driver;
	private HandlerSocketStorage storage;
    private long endTime;
    private long startTime;
	
	protected void setUp() throws Exception {
		try {
			driver = (StorageDriver) HandlerSocketStorageDriver.class
					.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		StorageConfig config = getStorageConfig();
		driver.init(config);
		storage = (HandlerSocketStorage)driver.createStorage();
		storage.open();
	}

	protected void tearDown() throws Exception {
		storage.close();
	}

	public StorageConfig getStorageConfig() {
		StorageConfig config = new StorageConfig();
		config.setPropertiesFile("handlersocket_test.properties");
		config.setSize(5000);
		config.setStorageDriverClass("com.alibaba.doris.dataserver.store.bdb.HandlerSocketStorageDriver");
		config.setStorageTypeClass("");
		return config;
	}

	protected StorageDriver getStorageDriver() {
		return driver;
	}

	protected Storage getStorage() {
		return storage;
	}
	
    public void testSet() throws IOException {
        try {
            startRecordTime();
            Key key = new KeyImpl(100, "TestKey", 10);
            storage.delete(key);
            Value value = new ValueImpl(ByteUtils.stringToByte("valueissijejrer",storage.getConfig().getEncoding()), (new Date()).getTime());
            storage.set(key, value);
            assertEquals(new String(storage.get(key).getValueBytes(),storage.getConfig().getEncoding()),"valueissijejrer");
            endRecordTime();
            printStatisticInformation();
        } finally {
            if (null != storage) {
                storage.close();
            }
        }
    }

    public void testGet() throws IOException {
        try {
            startRecordTime();
            Key key = new KeyImpl(100, "TestKey", 1);
            storage.delete(key);
            Value value1 = new ValueImpl(ByteUtils.stringToByte("valueissijejrer",storage.getConfig().getEncoding()), (new Date()).getTime());
            storage.set(key, value1);
            
            Value value = storage.get(key);
            assertEquals("valueissijejrer",new String(value.getValueBytes()));
            endRecordTime();
            printStatisticInformation();
        } finally {
            if (null != storage) {
                storage.close();
            }
        }
    }
    
    public void testDelete()
    {
        try {
            startRecordTime();
            Key key = new KeyImpl(100, "TestKey", 1);
            storage.delete(key);
            Value value = new ValueImpl(ByteUtils.stringToByte("valueissijejrer",storage.getConfig().getEncoding()), (new Date()).getTime());
            storage.set(key, value);
            boolean isDeleted = storage.delete(key);
            assertTrue(isDeleted);
            endRecordTime();
            printStatisticInformation();
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

    private void printStatisticInformation() {
        System.out.println("Create handlersocket storage, total time(ms):" + (endTime - startTime) );
    }	
}

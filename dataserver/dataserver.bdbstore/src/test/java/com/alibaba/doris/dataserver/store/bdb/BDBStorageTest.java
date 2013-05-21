package com.alibaba.doris.dataserver.store.bdb;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import junit.framework.TestCase;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.KeyImpl;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.store.ClosableIterator;
import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.StorageConfig;
import com.alibaba.doris.dataserver.store.StorageDriver;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class BDBStorageTest extends TestCase {

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
    private Storage       storage;

    public void testGetAndSet() {

        Storage storage = getStorage();
        Key key = createKey("test");
        Value value = createValue("啊荤万科假日巍峨峻岭");

        storage.set(key, value);
        Value vNew = storage.get(key);
        assertNotNull(vNew);
        assertTrue(value.equals(vNew));
    }

    public void testDelete() {
        Storage storage = getStorage();
        Key key = createKey("test");
        Value value = createValue("啊荤万科假日巍峨峻岭");

        storage.set(key, value);
        Value vNew = storage.get(key);
        assertNotNull(vNew);
        assertTrue(value.equals(vNew));

        storage.delete(key);
        vNew = storage.get(key);
        assertNull(vNew);
    }

    public void t1estCas() {
        Thread t1 = new ThreadCas();
        Thread t2 = new ThreadSet();
        t1.start();
        t2.start();
    }

    private class ThreadCas extends Thread {

        @Override
        public void run() {
            Storage storage = getStorage();
            Key key = createKey("test");
            Value value = createValue("啊荤万科假日巍峨峻岭");

            storage.set(key, value, true);
        }

    }

    private class ThreadSet extends Thread {

        @Override
        public void run() {
            Storage storage = getStorage();
            Key key = createKey("test");
            Value value = createValue("cas");

            storage.set(key, value);
        }

    }

    public void testGetAll() {
        Storage storage = getStorage();
        Map<Key, Value> map = new HashMap<Key, Value>();
        List<Key> keyList = new ArrayList<Key>();
        for (int i = 0; i < 10; i++) {
            Key key = createKey("test" + i);
            Value value = createValue("啊荤万科假日巍峨峻岭" + i);
            map.put(key, value);
            keyList.add(key);
            storage.set(key, value);
        }

        Map<Key, Value> mapNew = storage.getAll(keyList);
        assertNotNull(mapNew);

        Iterator<Entry<Key, Value>> itr = mapNew.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<Key, Value> evtry = itr.next();
            Value vNew = evtry.getValue();
            Key key = evtry.getKey();
            Value aspect = map.get(key);
            assertTrue(vNew.equals(aspect));
        }
    }

    public void testIterator() {
        clear();
        int len = 10;
        Storage storage = getStorage();
        for (int i = 0; i < len; i++) {
            Key key = createKey("test" + i);
            Value value = createValue("啊荤万科假日巍峨峻岭" + i);
            storage.set(key, value);
        }

        ClosableIterator<Pair> testPairIterator = (ClosableIterator<Pair>) storage.iterator();
        assertNotNull(testPairIterator);

        int count = 0;
        while (testPairIterator.hasNext()) {
            Pair p = testPairIterator.next();
            assertNotNull(p);
            assertNotNull(p.getKey());
            assertNotNull(p.getValue());
            count++;
        }
        testPairIterator.close();
        assertTrue(count >= len);
    }

    public void testIterator2() {
        int len = 10;
        Storage storage = getStorage();
        // for (int i = 0; i < len; i++) {
        // Key key = createKey("test" + i);
        // Value value = createValue("啊荤万科假日巍峨峻岭" + i);
        // storage.set(key, value);
        // }
        List<Integer> nodeList = new ArrayList<Integer>(virtualNode.length);
        for (int i : virtualNode) {
            nodeList.add(i);
        }

        ClosableIterator<Pair> testPairIterator = (ClosableIterator<Pair>) storage.iterator(nodeList);
        assertNotNull(testPairIterator);

        int count = 0;
        while (testPairIterator.hasNext()) {
            Pair p = testPairIterator.next();
            assertNotNull(p);
            assertNotNull(p.getKey());
            assertNotNull(p.getValue());
            count++;
        }

        testPairIterator.close();
        assertTrue(count >= len);
    }

    public void testRemoveDataBase() {
        Storage storage = getStorage();
        List<Integer> nodeList = new ArrayList<Integer>(virtualNode.length);
        for (int i : virtualNode) {
            nodeList.add(i);
        }

        ClosableIterator<Pair> testPairIterator = (ClosableIterator<Pair>) storage.iterator(nodeList);
        assertNotNull(testPairIterator);

        Set<Integer> vnodeSet = new HashSet<Integer>();
        while (testPairIterator.hasNext()) {
            Pair p = testPairIterator.next();
            assertNotNull(p);
            assertNotNull(p.getKey());
            vnodeSet.add(p.getKey().getVNode());
        }
        testPairIterator.close();

        List<Integer> vnodeList = new ArrayList<Integer>(vnodeSet);
        storage.delete(vnodeList);

        testPairIterator = (ClosableIterator<Pair>) storage.iterator(vnodeList);
        assertNotNull(testPairIterator);
        assertFalse(testPairIterator.hasNext());
        testPairIterator.close();
    }

    private int[] virtualNode = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };
}

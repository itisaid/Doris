package com.alibaba.doris.dataserver.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.KeyImpl;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.store.exception.VersionConflictException;

/**
 * 存储层通用单元测试
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public abstract class StorageTestUnit {

    protected abstract Storage getStorage();

    @Before
    public void setUp() throws Exception {
        getStorage().open();
    }

    @After
    public void tearDown() throws Exception {
        getStorage().close();
    }

    @Test
    public void testGetAndSet() {
        Storage storage = getStorage();
        Key key = createKey("test");
        Value value = createValue("啊荤万科假日巍峨峻岭123412341234Zhiwer%￥#@！（×&（）");

        Value vNew = storage.get(key);
        assertNull("断言从存储中获取一个不存在的key，应该返回null", vNew);

        storage.set(key, value);
        vNew = storage.get(key);
        assertNotNull(vNew);
        assertTrue("断言，set到storage的值可以正常获取。", value.equals(vNew));

        Value value2 = createValue("啊荤万科假日巍峨峻岭2");
        storage.set(key, value2);
        vNew = storage.get(key);
        assertEquals("断言，set最新的值需要覆盖原有的值。", value2, vNew);
    }

    @Test
    public void testCas() {
        Storage storage = getStorage();
        Key key = createKey("test");
        Value value = createValue("啊荤万科假日巍峨峻岭2");
        storage.set(key, value, true);
        Value vNew = storage.get(key);
        assertEquals("断言cas，当key在存储中不存在时，cas能够保存数据。", value, vNew);

        Value value1 = createValue("啊荤万科假日巍峨峻岭 cas new");
        storage.set(key, value1, true);
        vNew = storage.get(key);
        assertEquals("断言cas，cas能覆盖已有的值。", value1, vNew);
        try {
            storage.set(key, value, true);
            fail("断言，当老版本的value覆盖已有的更新版本的value时，应该抛出VersionConflictException异常。");
        } catch (VersionConflictException e) {
        }
        vNew = storage.get(key);
        assertEquals("断言cas，cas不能用老版本的value覆盖存储中已有的版本较新的值。", value1, vNew);
    }

    @Test
    public void testCad() {
        Storage storage = getStorage();
        Key key = createKey("test");
        Value oldValue = createValue("啊荤万科假日巍峨峻岭2");
        storage.set(key, oldValue);
        Value vNew = storage.get(key);

        Value newValue = createValue("啊荤万科假日巍峨峻岭 cas new");
        assertTrue("断言，一个带新版value的删除操作，能够删除已有的老版本的value", storage.delete(key, newValue));
        vNew = storage.get(key);
        assertNull("断言数据已经被删除成功。", vNew);
        try {
            storage.set(key, newValue);
            storage.delete(key, oldValue);
            fail("断言，当一个带旧版本value的删除操作，应该抛出异常。");
        } catch (VersionConflictException e) {
        }
        vNew = storage.get(key);
        assertEquals("断言用老版本的value，应该无法删除已有的更新版本的value。", newValue, vNew);
    }

    @Test
    public void testDelete() {
        Storage storage = getStorage();
        Key key = createKey("test");
        Value value = createValue("啊荤万科假日巍峨峻岭");

        storage.set(key, value);
        Value vNew = storage.get(key);
        assertNotNull(vNew);
        assertTrue(value.equals(vNew));

        assertTrue("断言应该成功删除一个已经存在的key", storage.delete(key));
        vNew = storage.get(key);
        assertNull("断言存储当中key对应的值已经成功删除。", vNew);

        assertFalse("断言删除一个不存在的key，应该返回失败。", storage.delete(key));
    }

    @Test
    public void testGetAll() {
        Storage storage = getStorage();
        Map<Key, Value> map = new HashMap<Key, Value>();
        List<Key> keyList = new ArrayList<Key>();
        for (int i = 0; i < len; i++) {
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
            assertTrue("断言key=" + key + " 的值应该和批量从存储中获取的值一致。", vNew.equals(aspect));
        }
    }

    @Test
    public void testIterator() {
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

        assertTrue("aspact:count==len, actual is count=" + count + ", len=" + len, count == len);
    }

    @Test
    public void testIterator2() {
        Storage storage = getStorage();
        for (int i = 0; i < len; i++) {
            Key key = createKey("test" + i);
            Value value = createValue("啊荤万科假日巍峨峻岭" + i);
            storage.set(key, value);
        }
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
        assertEquals(len, count);
    }

    @Test
    public void testRemoveDataBase() {
        Storage storage = getStorage();

        for (int i = 0; i < len; i++) {
            Key key = createKey("test" + i);
            Value value = createValue("啊荤万科假日巍峨峻岭" + i);
            storage.set(key, value);
        }

        List<Integer> nodeList = new ArrayList<Integer>(virtualNode.length);
        for (int i : virtualNode) {
            nodeList.add(i);
        }

        ClosableIterator<Pair> testPairIterator = (ClosableIterator<Pair>) storage.iterator(nodeList);
        assertNotNull("断言存储中的数据不为空。", testPairIterator);

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
        assertNotNull("断言指定虚拟节点中的数据被成功删除。", testPairIterator);
        assertFalse("断言指定虚拟节点中的数据被成功删除。", testPairIterator.hasNext());

        testPairIterator.close();
    }

    /**
     * cas，并发测试代码
     */
    public void t1estCas() {
        Thread t1 = new ThreadCas();
        Thread t2 = new ThreadSet();
        t1.start();
        t2.start();
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

    private class ThreadCas extends Thread {

        @Override
        public void run() {
            Storage storage = getStorage();
            Key key = createKey("test");
            Value value = createValue("啊荤万科假日巍峨峻岭");

            storage.set(key, value, true);
        }

    }

    protected Key createKey(String key) {
        return new KeyImpl(100, key, getVnode(key));
    }

    protected Value createValue(String value) {
        return new ValueImpl(ByteUtils.stringToByte(value), (new Date()).getTime());
    }

    private int getVnode(String key) {
        return Math.abs(key.hashCode() % virtualNode.length);
    }

    private int          len = 20;
    private static int[] virtualNode;
    {
        int vnodeNum = 20;
        virtualNode = new int[vnodeNum];
        for (int i = 0; i < vnodeNum; i++) {
            virtualNode[i] = i;
        }
    }
}

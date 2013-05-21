package com.alibaba.doris.dataserver.store.log.db;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.RandomUtils;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.KeyImpl;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.store.ClosableIterator;
import com.alibaba.doris.dataserver.store.log.BaseTestCase;
import com.alibaba.doris.dataserver.store.log.LogStorage;
import com.alibaba.doris.dataserver.store.log.db.impl.DefaultLogClumpImpl;
import com.alibaba.doris.dataserver.store.log.entry.LogEntry;
import com.alibaba.doris.dataserver.store.log.entry.SetLogEntry;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class LogStorageTest extends BaseTestCase {

    public void testIterator() throws IOException {
        clearDbPath();

        ClumpConfigure config = getClumpConfigure();
        LogStorage storage = new LogStorage(config);
        try {
            LogClump clump = new DefaultLogClumpImpl(config, "001");
            WriteWindow writeWindow = clump.getWriteWindow();
            writeWindow.append(getSetLogEntry(key1, "test", 1));
            writeWindow.close();

            clump = new DefaultLogClumpImpl(config, "002");
            writeWindow = clump.getWriteWindow();
            clump.getWriteWindow().append(getSetLogEntry(key2, "test2", 2));
            writeWindow.close();

            storage.open();
            ClosableIterator<Pair> iterator = (ClosableIterator<Pair>) storage.iterator();
            assertNotNull(iterator);
            Pair pair1 = iterator.next();
            Pair pair2 = iterator.next();
            assertNotNull(pair1);
            assertNotNull(pair2);

            assertEquals(key1, pair1.getKey().getKey());
            assertEquals(key2, pair2.getKey().getKey());
            assertFalse(iterator.hasNext());
            iterator.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            storage.close();
        }
    }

    private String key1 = "key1";
    private String key2 = "key2";
    private String key3 = "key3";

    /**
     * 注意：本测试依赖上一个测试方法，生成的数据。
     * 
     * @throws IOException
     */
    public void testDelete() throws IOException {
        clearDbPath();
        ClumpConfigure config = getClumpConfigure();
        LogStorage storage = new LogStorage(config);

        List<Integer> vnodeArray = new ArrayList<Integer>();
        vnodeArray.add(1);
        vnodeArray.add(2);
        boolean result = true;
        try {
            LogClump clump = new DefaultLogClumpImpl(config, "001");
            WriteWindow writeWindow = clump.getWriteWindow();
            writeWindow.append(getSetLogEntry(key1, "test", vnodeArray.get(0)));
            writeWindow.close();

            clump = new DefaultLogClumpImpl(config, "002");
            writeWindow = clump.getWriteWindow();
            clump.getWriteWindow().append(getSetLogEntry(key2, "test2", vnodeArray.get(1)));
            writeWindow.close();

            storage.open();

            assertTrue(storage.delete(vnodeArray));
            Thread.sleep(1000);
            ClosableIterator<Pair> iterator = (ClosableIterator<Pair>) storage.iterator();
            result = iterator.hasNext();
            iterator.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            storage.close();
        }

        assertFalse(result);
    }

    public void testIteratorByVnode() throws IOException {
        clearDbPath();

        ClumpConfigure config = getClumpConfigure();
        LogStorage storage = new LogStorage(config);
        try {
            LogClump clump = new DefaultLogClumpImpl(config, "001");
            WriteWindow writeWindow = clump.getWriteWindow();
            writeWindow.append(getSetLogEntry(key1, "test", 1));
            writeWindow.close();

            clump = new DefaultLogClumpImpl(config, "002");
            writeWindow = clump.getWriteWindow();
            writeWindow.append(getSetLogEntry(key2, "test2", 2));
            writeWindow.close();

            List<Integer> vnodeArray = new ArrayList<Integer>();
            vnodeArray.add(1);
            vnodeArray.add(2);

            storage.open();
            ClosableIterator<Pair> iterator = (ClosableIterator<Pair>) storage.iterator(vnodeArray);
            assertNotNull(iterator);
            Pair pair1 = iterator.next();
            Pair pair2 = iterator.next();
            assertNotNull(pair1);
            assertNotNull(pair2);
            assertEquals(key1, pair1.getKey().getKey());
            assertEquals(key2, pair2.getKey().getKey());
            assertFalse(iterator.hasNext());
            iterator.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            storage.close();
        }
    }

    public void testIteratorByVnode0() throws IOException {
        clearDbPath();

        ClumpConfigure config = getClumpConfigure();
        LogStorage storage = new LogStorage(config);
        try {
            LogClump clump = new DefaultLogClumpImpl(config, "001");
            WriteWindow writeWindow = clump.getWriteWindow();
            writeWindow.append(getSetLogEntry(key1, "test", 1));
            writeWindow.append(getSetLogEntry(key3, "test3", 3));
            writeWindow.close();

            clump = new DefaultLogClumpImpl(config, "002");
            writeWindow = clump.getWriteWindow();
            writeWindow.append(getSetLogEntry(key2, "test2", 2));
            writeWindow.close();

            List<Integer> vnodeArray = new ArrayList<Integer>();
            vnodeArray.add(1);
            vnodeArray.add(2);

            storage.open();
            LogEntry entry = getSetLogEntry(key2, "test2", 2);
            storage.set(entry.getKey(), entry.getValue());
            ClosableIterator<Pair> iterator = (ClosableIterator<Pair>) storage.iterator(vnodeArray);
            assertNotNull(iterator);
            Pair pair1 = iterator.next();
            Pair pair2 = iterator.next();
            assertNotNull(pair1);
            assertNotNull(pair2);
            assertEquals(key1, pair1.getKey().getKey());
            assertEquals(key2, pair2.getKey().getKey());
            iterator.next();
            assertFalse(iterator.hasNext());
            iterator.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            storage.close();
        }
    }

    public void testDelete1() throws IOException {
        clearDbPath();

        ClumpConfigure config = getClumpConfigure();
        LogStorage storage = new LogStorage(config);
        try {
            LogClump clump = new DefaultLogClumpImpl(config, "001");
            WriteWindow writeWindow = clump.getWriteWindow();
            writeWindow.append(getSetLogEntry(key1, "test", 1));
            writeWindow.append(getSetLogEntry(key2, "test2", 2));
            writeWindow.close();

            storage.open();
            List<Integer> vnodeArray = new ArrayList<Integer>();
            vnodeArray.add(1);
            assertTrue(storage.delete(vnodeArray));
            ClosableIterator<Pair> iterator = (ClosableIterator<Pair>) storage.iterator();
            // next assert true;
            assertTrue(iterator.hasNext());
            iterator.close();

            assertFalse(storage.delete(vnodeArray));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            storage.close();
        }
    }

    public void testDelete10000() throws IOException {
        clearDbPath();

        ClumpConfigure config = getClumpConfigure();
        LogStorage storage = new LogStorage(config);
        try {
            List<Integer> vnodeArray = new ArrayList<Integer>();
            LogClump clump = new DefaultLogClumpImpl(config, "001");
            WriteWindow writeWindow = clump.getWriteWindow();
            for (int i = 0; i < 10000; i++) {
                writeWindow.append(getSetLogEntry("key" + i, "test" + i, i));
                vnodeArray.add(i);
            }
            writeWindow.close();

            storage.open();
            ClosableIterator<Pair> iterator = (ClosableIterator<Pair>) storage.iterator();
            // next assert true;
            assertTrue(iterator.hasNext());
            iterator.close();

            assertTrue(storage.delete(vnodeArray));

            assertFalse(storage.delete(vnodeArray));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            storage.close();
        }
    }

    public void testConcurrentIterator() throws IOException, InterruptedException {
        clearDbPath();
        ClumpConfigure config = getClumpConfigure();
        LogStorage storage = new LogStorage(config);
        SetThread setThread = null;
        DeleteThread deleteThread = null;
        IteratorThread iteratorThread = null;
        try {
            storage.open();
            setThread = new SetThread(storage);
            setThread.start();

            deleteThread = new DeleteThread(storage);
            deleteThread.start();
            Thread.sleep(10000);

            iteratorThread = new IteratorThread(storage);
            iteratorThread.start();
            deleteThread.setSlowing();
            setThread.setSlowing();
            synchronized (iteratorThread) {
                iteratorThread.wait();
            }
            deleteThread.setStop(true);
            setThread.setStop(true);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            if (null != setThread) {
                setThread.setStop(true);
            }

            if (null != deleteThread) {
                deleteThread.setStop(true);
            }
            storage.close();
        }
        Thread.sleep(1000);
    }

    private class SetThread extends Thread {

        public SetThread(LogStorage storage) {
            this.storage = storage;
        }

        @Override
        public void run() {
            try {
                Key keyObj = null;
                Value valueObj = new ValueImpl("test value,test value".getBytes(), System.currentTimeMillis());
                int i = 0;
                while (!isStop) {
                    keyObj = new KeyImpl(DEFAULT_NAMESPACE, String.valueOf(i++), i);
                    storage.set(keyObj, valueObj);
                    if (!isSlowing) {
                        sleep(RandomUtils.nextInt(2));
                    } else {
                        sleep(20);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        public void setSlowing() {
            isSlowing = true;
        }

        public boolean isStop() {
            return isStop;
        }

        public void setStop(boolean isStop) {
            this.isStop = isStop;
        }

        private volatile boolean isStop    = false;
        private boolean          isSlowing = false;
        private LogStorage       storage;
    }

    private class IteratorThread extends Thread {

        public IteratorThread(LogStorage storage) {
            this.storage = storage;
        }

        @Override
        public void run() {
            List<Integer> vnodeList = new ArrayList<Integer>();
            for (int i = 0; i < 100; i++) {
                vnodeList.add(i);
            }

            for (int i = 1000; i < 2000; i++) {
                vnodeList.add(i);
            }

            int count = 0;
            ClosableIterator<Pair> iterator = (ClosableIterator<Pair>) storage.iterator(vnodeList);
            try {
                // next assert true;
                while (iterator.hasNext()) {
                    Pair p = iterator.next();
                    count++;
                    assertNotNull(p);
                    assertNotNull(p.getKey());
                    assertNotNull(p.getValue());
                }
            } catch (Exception e) {
                System.out.println("count=" + count);
                e.printStackTrace();
                fail();
            } finally {
                iterator.close();
            }

            try {
                synchronized (this) {
                    this.notifyAll();
                }
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

        private LogStorage storage;
    }

    private class DeleteThread extends Thread {

        public DeleteThread(LogStorage storage) {
            this.storage = storage;
        }

        @Override
        public void run() {
            try {
                while (!isStop) {
                    List<Integer> vnodeList = new ArrayList<Integer>();
                    vnodeList.add(RandomUtils.nextInt(100000));
                    storage.delete(vnodeList);
                    if (!isSlowing) {
                        sleep(RandomUtils.nextInt(2));
                    } else {
                        sleep(20);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

        public boolean isStop() {
            return isStop;
        }
        
        public void setSlowing() {
            isSlowing = true;
        }

        public void setStop(boolean isStop) {
            this.isStop = isStop;
        }

        private volatile boolean isStop    = false;
        private LogStorage       storage;
        private boolean          isSlowing = false;
    }

    public void testGet() throws IOException {
        clearDbPath();

        ClumpConfigure config = getClumpConfigure();
        LogStorage storage = new LogStorage(config);
        try {
            LogClump clump = new DefaultLogClumpImpl(config, "001");
            WriteWindow writeWindow = clump.getWriteWindow();
            writeWindow.append(getSetLogEntry("keytest", "testwerer", 1));
            writeWindow.append(getSetLogEntry("keytest1", "testwerer", 2));
            writeWindow.append(getSetLogEntry("keytest2", "testwerer", 1));

            writeWindow.append(getSetLogEntry(key1, "test", 1));
            writeWindow.close();

            clump = new DefaultLogClumpImpl(config, "002");
            writeWindow = clump.getWriteWindow();
            writeWindow.append(getSetLogEntry(key2, "test2", 2));
            writeWindow.close();

            storage.open();
            Key keyObj = new KeyImpl(DEFAULT_NAMESPACE, key1, 1);
            Value v = storage.get(keyObj);
            assertNotNull(v);
            assertEquals("test", ByteUtils.byteToString(v.getValueBytes()));

            keyObj = new KeyImpl(DEFAULT_NAMESPACE, key2, 1);
            v = storage.get(keyObj);
            assertNull("key2的虚拟节点编号为2，断言用vnode=1无法获取到数据。", v);
            keyObj = new KeyImpl(DEFAULT_NAMESPACE, key2, 2);
            v = storage.get(keyObj);
            assertNotNull(v);
            assertEquals("test2", ByteUtils.byteToString(v.getValueBytes()));

            keyObj = new KeyImpl(DEFAULT_NAMESPACE, "noexists_key", 1);
            v = storage.get(keyObj);
            assertNull("断言用一个storage中不存在的key去查询，将返回空值。", v);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            storage.close();
        }
    }

    public void testPutAndDelete() throws IOException {
        clearDbPath();

        ClumpConfigure config = getClumpConfigure();
        LogStorage storage = new LogStorage(config);
        try {
            storage.open();
            Key keyObj = new KeyImpl(DEFAULT_NAMESPACE, key1, 1);
            Value value = new ValueImpl("test".getBytes());
            storage.set(keyObj, value);

            keyObj = new KeyImpl(DEFAULT_NAMESPACE, key1, 1);
            Value v = storage.get(keyObj);
            assertNotNull(v);
            assertEquals("test", ByteUtils.byteToString(v.getValueBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            storage.close();
        }
    }

    public void testCheck() {
        ClumpConfigure config = getClumpConfigure();
        LogStorage storage = new LogStorage(config);
        try {
            storage.open();
            for (int i = 0; i < 10; i++) {
                ClosableIterator<Pair> iterator = (ClosableIterator<Pair>) storage.iterator();
                iterator.hasNext();
                iterator.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            storage.close();
        }
    }

    private LogEntry getSetLogEntry(String key, String value, int vnode) {
        Key keyObj = new KeyImpl(DEFAULT_NAMESPACE, key, vnode);
        Value valueObj = new ValueImpl(value.getBytes(), System.currentTimeMillis());
        return new SetLogEntry(keyObj, valueObj);
    }

    public void clearDbPath() throws IOException {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ClumpConfigure config = getClumpConfigure();
        String dbPath = config.getPath();
        FileUtils.deleteDirectory(new File(dbPath));
        File f = new File(dbPath);
        f.mkdir();
    }

    protected static final int DEFAULT_NAMESPACE = 1;
}

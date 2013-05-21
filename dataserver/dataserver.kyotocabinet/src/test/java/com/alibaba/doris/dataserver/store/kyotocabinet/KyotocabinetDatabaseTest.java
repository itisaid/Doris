package com.alibaba.doris.dataserver.store.kyotocabinet;

import java.util.Date;

import junit.framework.TestCase;

import com.alibaba.doris.common.config.ConfigTools;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.KeyImpl;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.store.ClosableIterator;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class KyotocabinetDatabaseTest extends TestCase {

    public void tes1tSet() {
        KyotocabinetStorageConfig config = getConfig();
        KyotocabinetDatabase db = new KyotocabinetDatabase("testset", config);
        try {
            db.open();
            Key key = createKey("key");
            Value value = createValue("value");
            db.set(key, value);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            db.close();
        }
    }

    public void tes1tGet() {
        KyotocabinetStorageConfig config = getConfig();
        KyotocabinetDatabase db = new KyotocabinetDatabase("testset", config);
        try {
            db.open();
            Key key = createKey("key");
            Value value = createValue("value");
            db.set(key, value);
            Value newValue = db.get(key);
            assertNotNull(newValue);
            assertEquals(newValue.getTimestamp(), value.getTimestamp());
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            db.close();
        }
    }

    public void tes1tDelete() {
        KyotocabinetStorageConfig config = getConfig();
        KyotocabinetDatabase database = new KyotocabinetDatabase("testset", config);
        try {
            database.open();
            Key key = createKey("key");
            Value value = createValue("value");
            database.set(key, value);
            Value newValue = database.get(key);
            assertNotNull(newValue);
            assertEquals(newValue.getTimestamp(), value.getTimestamp());

            assertTrue(database.delete(key));
            newValue = database.get(key);
            assertNull(newValue);

            assertFalse(database.delete(key));
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            database.close();
        }
    }

    public void tes1tIterator() {
        KyotocabinetStorageConfig config = getConfig();
        KyotocabinetDatabase database = new KyotocabinetDatabase("testset", config);
        try {
            database.open();
            Key key = createKey("key");
            Value value = createValue("value");
            database.set(key, value);

            Key key1 = createKey("key1");
            Value value1 = createValue("value1");
            database.set(key1, value1);

            Key key2 = createKey("key2");
            Value value2 = createValue("value2");
            database.set(key2, value2);

            ClosableIterator<Pair> closableIter = (ClosableIterator<Pair>) database.iterator();
            int index = 0;
            while (closableIter.hasNext()) {
                Pair p = closableIter.next();
                assertNotNull(p);
                assertNotNull(p.getKey());
                assertNotNull(p.getValue());
                if (index == 0) {
                    assertEquals(key, p.getKey());
                    assertEquals(value, p.getValue());
                }

                if (index == 1) {
                    assertEquals(key1, p.getKey());
                    assertEquals(value1, p.getValue());
                }

                if (index == 2) {
                    assertEquals(key2, p.getKey());
                    assertEquals(value2, p.getValue());
                }
                index++;
            }
            assertEquals(3, index);
            closableIter.close();
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            database.close();
        }
    }

    public void test0() {

    }

    private KyotocabinetStorageConfig getConfig() {
        KyotocabinetStorageConfig config = new KyotocabinetStorageConfig();
        String path = ConfigTools.getCurrentClassPath(this.getClass());
        path = path.substring(1);
        config.setDatabasePath(path);
        return config;
    }

    protected Key createKey(String key) {
        return new KeyImpl(100, key, 0);
    }

    protected Value createValue(String value) {
        return new ValueImpl(ByteUtils.stringToByte(value), (new Date()).getTime());
    }
}

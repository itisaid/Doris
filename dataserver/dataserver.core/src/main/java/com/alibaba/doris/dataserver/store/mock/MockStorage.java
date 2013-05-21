package com.alibaba.doris.dataserver.store.mock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.PairImpl;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.store.ClosableIterator;
import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.StorageType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class MockStorage implements Storage {

    public MockStorage(boolean isStoreDataInMem) {
        this.isStoreDataInMem = isStoreDataInMem;
    }

    public MockStorage() {
        this(true);
    }

    public void close() {
        storage = null;
    }

    public boolean delete(Key key) {
        sleep();
        if (isStoreDataInMem) {
            return storage.remove(new KeyWrapper(key)) != null;
        }
        return true;
    }

    public boolean delete(Key key, Value value) {
        sleep();
        return delete(key);
    }

    private void sleep() {
        // try {
        // Thread.sleep(60);
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }

    public boolean delete(List<Integer> vnodeList) {
        return false;
    }

    public Value get(Key key) {
        sleep();
        if (isStoreDataInMem) {
            return storage.get(new KeyWrapper(key));
        }

        return new ValueImpl(ByteUtils.stringToByte(key.getKey() + "sleep60"));
    }

    public Map<Key, Value> getAll(Iterable<Key> keyIterator) {
        sleep();
        if (isStoreDataInMem) {
            Map<Key, Value> retMap = new HashMap<Key, Value>();
            Iterator<Key> keyItr = keyIterator.iterator();
            while (keyItr.hasNext()) {
                Key key = keyItr.next();
                retMap.put(key, get(key));
            }
            return retMap;
        }
        return new HashMap<Key, Value>();
    }

    public StorageType getType() {
        return MockStorageType.MOCK_STORAGE;
    }

    public Iterator<Pair> iterator() {
        if (isStoreDataInMem) {
            final Iterator<Entry<KeyWrapper, Value>> localMapItr = storage.entrySet().iterator();
            Iterator<Pair> returnItr = new ClosableIterator<Pair>() {

                public boolean hasNext() {
                    return localMapItr.hasNext();
                }

                public Pair next() {
                    Entry<KeyWrapper, Value> entry = localMapItr.next();
                    return new PairImpl(entry.getKey().key, entry.getValue());
                }

                public void remove() {
                    localMapItr.remove();
                }

                public void close() {
                }
            };

            return returnItr;
        }
        return null;
    }

    public Iterator<Pair> iterator(List<Integer> vnodeList) {
        return iterator();
    }

    public void open() {
        storage = new ConcurrentHashMap<KeyWrapper, Value>();
    }

    public void set(Key key, Value value) {
        sleep();
        if (isStoreDataInMem) {
            storage.put(new KeyWrapper(key), value);
        }
    }

    public void set(Key key, Value value, boolean isSetWithCompareVersion) {
        sleep();
        if (isStoreDataInMem) {
            storage.put(new KeyWrapper(key), value);
        }
    }

    private Map<KeyWrapper, Value> storage = new HashMap<KeyWrapper, Value>();

    private static class KeyWrapper {

        public KeyWrapper(Key key) {
            this.key = key;
        }

        public Key getKey() {
            return key;
        }

        public void setKey(Key key) {
            this.key = key;
        }

        @Override
        public boolean equals(Object obj) {
            KeyWrapper keyObj = (KeyWrapper) obj;
            return this.key.getPhysicalKey().equals(keyObj.key.getPhysicalKey());
        }

        @Override
        public int hashCode() {
            return key.getPhysicalKey().hashCode();
        }

        @Override
        public String toString() {
            return "[Key:" + key.getPhysicalKey() + "]";
        }

        private Key key;
    }

    private boolean isStoreDataInMem = true;
}

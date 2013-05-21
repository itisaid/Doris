package com.alibaba.doris.dataserver.store.kyotocabinet;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import kyotocabinet.Cursor;
import kyotocabinet.DB;

import com.alibaba.doris.common.data.CompareStatus;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.PairImpl;
import com.alibaba.doris.dataserver.store.BaseStorage;
import com.alibaba.doris.dataserver.store.ClosableIterator;
import com.alibaba.doris.dataserver.store.StorageType;
import com.alibaba.doris.dataserver.store.exception.VersionConflictException;
import com.alibaba.doris.dataserver.store.serialize.KeyValueSerializerFactory;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class KyotocabinetDatabase extends BaseStorage {

    public KyotocabinetDatabase(String databaseName, KyotocabinetStorageConfig config) {
        this.databaseName = databaseName;
        this.configure = config;
        this.isOpen = false;
        database = new DB();
    }

    public synchronized void open() {
        if (!isOpen) {
            String fileName = getDatabaseFileName();
            // open the database
            if (!database.open(fileName, DB.OWRITER | DB.OCREATE)) {
                throw new KyotocabinetStorageException("open error: " + database.error());
            }
            isOpen = true;
        }
    }

    protected String getDatabaseFileName() {
        return configure.getDatabasePath() + File.separatorChar + getDatabaseName() + DB_SUFIX_HASH;
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public synchronized void close() {
        if (isOpen) {
            if (!database.close()) {
                throw new KyotocabinetStorageException("Close database " + databaseName + " failed!");
            }
            isOpen = false;
        }
    }

    public boolean delete(Key key) {
        byte[] keyBytes = serializerFactory.encode(key).copyBytes();
        return database.remove(keyBytes);
    }

    public boolean delete(Key key, Value value) {
        byte[] keyBytes = serializerFactory.encode(key).copyBytes();
        int tryTimes = 0;
        do {
            byte[] oldValueBytes = database.get(keyBytes);
            if (null != oldValueBytes) {
                Value oldValue = serializerFactory.decodeValue(oldValueBytes);
                if (oldValue.compareVersion(value) == CompareStatus.AFTER) {
                    throw new VersionConflictException("Key " + key.getKey() + " Namespace " + key.getNamespace() + " "
                                                       + value.getTimestamp()
                                                       + " is obsolete, it is no greater than the current version of "
                                                       + oldValue.getTimestamp() + ".");
                }

                if (database.cas(keyBytes, oldValueBytes, null)) {
                    return true;
                }

                tryTimes++;
                if (tryTimes >= 3) {
                    break;
                }

                sleep(1);
            } else {
                break;
            }
        } while (true);

        return false;
    }

    public boolean delete(List<Integer> vnodeList) {
        return false;
    }

    public Value get(Key key) {
        byte[] keyBytes = serializerFactory.encode(key).copyBytes();
        byte[] valueBytes = database.get(keyBytes);
        if (null != valueBytes) {
            return serializerFactory.decodeValue(valueBytes);
        }

        return null;
    }

    public void set(Key key, Value value) {
        set(key, value, false);
    }

    public void set(Key key, Value value, boolean isSetWithCompareVersion) {
        byte[] keyBytes = serializerFactory.encode(key).copyBytes();
        byte[] valueBytes = serializerFactory.encode(value).copyBytes();
        if (isSetWithCompareVersion) {
            int tryTimes = 0;
            do {
                byte[] oldValueBytes = database.get(keyBytes);
                if (null != oldValueBytes) {
                    Value oldValue = serializerFactory.decodeValue(oldValueBytes);
                    if (oldValue.compareVersion(value) == CompareStatus.AFTER) {
                        throw new VersionConflictException(
                                                           "Key "
                                                                   + key.getKey()
                                                                   + " Namespace "
                                                                   + key.getNamespace()
                                                                   + " "
                                                                   + value.getTimestamp()
                                                                   + " is obsolete, it is no greater than the current version of "
                                                                   + oldValue.getTimestamp() + ".");
                    }

                    if (database.cas(keyBytes, oldValueBytes, valueBytes)) {
                        break;
                    }

                    tryTimes++;
                    if (tryTimes >= 3) {
                        break;
                    }

                    sleep(1);
                } else {
                    if (!database.set(keyBytes, valueBytes)) {
                        throw new KyotocabinetStorageException("Set value error: " + database.error());
                    }
                    break;
                }
            } while (true);
        } else {
            if (!database.set(keyBytes, valueBytes)) {
                throw new KyotocabinetStorageException("Set value error: " + database.error());
            }
        }
    }

    public String getName() {
        return databaseName;
    }

    public StorageType getType() {
        return null;
    }

    public Iterator<Pair> iterator() {
        return new KyotocabinetIterator(database);
    }

    public Iterator<Pair> iterator(List<Integer> vnodeList) {
        return new KyotocabinetIterator(database);
    }

    private static class KyotocabinetIterator implements ClosableIterator<Pair> {

        public KyotocabinetIterator(DB db) {
            this.cursor = db.cursor();
            this.cursor.jump();
            this.currentPair = getNextPair();
        }

        public void close() {
            cursor.disable();
        }

        public boolean hasNext() {
            return currentPair != null;
        }

        public Pair next() {
            Pair cur = currentPair;
            currentPair = getNextPair();
            return cur;
        }

        public void remove() {
            cursor.remove();
        }

        private Pair getNextPair() {
            byte[] keyBytes = cursor.get_key(false);
            if (null != keyBytes) {
                byte[] valueBytes = cursor.get_value(true);
                Key k = serializerFactory.decodeKey(keyBytes);
                Value v = serializerFactory.decodeValue(valueBytes);
                return new PairImpl(k, v);
            }
            return null;
        }

        private Pair   currentPair;
        private Cursor cursor;
    }

    private void sleep(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ignore) {
            // TODO ignore exception
        }
    }

    private volatile boolean                       isOpen;
    private String                                 databaseName;
    private DB                                     database;
    private KyotocabinetStorageConfig              configure;
    private static final String                    DB_SUFIX_HASH     = ".kch";
    private static final KeyValueSerializerFactory serializerFactory = KeyValueSerializerFactory.getInstance();
}

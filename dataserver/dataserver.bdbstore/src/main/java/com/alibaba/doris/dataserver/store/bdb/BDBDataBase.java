package com.alibaba.doris.dataserver.store.bdb;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.data.ByteWrapper;
import com.alibaba.doris.common.data.CompareStatus;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.PairImpl;
import com.alibaba.doris.dataserver.store.BaseStorage;
import com.alibaba.doris.dataserver.store.ClosableIterator;
import com.alibaba.doris.dataserver.store.StorageType;
import com.alibaba.doris.dataserver.store.exception.StorageException;
import com.alibaba.doris.dataserver.store.exception.VersionConflictException;
import com.alibaba.doris.dataserver.store.serialize.KeyValueSerializerFactory;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class BDBDataBase extends BaseStorage {

    public BDBDataBase(Database db) {
        this.db = db;
        this.environment = db.getEnvironment();
        this.isOpen = new AtomicBoolean(true);
    }

    public void close() {
        if (isOpen.compareAndSet(true, false)) {
            this.db.close();
        }
    }

    public void open() {
        // Nothing to do;
    }

    public Value get(Key key) {
        return get(key, LockMode.READ_UNCOMMITTED);
    }

    public Database getDatabase() {
        return this.db;
    }

    private Value get(Key key, LockMode lockMode) {
        Cursor cursor = null;
        try {
            cursor = db.openCursor(null, null);

            ByteWrapper keyBytes = serializerFactory.encode(key);
            DatabaseEntry keyEntry = new DatabaseEntry(keyBytes.getBytes(), keyBytes.getStartPos(), keyBytes.getLen());
            DatabaseEntry valueEntry = new DatabaseEntry();
            OperationStatus status = cursor.getSearchKey(keyEntry, valueEntry, lockMode);
            while (status == OperationStatus.SUCCESS) {
                return serializerFactory.decodeValue(valueEntry.getData());
                // status = cursor.getNextDup(keyEntry, valueEntry, lockMode);
            }

            return null;
        } catch (DatabaseException e) {
            throw new BDBStorageException(e);
        } finally {
            closeCursor(cursor);
        }
    }

    public void set(Key key, Value value, boolean isSetWithCompareVersion) {
        ByteWrapper keyBytes = serializerFactory.encode(key);
        DatabaseEntry keyEntry = new DatabaseEntry(keyBytes.getBytes(), keyBytes.getStartPos(), keyBytes.getLen());
        boolean isSuccess = false;
        Transaction transaction = null;
        Cursor cursor = null;

        try {
            transaction = this.environment.beginTransaction(null, null);

            // Check existing values
            cursor = db.openCursor(transaction, null);
            if (isSetWithCompareVersion) {
                DatabaseEntry readValueEntry = new DatabaseEntry();
                OperationStatus status = cursor.getSearchKey(keyEntry, readValueEntry, LockMode.RMW);
                if (status == OperationStatus.SUCCESS) {
                    Value v = serializerFactory.decodeValue(readValueEntry.getData());
                    if (v.compareVersion(value) == CompareStatus.AFTER) {
                        throw new VersionConflictException(
                                                           "Key "
                                                                   + key.getKey()
                                                                   + " Namespace "
                                                                   + key.getNamespace()
                                                                   + " "
                                                                   + value.getTimestamp()
                                                                   + " is obsolete, it is no greater than the current version of "
                                                                   + v.getTimestamp() + ".");
                    }
                    // cursor.delete();//
                }
            }

            // Everything is okay, insert the new value.
            ByteWrapper valueBytes = serializerFactory.encode(value);
            DatabaseEntry writeValueEntry = new DatabaseEntry(valueBytes.getBytes(), valueBytes.getStartPos(),
                                                              valueBytes.getLen());
            OperationStatus putStatus = cursor.put(keyEntry, writeValueEntry);
            if (putStatus != OperationStatus.SUCCESS) {
                throw new StorageException("Put operation failed with status: " + putStatus);
            }

            isSuccess = true;
        } catch (DatabaseException e) {
            throw new StorageException(e);
        } finally {
            closeCursor(cursor);
            if (isSuccess) {
                commit(transaction);
            } else {
                rollback(transaction);
            }
        }
    }

    public void set(Key key, Value value) {
        set(key, value, false);
    }

    public boolean delete(Key key, Value value) {
        boolean isDeleteSuccess = false;
        Cursor cursor = null;
        Transaction transaction = null;
        try {
            transaction = this.environment.beginTransaction(null, null);
            ByteWrapper keyBytes = serializerFactory.encode(key);
            DatabaseEntry keyEntry = new DatabaseEntry(keyBytes.getBytes(), keyBytes.getStartPos(), keyBytes.getLen());
            DatabaseEntry valueEntry = new DatabaseEntry();
            cursor = this.db.openCursor(transaction, null);
            OperationStatus status = cursor.getSearchKey(keyEntry, valueEntry, LockMode.READ_UNCOMMITTED);
            while (status == OperationStatus.SUCCESS) {
                if (null != value) {
                    Value v = serializerFactory.decodeValue(valueEntry.getData());
                    if (v.compareVersion(value) == CompareStatus.AFTER) {
                        throw new VersionConflictException(
                                                           "Key "
                                                                   + key.getKey()
                                                                   + " Namespace "
                                                                   + key.getNamespace()
                                                                   + " "
                                                                   + value.getTimestamp()
                                                                   + " is obsolete, it is no greater than the current version of "
                                                                   + v.getTimestamp() + ".");
                    }
                }
                cursor.delete();
                status = cursor.getNextDup(keyEntry, valueEntry, LockMode.READ_UNCOMMITTED);
                isDeleteSuccess = true;
            }
            return isDeleteSuccess;
        } catch (DatabaseException e) {
            throw new BDBStorageException(e);
        } finally {
            try {
                closeCursor(cursor);
            } finally {
                commit(transaction);
            }
        }
    }

    public boolean delete(Key key) {
        return delete(key, null);
    }

    public boolean delete(List<Integer> vnodeList) {
        Integer[] vnodeArray = converVnodeListToArray(vnodeList);
        Cursor cursor = null;
        try {
            // transaction = this.environment.beginTransaction(null, null);
            cursor = this.db.openCursor(null, null);
            DatabaseEntry oldKey = new DatabaseEntry();
            DatabaseEntry foundKey = new DatabaseEntry();
            DatabaseEntry tempKey = null;

            DatabaseEntry foundData = new DatabaseEntry();
            while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                Key key = serializerFactory.decodeKey(foundKey.getData());
                if (vnodeArray[(key.getVNode())] != null) {
                    // cursor.delete();
                    if (oldKey.getData() != null) {
                        db.delete(null, oldKey);
                        oldKey.setData(null);
                    }

                    tempKey = oldKey;
                    oldKey = foundKey;
                    foundKey = tempKey;
                }
            }

            closeCursor(cursor);
            cursor = null;
            if (oldKey.getData() != null) {
                db.delete(null, oldKey);
            }
            return true;
        } catch (DatabaseException e) {
            logger.error("Delete data failed. The list of vnode is " + vnodeList, e);
            throw new BDBStorageException(e);
        } finally {
            try {
                closeCursor(cursor);
            } finally {
                // commit(transaction);
            }
        }
    }

    private Integer[] converVnodeListToArray(List<Integer> vnodeList) {
        Integer[] vnodeArray = new Integer[vnodeList.size()];
        vnodeArray = vnodeList.toArray(vnodeArray);

        // To find the max vnode number of the vnodeList;
        Arrays.sort(vnodeArray);
        int max = vnodeArray[vnodeArray.length - 1];

        // Converting the VNODE into node array. to save memory and improve the speed of search.
        Integer[] vnodeArrayTemp = new Integer[max + 1];

        for (Integer vnode : vnodeList) {
            vnodeArrayTemp[vnode.intValue()] = vnode;
        }

        return vnodeArrayTemp;
    }

    private void closeCursor(Cursor cursor) {
        try {
            if (cursor != null) {
                cursor.close();
            }
        } catch (DatabaseException e) {
            throw new BDBStorageException(e.getMessage(), e);
        }
    }

    private void commit(Transaction transaction) {
        try {
            transaction.commit();
        } catch (DatabaseException e) {
            rollback(transaction);
            throw new BDBStorageException(e.getMessage(), e);
        }
    }

    private void rollback(Transaction transaction) {
        try {
            if (transaction != null) {
                transaction.abort();
            }
        } catch (Exception e) {
            throw new BDBStorageException(e.getMessage(), e);
        }
    }

    public Iterator<Pair> iterator() {
        try {
            Cursor cursor = this.db.openCursor(null, null);
            return new BDBPairCursorIteraotr(cursor, null);
        } catch (DatabaseException e) {
            logger.error("", e);
            throw new BDBStorageException(e);
        }
    }

    public Iterator<Pair> iterator(List<Integer> vnodeList) {
        try {
            Cursor cursor = this.db.openCursor(null, null);
            return new BDBPairCursorIteraotr(cursor, vnodeList);
        } catch (DatabaseException e) {
            logger.error("", e);
            throw new BDBStorageException(e);
        }
    }

    public StorageType getType() {
        return null;
    }

    private class BDBPairCursorIteraotr extends BDBCursorIterator<Pair> {

        public BDBPairCursorIteraotr(Cursor cursor, List<Integer> vnodeList) {
            super(cursor, false);
            if (null != vnodeList && vnodeList.size() > 0) {
                vnodeArray = converVnodeListToArray(vnodeList);
            }
        }

        @Override
        protected Pair get(DatabaseEntry key, DatabaseEntry value) {
            Key k = serializerFactory.decodeKey(key.getData());

            if (null != vnodeArray) {
                if (vnodeArray[k.getVNode()] != null) {
                    Value v = serializerFactory.decodeValue(value.getData());
                    return new PairImpl(k, v);
                }

                return null;
            }

            Value v = serializerFactory.decodeValue(value.getData());
            return new PairImpl(k, v);
        }

        private Integer[] vnodeArray;
    }

    private static abstract class BDBCursorIterator<T> implements ClosableIterator<T> {

        public BDBCursorIterator(Cursor cursor, boolean noValues) {
            this.cursor = cursor;
            isOpen = true;
            this.noValues = noValues;
            DatabaseEntry keyEntry = new DatabaseEntry();
            DatabaseEntry valueEntry = new DatabaseEntry();
            if (noValues) {
                valueEntry.setPartial(true);
            }

            try {
                cursor.getFirst(keyEntry, valueEntry, LockMode.READ_UNCOMMITTED);
            } catch (DatabaseException e) {
                logger.error("", e);
                throw new BDBStorageException(e);
            }

            if (keyEntry.getData() != null) {
                currentValue = getRecord(keyEntry, valueEntry);
            }
        }

        protected abstract T get(DatabaseEntry key, DatabaseEntry value);

        protected T getRecord(DatabaseEntry keyEntry, DatabaseEntry valueEntry) {
            T tempValue = null;

            do {
                tempValue = get(keyEntry, valueEntry);
                if (null != tempValue) {
                    break;
                }
            } while (moveCursor(keyEntry, valueEntry));

            return tempValue;
        }

        protected boolean moveCursor(DatabaseEntry key, DatabaseEntry value) throws DatabaseException {
            return cursor.getNextNoDup(key, value, LockMode.READ_UNCOMMITTED) == OperationStatus.SUCCESS;
        }

        public final boolean hasNext() {
            return currentValue != null;
        }

        public final T next() {
            if (!isOpen) {
                throw new BDBStorageException("Call to next() on a closed iterator.");
            }

            DatabaseEntry keyEntry = new DatabaseEntry();
            DatabaseEntry valueEntry = new DatabaseEntry();
            if (noValues) {
                valueEntry.setPartial(true);
            }

            try {
                moveCursor(keyEntry, valueEntry);
            } catch (DatabaseException e) {
                logger.error("", e);
                throw new BDBStorageException(e);
            }

            T previous = currentValue;
            if (keyEntry.getData() == null) {
                currentValue = null;
            } else {
                currentValue = getRecord(keyEntry, valueEntry);
            }

            return previous;
        }

        public final void remove() {
            throw new UnsupportedOperationException("No remove.");
        }

        public final void close() {
            try {
                cursor.close();
                isOpen = false;
            } catch (DatabaseException e) {
                logger.error("", e);
            }
        }

        @Override
        protected final void finalize() {
            if (isOpen) {
                logger.error("Failure to close cursor, will be forcably closed.");
                close();
            }
        }

        private final boolean    noValues;
        private final Cursor     cursor;
        private T                currentValue;
        private volatile boolean isOpen;
    }

    private Environment                            environment;
    private Database                               db;
    private final AtomicBoolean                    isOpen;
    private static final KeyValueSerializerFactory serializerFactory = KeyValueSerializerFactory.getInstance();
    private static final Logger                    logger            = LoggerFactory.getLogger(BDBDataBase.class);
}

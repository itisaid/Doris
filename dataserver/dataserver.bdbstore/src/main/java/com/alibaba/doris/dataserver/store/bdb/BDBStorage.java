package com.alibaba.doris.dataserver.store.bdb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.dataserver.store.BaseStorage;
import com.alibaba.doris.dataserver.store.StorageType;
import com.alibaba.doris.dataserver.store.exception.StorageException;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.LockTimeoutException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class BDBStorage extends BaseStorage {

    public BDBStorage(BDBDataBaseFactory databaseFactory) {
        this.databaseFactory = databaseFactory;
    }

    public void close() {
        // 刷新cache,释放资源
        Set<Entry<String, BDBDataBase>> entrySet = databaseMap.entrySet();
        Iterator<Entry<String, BDBDataBase>> itr = entrySet.iterator();
        while (itr.hasNext()) {
            itr.next().getValue().close();
        }

        List<Environment> envList = databaseFactory.getEnvironmentList();
        for (Environment env : envList) {
            env.close();
        }
    }

    public void open() {
        // 打开文件，如果是第一次启动，需要初始化文件信息。
        List<Environment> envList = databaseFactory.getEnvironmentList();
        // load 所有已经存在的database。
        for (Environment localEnv : envList) {
            for (String name : localEnv.getDatabaseNames()) {
                getDataBase(name);
            }
        }
    }

    public Value get(Key key) {
        if (null == key || StringUtils.isBlank(key.getKey())) {
            throw new IllegalArgumentException("Key can't be null!");
        }

        BDBDataBase db = getDataBase(getDatabaseName(key));
        Value value = db.get(key);

        if (logger.isDebugEnabled()) {
            if (value != null) {
                logger.debug("get: key=" + key + " timestamp=" + value.getTimestamp());
            } else {
                logger.debug("get: key=" + key + " value is null.");
            }
        }

        return value;
    }

    public Map<Key, Value> getAll(Iterable<Key> keyIterator) {
        int size = ((Collection<?>) keyIterator).size();
        Map<Key, Value> result = new HashMap<Key, Value>(size);
        for (Key key : keyIterator) {
            Value value = get(key);
            if (value != null) {
                result.put(key, value);
            }
        }
        return result;
    }

    public void set(Key key, Value value) {
        BDBDataBase db = getDataBase(getDatabaseName(key));
        db.set(key, value);
    }

    public void set(Key key, Value value, boolean isSetWithCompareVersion) {
        if (null == key || StringUtils.isBlank(key.getKey())) {
            throw new IllegalArgumentException("Key can't be null!");
        }

        if (null == value) {
            throw new IllegalArgumentException("Value can't be null!");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("set: key=" + key + " timestamp=" + value.getTimestamp());
        }

        BDBDataBase db = getDataBase(getDatabaseName(key));
        try {
            db.set(key, value, isSetWithCompareVersion);
        } catch (LockTimeoutException writeFailedException) {
            // get lock failed, retry.
            db.set(key, value, isSetWithCompareVersion);
        }
    }

    public StorageType getType() {
        return BDBStorageDriver.type;
    }

    public boolean delete(Key key) {
        if (null == key || StringUtils.isBlank(key.getKey())) {
            throw new IllegalArgumentException("Key can't be null!");
        }

        BDBDataBase db = getDataBase(getDatabaseName(key));
        try {
            return db.delete(key);
        } catch (LockTimeoutException writeFailedException) {
            // get lock failed, retry.
            return db.delete(key);
        }
    }

    public boolean delete(Key key, Value value) {
        if (null == key || StringUtils.isBlank(key.getKey())) {
            throw new IllegalArgumentException("Key can't be null!");
        }

        if (null == value) {
            throw new IllegalArgumentException("Value can't be null!");
        }

        BDBDataBase db = getDataBase(getDatabaseName(key));
        try {
            return db.delete(key, value);
        } catch (LockTimeoutException writeFailedException) {
            // get lock failed, retry.
            return db.delete(key, value);
        }
    }

    public boolean delete(List<Integer> vnodeList) {
        boolean isSuccess = false;
        if (null == vnodeList || vnodeList.size() <= 0) {
            throw new IllegalArgumentException("Invalid argument. The vnodeList can't be empty list.");
        }

        String dbName = getDatabaseName(vnodeList.get(0));

        if (SINGLE_DATA_BASE_NAME.equals(dbName)) {
            BDBDataBase db = getDataBase(dbName);
            if (null != db) {
                isSuccess = db.delete(vnodeList);
            } else {
                throw new StorageException("Couldn't find the database for name [" + dbName + "]");
            }
        } else {
            isSuccess = true;
            for (Integer vnode : vnodeList) {
                dbName = getDatabaseName(vnode.intValue());
                BDBDataBase db = getDataBase(dbName);
                if (null != db) {
                    if (!db.delete(vnodeList)) {
                        isSuccess = false;
                        break;
                    } else {
                        if (databaseFactory.removeDataBase(db)) {
                            databaseMap.remove(dbName);
                        }
                    }
                }
            }
        }
        return isSuccess;
    }

    public Iterator<Pair> iterator() {
        return new MultiDataBaseIterator<BDBDataBase>(databaseMap.values().iterator(), null);
    }

    public Iterator<Pair> iterator(List<Integer> vnodeList) {
        List<BDBDataBase> dbList = new ArrayList<BDBDataBase>(vnodeList.size());
        for (Integer vnode : vnodeList) {
            BDBDataBase localDb = databaseMap.get(getDatabaseName(vnode.intValue()));
            if (null != localDb && !dbList.contains(localDb)) {
                dbList.add(localDb);
            }
        }

        return new MultiDataBaseIterator<BDBDataBase>(dbList.iterator(), vnodeList);
    }

    protected String getDatabaseName(Key key) {
        // int node = virtualRouter.findVirtualNode((String) key.getKey());
        // return getDatabaseName(node);
        return SINGLE_DATA_BASE_NAME;
    }

    protected String getDatabaseName(int vnode) {
        // return String.valueOf(vnode);
        return SINGLE_DATA_BASE_NAME;
    }

    protected List<String> getDatabaseName(List<Integer> vnodeList) {
        List<String> dsList = new ArrayList<String>(vnodeList.size());
        for (Integer node : vnodeList) {
            dsList.add(node.toString());
        }

        return dsList;
    }

    protected BDBDataBase getDataBase(String databaseName) {
        BDBDataBase bdb = databaseMap.get(databaseName);
        if (null != bdb) {
            return bdb;
        }

        try {
            synchronized (lock) {
                bdb = databaseMap.get(databaseName);
                if (null != bdb) {
                    return bdb;
                }
                bdb = databaseFactory.createDataBase(databaseName);
                databaseMap.put(databaseName, bdb);
            }
        } catch (DatabaseException e) {
            throw new BDBStorageException(e);
        }

        return bdb;
    }

    private Map<String, BDBDataBase> databaseMap           = new ConcurrentHashMap<String, BDBDataBase>();
    private Object                   lock                  = new Object();
    private BDBDataBaseFactory       databaseFactory;
    private static final Logger      logger                = LoggerFactory.getLogger(BDBStorage.class);
    private static final String      SINGLE_DATA_BASE_NAME = "0";
}

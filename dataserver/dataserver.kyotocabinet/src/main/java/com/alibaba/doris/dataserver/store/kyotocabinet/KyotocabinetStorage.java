package com.alibaba.doris.dataserver.store.kyotocabinet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.dataserver.store.BaseStorage;
import com.alibaba.doris.dataserver.store.StorageType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class KyotocabinetStorage extends BaseStorage {

    public KyotocabinetStorage(KyotocabinetStorageConfig config) {
        this.config = config;
        this.dbFactory = new KyotocabinetDBFactory(this.config);
    }

    public synchronized void close() {
        Set<Entry<String, KyotocabinetDatabase>> dbset = this.databaseMap.entrySet();
        for (Entry<String, KyotocabinetDatabase> entry : dbset) {
            entry.getValue().close();
        }
        this.databaseMap.clear();
    }

    public void open() {
        // load all exits database?
        ;
    }

    public boolean delete(Key key) {
        String databaseName = getDatabaseName(key.getVNode());
        KyotocabinetDatabase database = getDatabase(databaseName);
        return database.delete(key);
    }

    public boolean delete(Key key, Value value) {
        String databaseName = getDatabaseName(key.getVNode());
        KyotocabinetDatabase database = getDatabase(databaseName);
        return database.delete(key, value);
    }

    public boolean delete(List<Integer> vnodeList) {
        boolean isSuccess = false;
        for (Integer vnode : vnodeList) {
            String dbName = getDatabaseName(vnode.intValue());
            KyotocabinetDatabase db = getDatabase(dbName);
            if (null != db) {
                if (dbFactory.removeDatabase(db)) {
                    databaseMap.remove(dbName);
                    isSuccess = true;
                }
            }
        }

        return isSuccess;
    }

    public Value get(Key key) {
        String databaseName = getDatabaseName(key.getVNode());
        KyotocabinetDatabase database = getDatabase(databaseName);
        return database.get(key);
    }

    public StorageType getType() {
        return KyotocabinetStorageType.KYOTOCABINET;
    }

    public Iterator<Pair> iterator() {
        return new MultiDataBaseIterator<KyotocabinetDatabase>(databaseMap.values().iterator(), null);
    }

    public Iterator<Pair> iterator(List<Integer> vnodeList) {
        List<KyotocabinetDatabase> dbList = new ArrayList<KyotocabinetDatabase>(vnodeList.size());
        for (Integer vnode : vnodeList) {
            KyotocabinetDatabase localDb = databaseMap.get(getDatabaseName(vnode.intValue()));
            if (null != localDb) {
                dbList.add(localDb);
            }
        }

        return new MultiDataBaseIterator<KyotocabinetDatabase>(dbList.iterator(), vnodeList);
    }

    public void set(Key key, Value value) {
        String databaseName = getDatabaseName(key.getVNode());
        KyotocabinetDatabase database = getDatabase(databaseName);
        database.set(key, value);
    }

    public void set(Key key, Value value, boolean isSetWithCompareVersion) {
        String databaseName = getDatabaseName(key.getVNode());
        KyotocabinetDatabase database = getDatabase(databaseName);
        database.set(key, value, isSetWithCompareVersion);
    }

    private String getDatabaseName(int vnode) {
        // return "0000000";
        return String.format("%05d", vnode);
    }

    private synchronized KyotocabinetDatabase getDatabase(String databaseName) {
        KyotocabinetDatabase database = databaseMap.get(databaseName);
        if (null != database) {
            return database;
        }

        database = dbFactory.createKyotocabinetDatabase(databaseName);
        database.open();
        databaseMap.put(databaseName, database);
        return database;
    }

    private KyotocabinetDBFactory             dbFactory;
    private KyotocabinetStorageConfig         config;
    private Map<String, KyotocabinetDatabase> databaseMap = new ConcurrentHashMap<String, KyotocabinetDatabase>();
}

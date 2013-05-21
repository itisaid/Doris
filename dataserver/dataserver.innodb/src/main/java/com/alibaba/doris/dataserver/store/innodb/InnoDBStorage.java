package com.alibaba.doris.dataserver.store.innodb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.dataserver.store.BaseStorage;
import com.alibaba.doris.dataserver.store.StorageType;
import com.alibaba.doris.dataserver.store.innodb.config.InnoDBDatabaseConfiguration;
import com.alibaba.doris.dataserver.store.innodb.db.InnoDBBuilder;
import com.alibaba.doris.dataserver.store.innodb.db.InnoDBDataBase;
import com.alibaba.doris.dataserver.store.innodb.write.InnoWriteThread;
import com.alibaba.doris.dataserver.store.innodb.write.WriteOperation;
import com.alibaba.doris.dataserver.store.innodb.write.WriteType;

/**
 * @author long.mal long.mal@alibaba-inc.com
 */
public class InnoDBStorage extends BaseStorage {
	
    public InnoDBStorage(InnoDBDatabaseConfiguration config) {
        this.config = config;
    }

    public void open() {
        builder = new InnoDBBuilder(config);
        builder.initDataBase();
        
        CountDownLatch latch = new CountDownLatch(config.getWriteThread());
        startWriteThread(latch);
        try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

    public void close() {
    	endWriteThread();
    	builder.getDatabase().shutdown(false);
    }
    
    private void startWriteThread(CountDownLatch latch) {
    	executors = Executors.newFixedThreadPool(config.getWriteThread());
    	operationQueues = new ConcurrentHashMap<String, BlockingQueue<WriteOperation>>(config.getWriteThread());
    	for (int i = 0; i < config.getWriteThread(); i++) {
    		BlockingQueue<WriteOperation> writeOps = new ArrayBlockingQueue<WriteOperation>(1000);
    		InnoWriteThread writeThread = new InnoWriteThread(writeOps, latch);
    		operationQueues.put(Integer.toString(i), writeOps);
    		executors.submit(writeThread);
    	}
    }
    
    private void endWriteThread() {
    	executors.shutdownNow();
    }

    public boolean delete(List<Integer> vnodeList) {
        boolean isSuccess = false;
        if (null == vnodeList || vnodeList.size() <= 0) {
            throw new IllegalArgumentException("Invalid argument. The vnodeList can't be empty list.");
        }

        for (Integer vnode : vnodeList) {
            String dbName = getDatabaseName(vnode.intValue());
            InnoDBDataBase db = getInnoDBDataBase(dbName);
            if (null != db) {
                // db.delete(vnodeList);
                if (builder.deleteInnoDBDataBase(db)) {
                    databaseMap.remove(dbName);
                    isSuccess = true;
                }
            }
        }
        return isSuccess;
    }

    public Value get(Key key) {
        String databaseName = getDatabaseName(key);
        InnoDBDataBase db = getInnoDBDataBase(databaseName);
        return db.get(key);
    }

    public StorageType getType() {
        return InnoDBStorageType.INNODB;
    }

    public Iterator<Pair> iterator() {
        return new MultiDataBaseIterator<InnoDBDataBase>(databaseMap.values().iterator(), null);
    }

    public Iterator<Pair> iterator(List<Integer> vnodeList) {
        List<InnoDBDataBase> dbList = new ArrayList<InnoDBDataBase>(vnodeList.size());
        for (Integer vnode : vnodeList) {
            InnoDBDataBase localDb = databaseMap.get(getDatabaseName(vnode.intValue()));
            if (null != localDb && !dbList.contains(localDb)) {
                dbList.add(localDb);
            }
        }

        return new MultiDataBaseIterator<InnoDBDataBase>(dbList.iterator(), vnodeList);
    }

    public void set(Key key, Value value) {
        String databaseName = getDatabaseName(key);
        InnoDBDataBase db = getInnoDBDataBase(databaseName);
        int correspondingQueueID = key.getVNode() % config.getWriteThread();
        BlockingQueue<WriteOperation> writeOpQueue = operationQueues.get(String.valueOf(correspondingQueueID));
        Semaphore semaphore = new Semaphore(0);
        WriteOperation operation = new WriteOperation(db, WriteType.INSERT, key, value, false, semaphore);
        
    	try {
			writeOpQueue.put(operation);
        	Object result = operation.getResult();
        	if (result instanceof RuntimeException)
        		throw (RuntimeException) result;
		} catch (InterruptedException e) {
			System.out.println(correspondingQueueID);
			e.printStackTrace();
		}
    }

    public void set(Key key, Value value, boolean isSetWithCompareVersion) {
        String databaseName = getDatabaseName(key);
        InnoDBDataBase db = getInnoDBDataBase(databaseName);
        int correspondingQueueID = key.getVNode() % config.getWriteThread();
        BlockingQueue<WriteOperation> writeOpQueue = operationQueues.get(String.valueOf(correspondingQueueID));
        Semaphore semaphore = new Semaphore(0);
        WriteOperation operation = new WriteOperation(db, WriteType.INSERT, key, value, isSetWithCompareVersion, semaphore);
        
    	try {
			writeOpQueue.put(operation);
        	Object result = operation.getResult();
        	if (result instanceof RuntimeException)
        		throw (RuntimeException) result;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    public boolean delete(Key key) {
        String databaseName = getDatabaseName(key);
        InnoDBDataBase db = getInnoDBDataBase(databaseName);
        int correspondingQueueID = key.getVNode() % config.getWriteThread();
        BlockingQueue<WriteOperation> writeOpQueue = operationQueues.get(String.valueOf(correspondingQueueID));
        Semaphore semaphore = new Semaphore(0);
        WriteOperation operation = new WriteOperation(db, WriteType.DELETE, key, null, false, semaphore);
        
    	try {
			writeOpQueue.put(operation);
        	Object result = operation.getResult();
        	if (result instanceof RuntimeException)
        		throw (RuntimeException) result;
        	return (Boolean) result;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	return false;
    }

    public boolean delete(Key key, Value value) {
        String databaseName = getDatabaseName(key);
        InnoDBDataBase db = getInnoDBDataBase(databaseName);
        int correspondingQueueID = key.getVNode() % config.getWriteThread();
        BlockingQueue<WriteOperation> writeOpQueue = operationQueues.get(String.valueOf(correspondingQueueID));
        Semaphore semaphore = new Semaphore(0);
        WriteOperation operation = new WriteOperation(db, WriteType.DELETE, key, value, false, semaphore);
        
    	try {
			writeOpQueue.put(operation);
        	Object result = operation.getResult();
        	if (result instanceof RuntimeException) {
        		throw (RuntimeException) result;
        	}
        	return (Boolean) result;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	return false;
    }

    protected String getDatabaseName(Key key) {
        return getDatabaseName(key.getVNode());
    }

    protected String getDatabaseName(int vnode) {
        return String.valueOf(vnode);
    }

    protected List<String> getDatabaseName(List<Integer> vnodeList) {
        List<String> dsList = new ArrayList<String>(vnodeList.size());
        for (Integer node : vnodeList) {
            dsList.add(node.toString());
        }

        return dsList;
    }

    protected InnoDBDataBase getInnoDBDataBase(String databaseName) {
        InnoDBDataBase db = databaseMap.get(databaseName);
        if (null != db) {
            return db;
        }

        try {
            synchronized (lock) {
                db = databaseMap.get(databaseName);
                if (null != db) {
                    return db;
                }
                db = builder.buildInnoDBDataBase(databaseName);
                databaseMap.put(databaseName, db);
            }
        } catch (Exception e) {
            throw new InnoDBStorageException(e);
        }

        return db;
    }

    private ExecutorService             executors;
    private Map<String, BlockingQueue<WriteOperation>> operationQueues; 
    private Object                      lock        = new Object();
    private Map<String, InnoDBDataBase> databaseMap = new ConcurrentHashMap<String, InnoDBDataBase>();
    private InnoDBDatabaseConfiguration config;
    private InnoDBBuilder               builder;
}

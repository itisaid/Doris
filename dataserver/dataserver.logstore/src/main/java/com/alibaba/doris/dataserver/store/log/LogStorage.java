package com.alibaba.doris.dataserver.store.log;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.dataserver.store.ClosableIterator;
import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.StorageType;
import com.alibaba.doris.dataserver.store.log.db.BatchWriteThread;
import com.alibaba.doris.dataserver.store.log.db.ClumpConfigure;
import com.alibaba.doris.dataserver.store.log.db.LogClump;
import com.alibaba.doris.dataserver.store.log.db.LogClumpManager;
import com.alibaba.doris.dataserver.store.log.db.LogClumpsIterator;
import com.alibaba.doris.dataserver.store.log.db.LogCommand;
import com.alibaba.doris.dataserver.store.log.db.impl.AppendLogCommand;
import com.alibaba.doris.dataserver.store.log.db.impl.DeleteLogByVnodesCommand;
import com.alibaba.doris.dataserver.store.log.db.impl.ExitLogCommand;
import com.alibaba.doris.dataserver.store.log.entry.DeleteLogEntry;
import com.alibaba.doris.dataserver.store.log.entry.LogEntry;
import com.alibaba.doris.dataserver.store.log.entry.SetLogEntry;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class LogStorage implements Storage {

    public LogStorage(ClumpConfigure config) {
        this.config = config;
    }

    public void close() {
        clumpManager.releaseAllResources();
        isClosed = true;
        addCommand(new ExitLogCommand());
    }

    public void open() {
        checkDatabasePath();
        clumpManager = new LogClumpManager(config);
        logCommandQueue = new ArrayBlockingQueue<LogCommand>(1000);
        batchWriteThread = new BatchWriteThread(logCommandQueue, clumpManager);
        isClosed = false;
        batchWriteThread.start();
    }

    public boolean delete(Key key) {
        if (!isClosed) {
            LogEntry logEntry = new DeleteLogEntry(key, new ValueImpl(null, System.currentTimeMillis()));
            return addCommand(new AppendLogCommand(logEntry));
        }
        return false;
    }

    public boolean delete(Key key, Value value) {
        throw new LogStorageNotSupportedOperationException("Compare and delete command is not supported.");
    }

    public void set(Key key, Value value) {
        if (!isClosed) {
            LogEntry logEntry = new SetLogEntry(key, value);
            if (!addCommand(new AppendLogCommand(logEntry))) {
                throw new LogStorageException("Failed to set key:" + key);
            }
        }
    }

    public void set(Key key, Value value, boolean isSetWithCompareVersion) {
        if (isSetWithCompareVersion) {
            throw new LogStorageNotSupportedOperationException("Set with compare command is not supported.");
        } else {
            set(key, value);
        }
    }

    /**
     * return value: true:成功删除了数据；false：没有删除数据，有可能是发生异常，也可能是所给的vnodeList中的数据在log中不存在；
     */
    public boolean delete(List<Integer> vnodeList) {
        if (!isClosed) {
            return addCommand(new DeleteLogByVnodesCommand(vnodeList));
        }
        return false;
    }

    public synchronized Value get(Key key) {
        List<Integer> vnodeList = new ArrayList<Integer>(1);
        vnodeList.add(key.getVNode());

        final Key interKey = key;
        LogClump[] clumpArray = clumpManager.listLogClumpByVnodes(vnodeList);
        ClosableIterator<Pair> iterator = new LogClumpsIterator(clumpArray) {

            @Override
            protected boolean doFilter(LogEntry logEntry) {
                if (null == logEntry) {
                    return false;
                }

                Key k = logEntry.getKey();
                if (interKey.equals(k)) {
                    return true;
                }
                return false;
            }
        };

        try {
            if (iterator.hasNext()) {
                Pair p = iterator.next();
                return p.getValue();
            }
        } finally {
            iterator.close();
        }

        return null;
    }

    public Map<Key, Value> getAll(Iterable<Key> keyIterator) {
        throw new LogStorageNotSupportedOperationException("Gets command is not supported.");
    }

    public StorageType getType() {
        return LogStorageType.LOG_STORAGE;
    }

    public Iterator<Pair> iterator() {
        // TODO insert a snapshot logEntry;
        return clumpManager.iterator();
    }

    public Iterator<Pair> iterator(List<Integer> vnodeList) {
        // TODO insert a snapshot logEntry;
        return clumpManager.iterator(vnodeList);
    }

    private void checkDatabasePath() {
        String dbPath = config.getPath();
        if (StringUtils.isBlank(dbPath)) {
            throw new LogStorageException("Invalid database path :" + dbPath);
        }

        File f = new File(dbPath);
        if (!f.exists()) {
            try {
                FileUtils.forceMkdir(f);
            } catch (Exception e) {
                throw new LogStorageException("Create database path failed. Path:" + dbPath, e);
            }
        }
    }

    private boolean addCommand(LogCommand command) {
        try {
            logCommandQueue.put(command);
            command.waitingResult();
            return command.isSuccess();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return false;
    }

    private LogClumpManager           clumpManager;
    private ClumpConfigure            config;
    private BatchWriteThread          batchWriteThread;
    private BlockingQueue<LogCommand> logCommandQueue;
    private volatile boolean          isClosed = false;
    // private static final Logger logger = LoggerFactory.getLogger(LogStorage.class);
}

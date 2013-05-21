package com.alibaba.doris.dataserver.store.log.db;

import com.alibaba.doris.common.data.ActionPair;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.ActionPairImpl;
import com.alibaba.doris.common.data.impl.ByteWrapperValueImpl;
import com.alibaba.doris.dataserver.store.ClosableIterator;
import com.alibaba.doris.dataserver.store.log.LogStorageException;
import com.alibaba.doris.dataserver.store.log.entry.LogEntry;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class LogClumpsIterator implements ClosableIterator<Pair> {

    public LogClumpsIterator(LogClump[] clumpArray) {
        this.clumpArray = clumpArray;
        this.index = 0;
        nextLogClump();
        this.currentLogEntry = getNextLogEntry();
    }

    public boolean hasNext() {
        return currentLogEntry != null;
    }

    public Pair next() {
        if (null != currentLogEntry) {
            LogEntry preLogEntry = currentLogEntry;
            currentLogEntry = getNextLogEntry();
            return convertLogEntryToPair(preLogEntry);
        }

        throw new IndexOutOfBoundsException();
    }

    protected LogEntry getNextLogEntry() {
        while (null != currentReadWindow) {
            if (currentReadWindow.hasNext()) {
                LogEntry logEntry = currentReadWindow.next();
                if (doFilter(logEntry)) {
                    return logEntry;
                }
            } else {
                nextLogClump();
            }
        }

        return null;
    }

    protected boolean doFilter(LogEntry logEntry) {
        return true;
    }

    private Pair convertLogEntryToPair(LogEntry entry) {
        Value value = entry.getValue();
        if (value instanceof ByteWrapperValueImpl) {
            //It is important to copy the log value data, , because the data buffer of reading is shared by all threads.
            value = ((ByteWrapperValueImpl) value).checkAndCopyValue();
        }

        switch (entry.getType()) {
            case DELETE: {
                return new ActionPairImpl(ActionPair.Type.DELETE, entry.getKey(), value);
            }
            case SET: {
                return new ActionPairImpl(ActionPair.Type.SET, entry.getKey(), value);
            }
            default: {
                throw new LogStorageException("Unknown log entry type " + entry.getType());
            }
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("Log storage can't remove one log entry.");
    }

    private void nextLogClump() {
        if (index < clumpArray.length) {
            if (null != currentReadWindow) {
                currentReadWindow.close();
            }

            currentLogClump = clumpArray[index++];
            currentReadWindow = currentLogClump.getReadWindow();
            return;
        }

        currentLogClump = null;
        if (null != currentReadWindow) {
            currentReadWindow.close();// Here, It's important to free file handler.
            currentReadWindow = null;
        }
        return;
    }

    public void close() {
        if (null != currentReadWindow) {
            currentReadWindow.close();
        }
    }

    private LogClump[] clumpArray;
    private LogClump   currentLogClump;
    private LogEntry   currentLogEntry;
    private int        index;
    private ReadWindow currentReadWindow;
}

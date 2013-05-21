package com.alibaba.doris.dataserver.store.log.db.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.doris.dataserver.store.log.db.WriteWindow;
import com.alibaba.doris.dataserver.store.log.db.WriteWindowRef;
import com.alibaba.doris.dataserver.store.log.entry.ClumpHeadEntry;
import com.alibaba.doris.dataserver.store.log.entry.LogEntry;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DefaultWriteWindowRefImpl implements WriteWindowRef {

    public DefaultWriteWindowRefImpl(WriteWindow writeWindow) {
        this.writeWindow = writeWindow;
    }

    public void incrementReference() {
        reference.incrementAndGet();
    }

    public void releaseReference() {
        reference.decrementAndGet();
    }

    public void append(LogEntry logEntry) {
        writeWindow.append(logEntry);
    }

    public boolean close() {
        if (reference.get() == 0) {
            return writeWindow.close();
        }
        return false;
    }

    public void flushAll() {
        writeWindow.flushAll();
    }

    public long size() {
        return writeWindow.size();
    }

    public ClumpHeadEntry getClumpHeadEntry() {
        return writeWindow.getClumpHeadEntry();
    }

    public boolean deleteByVnodes(List<Integer> vnodeList) {
        return writeWindow.deleteByVnodes(vnodeList);
    }

    public boolean isOpen() {
        return writeWindow.isOpen();
    }

    public WriteWindow getWriteWindow() {
        return writeWindow;
    }

    public void append(LogEntry[] logEntryArray) {
        writeWindow.append(logEntryArray);
    }

    private WriteWindow writeWindow;
    private AtomicLong  reference = new AtomicLong();
}

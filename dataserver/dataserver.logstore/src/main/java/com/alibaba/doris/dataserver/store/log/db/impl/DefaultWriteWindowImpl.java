package com.alibaba.doris.dataserver.store.log.db.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.dataserver.store.log.LogStorageException;
import com.alibaba.doris.dataserver.store.log.db.ClumpConfigure;
import com.alibaba.doris.dataserver.store.log.db.LogClumpHead;
import com.alibaba.doris.dataserver.store.log.db.LogFile;
import com.alibaba.doris.dataserver.store.log.db.WriteWindow;
import com.alibaba.doris.dataserver.store.log.db.WriteWindowClosedException;
import com.alibaba.doris.dataserver.store.log.db.LogFile.AccessMode;
import com.alibaba.doris.dataserver.store.log.entry.ClumpHeadEntry;
import com.alibaba.doris.dataserver.store.log.entry.LogEntry;
import com.alibaba.doris.dataserver.store.log.serialize.LogSerializer;
import com.alibaba.doris.dataserver.store.log.serialize.LogSerializerFactory;
import com.alibaba.doris.dataserver.store.log.utils.LogFileUtil;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DefaultWriteWindowImpl implements WriteWindow {

    public DefaultWriteWindowImpl(ClumpConfigure config, String clumpName) {
        this(config, clumpName, null);
    }

    public DefaultWriteWindowImpl(ClumpConfigure config, String clumpName, ByteBuffer buffer) {
        this.config = config;
        this.clumpName = clumpName;
        this.writeBuffer = buffer;
        prepare();
    }

    public synchronized long size() {
        checkIsOpen();
        return logData.length();
    }

    private void prepare() {
        head = new DefaultLogClumpHeadImpl(config, clumpName);
        head.open();

        if (null == writeBuffer) {
            writeBuffer = ByteBuffer.allocate(config.getWriteBufferSize());
        } else {
            writeBuffer.clear();
        }

        serializer = LogSerializerFactory.getInstance().getSerializer(head.getClumpHeadEntry().getLogFileVersion());
        isWriteDirect = config.isWriteDirect();

        logData = new LogFile(LogFileUtil.generateDataFileName(config.getPath(), clumpName));
        try {
            logData.open(AccessMode.RW);
        } catch (IOException e) {
            throw new LogStorageException(e);
        }

        // append mode
        logData.seek(logData.length());
        headEntry = head.getClumpHeadEntry();
        isOpen = true;
    }

    public synchronized void append(LogEntry logEntry) {
        checkIsOpen();
        if (fillDataToWriteBuffer(logEntry)) {
            if (isWriteDirect) {
                flush();
            }
        }
    }

    public void append(LogEntry[] logEntryArray) {
        checkIsOpen();

        for (LogEntry logEntry : logEntryArray) {
            if (!fillDataToWriteBuffer(logEntry)) {
                throw new LogStorageException("Writing data failed!");
            }
        }

        if (isWriteDirect) {
            flush();
        }
    }

    private boolean fillDataToWriteBuffer(LogEntry logEntry) {
        boolean needBreak = false;
        do {
            if (writeBuffer.hasRemaining()) {
                if (serializer.writeLogEntry(writeBuffer, logEntry)) {
                    headEntry.addVnode(logEntry.getVnode());
                    return true;
                } else {
                    flush();
                    needBreak = true;
                    continue;
                }
            } else {
                flush();
            }

            if (needBreak) {
                return false;
            }
        } while (true);
    }

    public synchronized boolean isOpen() {
        return isOpen;
    }

    public synchronized boolean close() {
        if (logger.isDebugEnabled()) {
            logger.debug("Close writeWindow: " + this.clumpName);
            // StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            // for (StackTraceElement e : elements) {
            // logger.debug(e.toString());
            // }
        }

        if (isOpen) {
            flushAll();
            head.close();
            logData.close();
            isOpen = false;
            return true;
        }

        return true;
    }

    /**
     * 将缓冲区的所有数据写入文件。
     */
    public synchronized void flushAll() {
        checkIsOpen();
        if (null == writeBuffer) {
            return;
        }

        try {
            if (writeBuffer.hasRemaining()) {
                writeBuffer.flip();
                while (writeBuffer.hasRemaining()) {
                    logData.write(writeBuffer);
                }
            }
        } catch (IOException e) {
            throw new LogStorageException(e);
        }
    }

    public ClumpHeadEntry getClumpHeadEntry() {
        // TODO:The better way here: to return an immutable Entry Object. :)
        return headEntry;
    }

    public synchronized boolean deleteByVnodes(List<Integer> vnodeList) {
        checkIsOpen();
        if (headEntry.removeVnodes(vnodeList)) {
            // 刷新head信息，保存到磁盘
            head.flush();
            return true;
        }
        return false;
    }

    private void flush() {
        // flush
        try {
            head.flush();
            if (null != writeBuffer) {
                writeBuffer.flip();
                logData.write(writeBuffer);
                writeBuffer.compact();
            }
        } catch (IOException e) {
            throw new LogStorageException(e);
        }
    }

    private void checkIsOpen() {
        if (!isOpen) {
            throw new WriteWindowClosedException("The write window is closed. clump:[" + this.clumpName + "]");
        }
    }

    private LogClumpHead        head;
    private ClumpConfigure      config;
    private LogFile             logData;
    private String              clumpName;
    private ClumpHeadEntry      headEntry;
    private ByteBuffer          writeBuffer;
    private LogSerializer       serializer;
    private boolean             isWriteDirect = false;
    private volatile boolean    isOpen        = false;
    private static final Logger logger        = LoggerFactory.getLogger(DefaultWriteWindowImpl.class);
}

package com.alibaba.doris.dataserver.store.log.db.impl;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.alibaba.doris.dataserver.store.log.LogStorageException;
import com.alibaba.doris.dataserver.store.log.db.ClumpConfigure;
import com.alibaba.doris.dataserver.store.log.db.LogClumpHead;
import com.alibaba.doris.dataserver.store.log.db.LogFile;
import com.alibaba.doris.dataserver.store.log.db.ReadWindow;
import com.alibaba.doris.dataserver.store.log.db.LogFile.AccessMode;
import com.alibaba.doris.dataserver.store.log.entry.ClumpHeadEntry;
import com.alibaba.doris.dataserver.store.log.entry.LogEntry;
import com.alibaba.doris.dataserver.store.log.serialize.LogSerializer;
import com.alibaba.doris.dataserver.store.log.serialize.LogSerializerFactory;
import com.alibaba.doris.dataserver.store.log.utils.LogFileUtil;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DefaultReadWindowImpl implements ReadWindow {

    public DefaultReadWindowImpl(ClumpConfigure config, String clumpName) {
        this.config = config;
        this.clumpName = clumpName;
        this.bufferSize = config.getReadBufferSize();
        this.readBuffer = ByteBuffer.allocate(bufferSize);
        this.readBuffer.flip();
        prepare();
    }

    private void prepare() {
        head = new DefaultLogClumpHeadImpl(config, clumpName);
        head.loadLogHeader();
        ClumpHeadEntry headEntry = head.getClumpHeadEntry();
        this.serializer = LogSerializerFactory.getInstance().getSerializer(headEntry.getLogFileVersion());

        logData = new LogFile(LogFileUtil.generateDataFileName(config.getPath(), clumpName));
        try {
            logData.open(AccessMode.R);
        } catch (IOException e) {
            throw new LogStorageException(e);
        }
    }

    public boolean hasNext() {
        if (readBuffer.hasRemaining()) {
            return true;
        } else {
            return logData.isEOF() == false && head.getClumpHeadEntry().getVnodeNum() > 0;
        }
    }

    public LogEntry next() {
        LogEntry entry = null;
        boolean needBreak = false;
        do {
            if (readBuffer.hasRemaining()) {
                entry = serializer.readLogEntry(readBuffer);
                if (null != entry) {
                    return entry;
                }
            }

            if (!needBreak) {
                fillBuffer();
                needBreak = true;
            } else {
                break;
            }
        } while (true);
        // TODO: But The better way maybe we need throw one exception, is it right?
        return null;
    }

    public void close() {
        logData.close();
    }

    public long size() {
        return logData.length();
    }

    private void fillBuffer() {
        readBuffer.compact();
        try {
            logData.read(readBuffer);
            readBuffer.flip();
        } catch (IOException e) {
            throw new LogStorageException(e);
        }
    }

    private LogFile        logData;
    private ByteBuffer     readBuffer;
    private int            bufferSize;
    private LogClumpHead   head;
    private ClumpConfigure config;
    private LogSerializer  serializer;
    private String         clumpName;
}

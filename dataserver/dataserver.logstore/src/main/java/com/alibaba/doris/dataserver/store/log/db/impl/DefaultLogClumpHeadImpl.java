package com.alibaba.doris.dataserver.store.log.db.impl;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.alibaba.doris.dataserver.store.log.LogStorageException;
import com.alibaba.doris.dataserver.store.log.db.ClumpConfigure;
import com.alibaba.doris.dataserver.store.log.db.LogClumpHead;
import com.alibaba.doris.dataserver.store.log.db.LogFile;
import com.alibaba.doris.dataserver.store.log.db.LogFile.AccessMode;
import com.alibaba.doris.dataserver.store.log.entry.ClumpHeadEntry;
import com.alibaba.doris.dataserver.store.log.serialize.LogSerializer;
import com.alibaba.doris.dataserver.store.log.serialize.impl.DefaultLogSerializer;
import com.alibaba.doris.dataserver.store.log.utils.LogFileUtil;

/**
 * Not thread safety
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DefaultLogClumpHeadImpl implements LogClumpHead {

    public DefaultLogClumpHeadImpl(ClumpConfigure config, String clumpName) {
        this.clumpName = clumpName;
        this.config = config;
    }

    public void open() {
        logHeader = new LogFile(LogFileUtil.generateHeadFileName(config.getPath(), clumpName));
        try {
            logHeader.open(AccessMode.RW);
            read(logHeader);
        } catch (IOException e) {
            throw new LogStorageException(e);
        }
    }

    public void loadLogHeader() {
        LogFile headerFile = new LogFile(LogFileUtil.generateHeadFileName(config.getPath(), clumpName));
        try {
            headerFile.open(AccessMode.R);
            read(headerFile);
        } catch (IOException e) {
            throw new LogStorageException(e);
        } finally {
            headerFile.close();
        }
    }

    private void read(LogFile headerFile) throws IOException {
        headBuffer = ByteBuffer.allocate(DEFAULT_LOG_READER_BUFFER_SIZE);
        headerFile.read(headBuffer);
        headBuffer.flip();

        head = serializer.readHead(headBuffer);
    }

    public void close() {
        flush();
        logHeader.close();
    }

    public void flush() {
        if (head.isChanged()) {
            try {
                headBuffer.clear();
                serializer.writeHead(headBuffer, head);
                headBuffer.flip();
                logHeader.seek(0);
                logHeader.write(headBuffer);
                head.setChanged(false);
            } catch (Exception e) {
                throw new LogStorageException(e);
            }
        }
    }

    public ClumpHeadEntry getClumpHeadEntry() {
        return head;
    }

    private ClumpConfigure       config;
    private String               clumpName;
    private ClumpHeadEntry       head;
    private ByteBuffer           headBuffer;
    private LogFile              logHeader;
    private static LogSerializer serializer                     = new DefaultLogSerializer();
    private static final int     DEFAULT_LOG_READER_BUFFER_SIZE = 1024 * 1024;
}

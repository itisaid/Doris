package com.alibaba.doris.dataserver.store.log.serialize;

import java.nio.ByteBuffer;

import com.alibaba.doris.dataserver.store.log.entry.ClumpHeadEntry;
import com.alibaba.doris.dataserver.store.log.entry.LogEntry;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface LogSerializer {

    /**
     * 将Log头部信息数据转换成byte，并存入buffer。
     * 
     * @param buffer
     * @param head
     */
    public void writeHead(ByteBuffer buffer, ClumpHeadEntry head);

    /**
     * 将一条Log记录，存入buffer
     * 
     * @param buffer
     * @param logEntry
     */
    public boolean writeLogEntry(ByteBuffer buffer, LogEntry logEntry);

    /**
     * 从buffer中读取Log头部信息。
     * 
     * @param buffer
     * @return
     */
    public ClumpHeadEntry readHead(ByteBuffer buffer);

    /**
     * 从buffer中读取一条Log记录
     * 
     * @param buffer
     * @return
     */
    public LogEntry readLogEntry(ByteBuffer buffer);

    public int readVersion(ByteBuffer buffer);

    public void writeVersion(ByteBuffer buffer, int version);
}

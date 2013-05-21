package com.alibaba.doris.dataserver.store.log.serialize.impl;

import java.nio.ByteBuffer;
import java.util.Iterator;

import com.alibaba.doris.dataserver.store.log.LogStorageException;
import com.alibaba.doris.dataserver.store.log.entry.ClumpHeadEntry;
import com.alibaba.doris.dataserver.store.log.entry.DeleteLogEntry;
import com.alibaba.doris.dataserver.store.log.entry.LogEntry;
import com.alibaba.doris.dataserver.store.log.entry.SetLogEntry;
import com.alibaba.doris.dataserver.store.log.entry.LogEntry.Type;
import com.alibaba.doris.dataserver.store.log.serialize.LogSerializer;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DefaultLogSerializer implements LogSerializer {

    private static final int LOG_ENTRY_HEADER_LENGTH = 9;

    public ClumpHeadEntry readHead(ByteBuffer buffer) {
        // 没有head文件，则创建一个空的Head对象。
        ClumpHeadEntry head = new ClumpHeadEntry();

        if (buffer.remaining() >= 4) {
            int vnodeNum = buffer.getInt();
            for (int i = 0; i < vnodeNum; i++) {
                head.addVnode(buffer.getInt());
            }
        }

        return head;
    }

    public void writeHead(ByteBuffer buffer, ClumpHeadEntry head) {
        buffer.putInt(head.getVnodeNum());

        Iterator<Integer> nodes = head.getVnodes();
        int count = 0;
        while (nodes.hasNext()) {
            count++;
            buffer.putInt(nodes.next().intValue());
        }

        if (count != head.getVnodeNum()) {
            throw new LogStorageException("Check clump head failed! The vnode number is not equals.");
        }
    }

    public LogEntry readLogEntry(ByteBuffer buffer) {
        if (buffer.remaining() < LOG_ENTRY_HEADER_LENGTH) {
            return null;
        }

        int startPos = buffer.position();
        // read entry type
        byte bType = buffer.get();
        Type type = Type.valueOf(bType);
        LogEntry logEntry = getLogEntry(type);

        // read vnode
        logEntry.setVnode(buffer.getInt());
        // read data length
        int dataLen = buffer.getInt();

        if (buffer.remaining() < dataLen) {
            buffer.position(startPos);
            return null;
        }
        // read data
        logEntry.decode(buffer);
        // read data length
        int checkDataLen = buffer.getInt();
        if (checkDataLen != dataLen) {
            throw new LogStorageException("The LogEntry is invalid, Check data len failed! start=" + dataLen + " end="
                                          + checkDataLen);
        }

        return logEntry;
    }

    public boolean writeLogEntry(ByteBuffer buffer, LogEntry logEntry) {
        boolean bReturn = false;
        int outerPosition = buffer.position();
        if (buffer.remaining() > 9) {
            Type type = logEntry.getType();
            // write entry type.
            buffer.put(type.getCode());
            // write vnode;
            buffer.putInt(logEntry.getVnode());
            int startPos = buffer.position();
            // write data length
            buffer.putInt(0);

            // write data
            if (logEntry.encode(buffer)) {
                int dataLen = buffer.position() - startPos;
                if (buffer.remaining() > 4) {
                    // write data length
                    buffer.putInt(dataLen);

                    int pos = buffer.position();
                    buffer.position(startPos);
                    buffer.putInt(dataLen);

                    // back to end position.
                    buffer.position(pos);
                    bReturn = true;
                }
            }
        }

        if (bReturn) {
            return true;
        }

        buffer.position(outerPosition);
        return false;
    }

    public int readVersion(ByteBuffer buffer) {
        return buffer.getInt();
    }

    public void writeVersion(ByteBuffer buffer, int version) {
        buffer.putInt(version);
    }

    private LogEntry getLogEntry(Type type) {
        switch (type) {
            case SET:
                return new SetLogEntry();
            case DELETE:
                return new DeleteLogEntry();
            default:
                throw new LogStorageException("Invalid LogEntry type :" + type);
        }
    }

    // private static final int DEFAULT_HEAD_LENGTH = 100;
}

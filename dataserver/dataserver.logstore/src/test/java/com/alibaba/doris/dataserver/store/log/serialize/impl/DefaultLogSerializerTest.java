package com.alibaba.doris.dataserver.store.log.serialize.impl;

import java.nio.ByteBuffer;
import java.util.Iterator;

import junit.framework.TestCase;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.KeyImpl;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.store.log.entry.ClumpHeadEntry;
import com.alibaba.doris.dataserver.store.log.entry.SetLogEntry;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DefaultLogSerializerTest extends TestCase {

    public void testReadAndWriteLogEntry() {
        DefaultLogSerializer serializer = new DefaultLogSerializer();
        String valueString = "test value";
        Key key = new KeyImpl(100, "key", 1);
        Value value = new ValueImpl(ByteUtils.stringToByte(valueString), System.currentTimeMillis());
        value.setFlag((short) 10);

        SetLogEntry logEntry = new SetLogEntry(key, value);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        serializer.writeLogEntry(buffer, logEntry);

        assertTrue(buffer.position() > 0);

        buffer.flip();

        SetLogEntry newEntry = (SetLogEntry) serializer.readLogEntry(buffer);
        assertNotNull(newEntry);
        Key newKey = newEntry.getKey();
        assertNotNull(newKey);
        assertEquals(key.getPhysicalKey(), newKey.getPhysicalKey());

        Value newValue = newEntry.getValue();
        assertNotNull(newValue);
        assertEquals(valueString, ByteUtils.byteToString(newValue.getValueBytes()));
        assertEquals(value.getFlag(), newValue.getFlag());
        assertEquals(value.getTimestamp(), newValue.getTimestamp());
    }

    public void testReadAndWriteHead() {
        DefaultLogSerializer serializer = new DefaultLogSerializer();
        ClumpHeadEntry head = new ClumpHeadEntry();
        head.setLogFileVersion(10);

        int vnodeNum = 10;
        for (int i = 0; i < vnodeNum; i++) {
            head.addVnode(i);
        }
        ByteBuffer buffer = ByteBuffer.allocate(64);
        serializer.writeHead(buffer, head);
        assertTrue(buffer.position() > 0);

        buffer.flip();

        ClumpHeadEntry newHead = serializer.readHead(buffer);
        assertNotNull(newHead);
        assertEquals(vnodeNum, newHead.getVnodeNum());
        for (int i = 0; i < vnodeNum; i++) {
            boolean isContains = false;
            Iterator<Integer> vnodeItr = newHead.getVnodes();
            while (vnodeItr.hasNext()) {
                if (i == vnodeItr.next().intValue()) {
                    isContains = true;
                }
            }
            assertTrue(isContains);
        }
    }
}

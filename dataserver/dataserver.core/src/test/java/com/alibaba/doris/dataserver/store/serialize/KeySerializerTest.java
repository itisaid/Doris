package com.alibaba.doris.dataserver.store.serialize;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import junit.framework.TestCase;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.KeyImpl;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.common.data.util.ByteUtils;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class KeySerializerTest extends TestCase {

    public void testEncode() {
        KeyValueSerializerFactory serializer = KeyValueSerializerFactory.getInstance();
        Key key = new KeyImpl(1, "test key value", 1);
        byte[] bytes = serializer.encode(key).copyBytes();
        assertNotNull(bytes);

        Key deKey = serializer.decodeKey(bytes);
        assertNotNull(deKey);
        assertEquals(deKey.getKey(), key.getKey());
        assertEquals(deKey.getNamespace(), key.getNamespace());
    }

    public void testEncodeKey0() {
        KeyValueSerializerFactory serializer = KeyValueSerializerFactory.getInstance();
        Key key = new KeyImpl(1, "test key value 123456789012345678901234567890", 1);
        ByteBuffer buffer = ByteBuffer.allocate(30);
        assertFalse(serializer.encode(buffer, key));
        buffer.flip();
        assertEquals(0, buffer.remaining());

        buffer = ByteBuffer.allocate(300);
        assertTrue(serializer.encode(buffer, key));
        buffer.flip();
        assertFalse(0 == buffer.remaining());
    }

    public void testEncodeValue0() {
        KeyValueSerializerFactory serializer = KeyValueSerializerFactory.getInstance();
        Value value = new ValueImpl("test value value 123456789012345678901234567890".getBytes(), 1);
        ByteBuffer buffer = ByteBuffer.allocate(30);
        assertFalse(serializer.encode(buffer, value));
        buffer.flip();
        assertEquals(0, buffer.remaining());

        buffer = ByteBuffer.allocate(300);
        assertTrue(serializer.encode(buffer, value));
        buffer.flip();
        assertFalse(0 == buffer.remaining());
    }

    public void t1estString() throws UnsupportedEncodingException {
        int loopTimes = 10000000;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1024; i++) {
            sb.append("1");
        }
        String value = sb.toString();
        value.intern();
        long start = System.nanoTime();
        for (int i = 0; i < loopTimes; i++) {
            encodeString(value);
        }
        long end = System.nanoTime();

        System.out.println("loop times:" + loopTimes + " avg:" + ((end - start) / loopTimes));
    }

    private void encodeString(String value) {
        ByteUtils.stringToByte(Integer.toString((1000)));
    }
}

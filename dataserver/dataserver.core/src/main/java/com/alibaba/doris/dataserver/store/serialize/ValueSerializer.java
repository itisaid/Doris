package com.alibaba.doris.dataserver.store.serialize;

import java.nio.ByteBuffer;

import com.alibaba.doris.common.data.ByteWrapper;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.ValueFactory;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ValueSerializer extends BaseSerializer<Value> {

    public Value decode(byte[] bytes) {
        return decode(ByteBuffer.wrap(bytes));
    }

    public Value decode(ByteBuffer buffer) {
        // read head
        short flag = buffer.getShort();
        long timestamp = buffer.getLong();
        int len = buffer.getInt();
        if (buffer.remaining() < len) {
            throw new RuntimeException("Read data error.");
        }

        // read body;
        // byte[] valueBytes = new byte[len];
        // buffer.get(valueBytes);
        // Don't copy data and allocate the memory, we just wrap it.
        int pos = buffer.position();
        ByteWrapper byteWrapper = new ByteWrapper(buffer.array(), pos, len);
        buffer.position(pos + len);
        return ValueFactory.createValue(byteWrapper, flag, timestamp);
    }

    public ByteWrapper encode(Value t) {
        byte[] valueBytes = t.getValueBytes();
        ByteBuffer buf = getByteBuffer(2 + 8 + 4 + valueBytes.length + 1);
        encode(buf, t);
        return new ByteWrapper(buf.array(), 0, buf.position());
    }

    public boolean encode(ByteBuffer buffer, Value value) {
        if (buffer.remaining() > 15) {
            int pos = buffer.position();
            // put data head
            buffer.put(getVersion());
            buffer.putShort(value.getFlag());
            buffer.putLong(value.getTimestamp());
            int len = value.getValueBytes().length;
            buffer.putInt(len);
            if (buffer.remaining() >= len) {
                // put data body
                buffer.put(value.getValueBytes());
                return true;
            }
            buffer.position(pos);
        }
        return false;
    }

    public byte getVersion() {
        return 0;
    }
}

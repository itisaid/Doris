package com.alibaba.doris.dataserver.store.log.entry;

import java.nio.ByteBuffer;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.dataserver.store.log.LogStorageException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DeleteLogEntry extends BaseLogEntry {

    public DeleteLogEntry(Key key, Value value) {
        super(key, value);
    }

    public DeleteLogEntry() {
        super(null, null);
    }

    public Type getType() {
        return Type.DELETE;
    }

    public boolean decode(ByteBuffer buffer) {
        Key key = serializer.decodeKey(buffer);
        if (null == key) {
            throw new LogStorageException();
        }

        setKey(key);

        long timestamp = buffer.getLong();
        setValue(new ValueImpl(null, timestamp));
        return true;
    }

    public boolean encode(ByteBuffer buffer) {
        Key key = getKey();
        Value value = getValue();

        if (null == key) {
            throw new IllegalArgumentException("The key of LogEntry is null");
        }

        if (null == value) {
            throw new IllegalArgumentException("The value of LogEntry is null");
        }

        int pos = buffer.position();
        if (serializer.encode(buffer, key)) {
            if (buffer.remaining() >= 8) {
                buffer.putLong(value.getTimestamp());
                return true;
            }
        }

        buffer.position(pos);
        return false;
    }
}

package com.alibaba.doris.dataserver.store.log.entry;

import java.nio.ByteBuffer;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.ByteWrapperValueImpl;
import com.alibaba.doris.dataserver.store.log.LogStorageException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class SetLogEntry extends BaseLogEntry {

    public SetLogEntry(Key key, Value value) {
        super(key, value);
    }

    public SetLogEntry() {
        super(null, null);
    }

    public Type getType() {
        return Type.SET;
    }

    public boolean decode(ByteBuffer buffer) {
        Key key = serializer.decodeKey(buffer);
        if (null == key) {
            throw new LogStorageException();
        }
        setKey(key);

        Value value = serializer.decodeValue(buffer);
        if (null == value) {
            throw new LogStorageException();
        }
        
        if (value instanceof ByteWrapperValueImpl) {
            ((ByteWrapperValueImpl) value).checkAndCopyValue();
        }
        
        setValue(value);
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
            if (serializer.encode(buffer, value)) {
                return true;
            }
        }
        buffer.position(pos);
        return false;
    }

}

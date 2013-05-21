package com.alibaba.doris.dataserver.store.serialize;

import java.nio.ByteBuffer;

import com.alibaba.doris.common.data.ByteWrapper;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.KeyFactory;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class KeySerializer extends BaseSerializer<Key> {

    public Key decode(byte[] bytes) {
        return decode(ByteBuffer.wrap(bytes));
    }

    public Key decode(ByteBuffer buffer) {
        if (buffer.remaining() > (INTEGER_INT * 2)) {
            int startPos = buffer.position();

            int vnode = buffer.getInt();// read vnode
            int keyLen = buffer.getInt();// read len

            if (buffer.remaining() >= keyLen) {
                byte[] keyBytes = new byte[keyLen];

                buffer.get(keyBytes);// read data

                return KeyFactory.createKey(keyBytes, vnode);
            }
            buffer.position(startPos);
        }
        return null;
    }

    public boolean encode(ByteBuffer buffer, Key key) {
        int remaining = buffer.remaining();
        if (remaining > 9) {
            int pos = buffer.position();
            buffer.put(getVersion());
            buffer.putInt(key.getVNode());
            byte[] keyBytes = key.getPhysicalKeyBytes();
            buffer.putInt(keyBytes.length);
            if (buffer.remaining() >= keyBytes.length) {
                buffer.put(keyBytes);
                return true;
            }
            buffer.position(pos);
        }
        return false;
    }

    public ByteWrapper encode(Key t) {
        byte[] keyBytes = t.getPhysicalKeyBytes();

        int len = INTEGER_INT * 2 + keyBytes.length + 1;
        ByteBuffer buf = getByteBuffer(len);
        encode(buf, t);

        return new ByteWrapper(buf.array(), 0, buf.position());
    }

    public byte getVersion() {
        return 0;
    }

    private static final int INTEGER_INT = 4;
}

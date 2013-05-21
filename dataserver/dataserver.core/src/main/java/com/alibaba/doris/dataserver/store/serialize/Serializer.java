package com.alibaba.doris.dataserver.store.serialize;

import java.nio.ByteBuffer;

import com.alibaba.doris.common.data.ByteWrapper;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface Serializer<T> {

    public ByteWrapper encode(T t);

    public boolean encode(ByteBuffer buffer, T t);

    public T decode(ByteBuffer buffer);

    public byte getVersion();
}

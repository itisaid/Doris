package com.alibaba.doris.dataserver.store.serialize;

import java.nio.ByteBuffer;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public abstract class BaseSerializer<T> implements Serializer<T> {

    /**
     * get buffer from buffer pool.
     * 
     * @param size
     * @return
     */
    protected ByteBuffer getByteBuffer(int size) {
        ByteBuffer buffer = threadLocalBasedBufferPool.get();

        if (null == buffer || buffer.capacity() < size) {
            buffer = ByteBuffer.allocate(size);
            threadLocalBasedBufferPool.set(buffer);
        } else {
            buffer.clear();
        }

        return buffer;
    }

    private final ThreadLocal<ByteBuffer> threadLocalBasedBufferPool = new ThreadLocal<ByteBuffer>();
}

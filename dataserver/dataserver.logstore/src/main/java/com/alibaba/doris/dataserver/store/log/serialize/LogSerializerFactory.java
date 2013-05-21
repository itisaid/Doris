package com.alibaba.doris.dataserver.store.log.serialize;

import com.alibaba.doris.dataserver.store.log.serialize.impl.DefaultLogSerializer;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class LogSerializerFactory {

    private LogSerializerFactory() {
    }

    public LogSerializer getSerializer(int version) {
        return serializer;
    }

    public static LogSerializerFactory getInstance() {
        return instance;
    }

    private LogSerializer                     serializer = new DefaultLogSerializer();
    private static final LogSerializerFactory instance   = new LogSerializerFactory();
}

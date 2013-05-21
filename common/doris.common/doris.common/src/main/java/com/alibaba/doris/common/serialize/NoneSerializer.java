package com.alibaba.doris.common.serialize;

/**
 * 
 * 在用户使用了自定义序列化方式的namespace时，不会做任何序列化的处理，直接返回。遇到非byte[]的类型会抛出异常
 * @author chenchao.yecc
 * @since 0.1.4
 *
 */
public class NoneSerializer implements Serializer{

    public Object deserialize(byte[] o, Object deserializeTarget) {
        if (o.length == 1 && o[0] == BYTE_NULL) {
            return null;
        }
        return o;
    }

    public byte[] serialize(Object o, Object arg) {
        // null不做任何处理
        if (o == null) {
            return BYTES_NULL;
        }

        // 只处理byte[]，否则报错
        if (!(o instanceof byte[])) {
            throw new IllegalArgumentException("Because of using none serializer,the namespace only supports putBytes and getBytes method!");
        }
        return (byte[])o;
    }
}

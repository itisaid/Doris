package com.alibaba.doris.dataserver.store.serialize;

import java.nio.ByteBuffer;

import com.alibaba.doris.common.data.ByteWrapper;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class KeyValueSerializerFactory {

    @SuppressWarnings("unchecked")
    private KeyValueSerializerFactory() {
        keySerializerArray = new Serializer[1];
        Serializer<Key> serializer = new KeySerializer();
        keySerializerArray[serializer.getVersion()] = serializer;

        valueSerializerArray = new Serializer[1];
        Serializer<Value> valueSerializer = new ValueSerializer();
        valueSerializerArray[valueSerializer.getVersion()] = valueSerializer;
    }

    public static KeyValueSerializerFactory getInstance() {
        return instance;
    }

    public Serializer<Key> getKeySerializer(byte version) {
        return keySerializerArray[version];
    }

    public Serializer<Value> getValueSerializer(byte version) {
        return valueSerializerArray[version];
    }

    public Serializer<Key> getDefaultKeySerializer() {
        return keySerializerArray[DEFAULT_KEY_SERIALIZER_VERSION];
    }

    public Serializer<Value> getDefaultValueSerializer() {
        return valueSerializerArray[DEFAULT_VALUE_SERIALIZER_VERSION];
    }

    public Key decodeKey(byte[] keyBytes) {
        return decodeKey(ByteBuffer.wrap(keyBytes));
    }

    public Key decodeKey(ByteBuffer buffer) {
        byte serializerVersion = buffer.get();
        Serializer<Key> keySerializer = instance.getKeySerializer(serializerVersion);
        return keySerializer.decode(buffer);
    }

    public Value decodeValue(byte[] valueBytes) {
        return decodeValue(ByteBuffer.wrap(valueBytes));
    }

    public Value decodeValue(ByteBuffer buffer) {
        if (buffer.hasRemaining()) {
            byte serializerVersion = buffer.get();
            Serializer<Value> valueSerializer = instance.getValueSerializer(serializerVersion);
            return valueSerializer.decode(buffer);
        } else {
            return null;
        }
    }

    public ByteWrapper encode(Key key) {
        Serializer<Key> keySerializer = instance.getDefaultKeySerializer();
        return keySerializer.encode(key);
    }

    public boolean encode(ByteBuffer buffer, Key key) {
        Serializer<Key> keySerializer = instance.getDefaultKeySerializer();
        return keySerializer.encode(buffer, key);
    }

    public ByteWrapper encode(Value value) {
        Serializer<Value> valueSerializer = instance.getDefaultValueSerializer();
        return valueSerializer.encode(value);
    }

    public boolean encode(ByteBuffer buffer, Value value) {
        Serializer<Value> valueSerializer = instance.getDefaultValueSerializer();
        return valueSerializer.encode(buffer, value);
    }

    private final Serializer<Key>[]                keySerializerArray;
    private final Serializer<Value>[]              valueSerializerArray;
    private static final byte                      DEFAULT_KEY_SERIALIZER_VERSION   = 0;
    private static final byte                      DEFAULT_VALUE_SERIALIZER_VERSION = 0;
    private static final KeyValueSerializerFactory instance                         = new KeyValueSerializerFactory();
}

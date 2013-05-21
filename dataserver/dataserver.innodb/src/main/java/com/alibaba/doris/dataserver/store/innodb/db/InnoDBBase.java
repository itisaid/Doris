package com.alibaba.doris.dataserver.store.innodb.db;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.dataserver.store.BaseStorage;
import com.alibaba.doris.dataserver.store.serialize.KeyValueSerializerFactory;

/**
 * @author long.mal long.mal@alibaba-inc.com
 */
public abstract class InnoDBBase extends BaseStorage{
    protected long getHash(Key key) {
        return Arrays.hashCode(key.getPhysicalKeyBytes());
    }
    
    protected Value getValueFromResult(Map<String, Object> row) {
    	 byte[] valueBytes = (byte[]) row.get(InnoDBBuilder.FIELD_VALUE);
    	 Value value = serializerFactory.decodeValue(valueBytes); 
    	 value.setTimestamp(getVersionFromResult(row));
    	 return value;
    }
    
    protected byte[] getKeyBytesFromResult(Map<String, Object> row) {
    	return (byte[]) row.get(InnoDBBuilder.FIELD_KEY);
    }
    
    protected String getKeyStringFromResult(Map<String, Object> row) {
    	return new String(getKeyBytesFromResult(row));
    }
    
    protected long getVersionFromResult(Map<String, Object> row){
    	BigInteger versionBigInteger = (BigInteger)row.get(InnoDBBuilder.FIELD_VERSION);
    	return versionBigInteger.longValue();
    }
    
    protected static final KeyValueSerializerFactory serializerFactory = KeyValueSerializerFactory.getInstance();
}

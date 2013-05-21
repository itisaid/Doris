package com.alibaba.doris.dataserver.store.innodb.config;

import com.g414.haildb.DatabaseConfiguration;

/**
 * @author long.mal long.mal@alibaba-inc.com
 */
public class InnoDBDatabaseConfiguration extends DatabaseConfiguration {

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }
    
    public long getValueLength() {
    	return valueLength;
    }
    
    public void setValueLength(long valueLen) {
    	this.valueLength = valueLen;
    }
    
    public int getWriteThread() {
		return writeThread;
	}

	public void setWriteThread(int writeThread) {
		this.writeThread = writeThread;
	}

    private String schema;
    private int    keyLength   = 200;
    private long   valueLength = 65535;
    private int    writeThread = 32;
}

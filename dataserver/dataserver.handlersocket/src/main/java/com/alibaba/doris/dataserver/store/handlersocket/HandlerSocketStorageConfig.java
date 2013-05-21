package com.alibaba.doris.dataserver.store.handlersocket;

import com.alibaba.hsclient.conf.HSConfig;

public class HandlerSocketStorageConfig extends HSConfig{
	private String dbName;

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	
}

package com.alibaba.doris.dataserver.store.handlersocket;

import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.StorageConfig;
import com.alibaba.doris.dataserver.store.StorageDriver;
import com.alibaba.doris.dataserver.store.StorageType;
import com.alibaba.doris.dataserver.store.handlersocket.util.HandlerSocketConfigUtil;

public class HandlerSocketStorageDriver implements StorageDriver{

	private HandlerSocketStorageConfig handlerSocketConfig;
	public Storage createStorage() {
		return new HandlerSocketStorage(handlerSocketConfig);
	}

	public void init(StorageConfig config) {
		loadConfig(config);
	}

	public StorageType getStorageType() {
		return HandlerSocketStorageType.HandlerSocket;
	}

    private void loadConfig(StorageConfig config) {
    	handlerSocketConfig = HandlerSocketConfigUtil.loadHandlerSocketStorageConfigFromFile(config.getPropertiesFile());
    }
}

package com.alibaba.doris.dataserver.store.handlersocket;

import com.alibaba.doris.dataserver.store.StorageType;

public enum HandlerSocketStorageType implements StorageType {

	HandlerSocket("HandlerSocket");

    private HandlerSocketStorageType(String type) {
        this.type = type;
    }

    public String getStorageType() {
        return type;
    }

    private String type;

}

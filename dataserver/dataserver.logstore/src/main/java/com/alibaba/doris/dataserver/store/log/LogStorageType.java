package com.alibaba.doris.dataserver.store.log;

import com.alibaba.doris.dataserver.store.StorageType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public enum LogStorageType implements StorageType {
    LOG_STORAGE;

    public String getStorageType() {
        return "LOG_STORAGE";
    }

}

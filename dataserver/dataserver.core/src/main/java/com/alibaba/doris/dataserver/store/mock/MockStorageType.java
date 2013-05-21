package com.alibaba.doris.dataserver.store.mock;

import com.alibaba.doris.dataserver.store.StorageType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public enum MockStorageType implements StorageType {
    MOCK_STORAGE;

    public String getStorageType() {
        return "MockStorage";
    }
}

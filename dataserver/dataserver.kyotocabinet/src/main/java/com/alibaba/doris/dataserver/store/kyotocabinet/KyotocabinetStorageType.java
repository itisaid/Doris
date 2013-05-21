package com.alibaba.doris.dataserver.store.kyotocabinet;

import com.alibaba.doris.dataserver.store.StorageType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public enum KyotocabinetStorageType implements StorageType {
    KYOTOCABINET("Kyotocabinet");

    private KyotocabinetStorageType(String type) {
        this.type = type;
    }

    public String getStorageType() {
        return type;
    }

    private String type;
}

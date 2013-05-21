package com.alibaba.doris.dataserver.store.innodb;

import com.alibaba.doris.dataserver.store.StorageType;

/*
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public enum InnoDBStorageType implements StorageType {
    INNODB("InnoDB");

    private InnoDBStorageType(String storageType) {
        this.storageType = storageType;
    }

    public String getStorageType() {
        return storageType;
    }

    private String storageType;
}

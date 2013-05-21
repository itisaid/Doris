package com.alibaba.doris.dataserver.store.mysql;

import com.alibaba.doris.dataserver.store.StorageType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public enum MysqlStorageType implements StorageType {
    MYSQL("mysql");

    private MysqlStorageType(String type) {
        this.type = type;
    }

    public String getStorageType() {
        return type;
    }

    private String type;
}

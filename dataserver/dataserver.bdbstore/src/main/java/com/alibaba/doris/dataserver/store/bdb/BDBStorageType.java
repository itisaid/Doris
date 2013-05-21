package com.alibaba.doris.dataserver.store.bdb;

import com.alibaba.doris.dataserver.store.StorageType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class BDBStorageType implements StorageType {

    public String getStorageType() {
        return "BDB";
    }
}

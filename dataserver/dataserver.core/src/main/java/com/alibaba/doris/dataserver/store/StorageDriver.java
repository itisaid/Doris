package com.alibaba.doris.dataserver.store;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface StorageDriver {

    public Storage createStorage();

    public void init(StorageConfig config);

    public StorageType getStorageType();
}

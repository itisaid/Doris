package com.alibaba.doris.dataserver.store;

import com.alibaba.doris.dataserver.store.exception.StorageException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class StorageManager {

    public StorageManager() {
        ;
    }

    protected void loadStorage(StorageConfig config) {
        // 从配置文件装载所有的存储驱动
        driver.init(config);
        // 从驱动创建Storage
        storage = driver.createStorage();
    }

    protected void openStorage() {
        storage.open();
    }

    protected void closeStorage() {
        storage.close();
    }

    public Storage getStorage() {
        return storage;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public void registStorage(Class<?> storageDriverClass) {
        try {
            driver = (StorageDriver) storageDriverClass.newInstance();
            this.storageType = driver.getStorageType();
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    private Storage       storage;
    private StorageType   storageType;
    private StorageDriver driver;
}

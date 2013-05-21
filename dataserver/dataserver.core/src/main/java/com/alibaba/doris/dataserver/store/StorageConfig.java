package com.alibaba.doris.dataserver.store;

import com.alibaba.doris.dataserver.config.data.ModuleConfigure;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class StorageConfig {

    public StorageConfig() {
    }

    public StorageConfig(ModuleConfigure moduleConfigure) {
        this.storageModuleConfigure = moduleConfigure;
    }

    public void setStorageTypeClass(String storageTypeClass) {
        this.storageTypeClass = storageTypeClass;
    }

    public void setStorageDriverClass(String storageDriverClass) {
        this.storageDriverClass = storageDriverClass;
    }

    public String getStorageTypeClass() {
        return storageTypeClass;
    }

    public String getStorageDriverClass() {
        return storageDriverClass;
    }

    public String getPropertiesFile() {
        return propertiesFile;
    }

    public void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    public ModuleConfigure getStorageModuleConfigure() {
        return storageModuleConfigure;
    }

    private String          databasePath;
    private String          storageTypeClass;
    private String          storageDriverClass;
    private String          propertiesFile;
    private int             size;                  // 100M
    private ModuleConfigure storageModuleConfigure;
}

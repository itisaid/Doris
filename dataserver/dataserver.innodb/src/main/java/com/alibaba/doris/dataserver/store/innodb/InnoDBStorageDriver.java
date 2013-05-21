package com.alibaba.doris.dataserver.store.innodb;

import java.io.File;

import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.StorageConfig;
import com.alibaba.doris.dataserver.store.StorageDriver;
import com.alibaba.doris.dataserver.store.StorageType;
import com.alibaba.doris.dataserver.store.innodb.config.InnoDBDatabaseConfiguration;
import com.alibaba.doris.dataserver.store.innodb.util.InnoDBConfigUtils;

/*
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class InnoDBStorageDriver implements StorageDriver {

    public Storage createStorage() {
        return new InnoDBStorage(this.config);
    }

    public StorageType getStorageType() {
        return InnoDBStorageType.INNODB;
    }

    public void init(StorageConfig storageConfig) {
        this.storageConfig = storageConfig;
        this.config = InnoDBConfigUtils.loadConfigFile(storageConfig.getPropertiesFile());
        initStorageDriver();
    }

    private void initStorageDriver() {
        checkPath(config.getDataHomeDir(), "data_home_dir");
        checkPath(config.getLogFileHomeDirectory(), "log_file_home_directory");
        config.setSchema("doris");
    }

    private void checkPath(String path, String pathDesc) {
        File dbDir = new File(path);
        if (!dbDir.exists()) {
            if (!dbDir.mkdirs()) {
                throw new InnoDBStorageException("Creating " + pathDesc + " failed. path=" + path);
            }
        }
    }
    
    /**
     * Note: This method is just for testing.
     * @return
     */
    protected InnoDBDatabaseConfiguration getConfig(){
    	return this.config;
    }

    private InnoDBDatabaseConfiguration config;
    private StorageConfig               storageConfig;
}

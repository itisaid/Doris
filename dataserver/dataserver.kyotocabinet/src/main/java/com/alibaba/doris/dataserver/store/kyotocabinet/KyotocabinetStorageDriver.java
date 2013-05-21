package com.alibaba.doris.dataserver.store.kyotocabinet;

import java.io.File;

import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.StorageConfig;
import com.alibaba.doris.dataserver.store.StorageDriver;
import com.alibaba.doris.dataserver.store.StorageType;
import com.alibaba.doris.dataserver.store.kyotocabinet.util.DatabaseUtils;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class KyotocabinetStorageDriver implements StorageDriver {

    public Storage createStorage() {
        return new KyotocabinetStorage(config);
    }

    public StorageType getStorageType() {
        return KyotocabinetStorageType.KYOTOCABINET;
    }

    public void init(StorageConfig storageConfig) {
        loadConfig(storageConfig);
        File dbDir = new File(config.getDatabasePath());
        if (!dbDir.exists()) {
            // logger.info("Creating BDB data directory '" + bdbDir.getAbsolutePath() + ".");
            dbDir.mkdirs();
        }
    }

    private void loadConfig(StorageConfig storageConfig) {
        this.config = DatabaseUtils.loadStorageConfigFromFile(storageConfig.getPropertiesFile());
    }

    private KyotocabinetStorageConfig config;
}

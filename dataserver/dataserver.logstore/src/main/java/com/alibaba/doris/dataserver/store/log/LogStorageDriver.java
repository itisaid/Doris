package com.alibaba.doris.dataserver.store.log;

import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.StorageConfig;
import com.alibaba.doris.dataserver.store.StorageDriver;
import com.alibaba.doris.dataserver.store.StorageType;
import com.alibaba.doris.dataserver.store.log.db.ClumpConfigure;
import com.alibaba.doris.dataserver.store.log.db.ClumpConfigureLoader;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class LogStorageDriver implements StorageDriver {

    public Storage createStorage() {
        return storage;
    }

    public StorageType getStorageType() {
        return LogStorageType.LOG_STORAGE;
    }

    public void init(StorageConfig config) {
        String configFile = config.getPropertiesFile();
        ClumpConfigureLoader loader = new ClumpConfigureLoader(configFile);
        clumpConfig = loader.load();

        String databasePath = config.getDatabasePath();
        // 如果命令行里面设置了数据库文件路径，则覆盖配置文件中的路径设置。
        if (null != databasePath) {
            clumpConfig.setPath(databasePath);
        }
        storage = new LogStorage(clumpConfig);
    }

    public ClumpConfigure getClumpConfigure() {
        return clumpConfig;
    }

    private ClumpConfigure clumpConfig;
    private LogStorage     storage;
}

package com.alibaba.doris.dataserver.store.bdb;

import java.io.File;

import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.StorageConfig;
import com.alibaba.doris.dataserver.store.StorageDriver;
import com.alibaba.doris.dataserver.store.StorageType;
import com.alibaba.doris.dataserver.store.bdb.utils.BDBConfigUtil;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class BDBStorageDriver implements StorageDriver {

    public Storage createStorage() {
        return new BDBStorage(databaseFactory);
    }

    public void init(StorageConfig config) {
        loadConfig(config);
        initStorageDriver();
    }

    private void initStorageDriver() {
        File dbDir = new File(bdbConfig.getDataDirectory());
        if (!dbDir.exists()) {
            // logger.info("Creating BDB data directory '" + bdbDir.getAbsolutePath() + ".");
            dbDir.mkdirs();
        }

        databaseFactory = new BDBDataBaseFactory(environmentConfig, databaseConfig, bdbConfig);
        databaseFactory.initFactory();
    }

    protected Environment getEnv() {
        return env;
    }

    protected DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    protected BDBStorageConfig getBDBStorageConfig() {
        return bdbConfig;
    }

    private void loadConfig(StorageConfig config) {
        bdbConfig = BDBConfigUtil.loadBDBStorageConfigFromFile(config.getPropertiesFile());
        String databasePath = config.getDatabasePath();
        // 如果命令行里面设置了数据库文件路径，则覆盖配置文件中的路径设置。
        if (null != databasePath) {
            bdbConfig.setDataDirectory(databasePath);
        }
        environmentConfig = bdbConfig.getEnvironmentConfig();
        databaseConfig = bdbConfig.getDatabaseConfig();
    }

    public StorageType getStorageType() {
        return type;
    }

    private EnvironmentConfig             environmentConfig;
    private DatabaseConfig                databaseConfig;
    private BDBStorageConfig              bdbConfig;
    private BDBDataBaseFactory            databaseFactory;
    private volatile Environment          env;
    protected static final BDBStorageType type = new BDBStorageType();

}

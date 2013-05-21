package com.alibaba.doris.dataserver.store.bdb.utils;

import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.config.ConfigTools;
import com.alibaba.doris.dataserver.store.bdb.BDBStorageConfig;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.EnvironmentConfig;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class BDBConfigUtil {

    /**
     * 从配置文件中装载BDB存储层的详细配置项。
     * 
     * @param configFile
     * @return
     */
    public static BDBStorageConfig loadBDBStorageConfigFromFile(String configFile) {
        if (StringUtils.isBlank(configFile)) {
            throw new IllegalArgumentException("Configure file can't be empty.");
        }

        Properties properties = ConfigTools.loadProperties(configFile);
        if (null == properties) {
            properties = ConfigTools.loadProperties("default_bdb.properties");
            if (logger.isDebugEnabled()) {
                logger.debug("Couldn't load properties file :" + configFile
                             + " Using default bdb properties file. (default_bdb.properties)");
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Loading bdb properties file: " + configFile);
            }
        }

        BDBStorageConfig config = new BDBStorageConfig();
        config.setCursorPreload(Boolean.valueOf(StringUtils.trim(properties.getProperty(CURSOR_PRELOAD, "true"))));
        String path = StringUtils.trim(properties.getProperty("database.directory"));
        if (StringUtils.isBlank(path)) {
            path = getDataBaseDirectory();
        }
        config.setDataDirectory(path);
        config.setPerDatabaseOneEnvironment(Boolean.valueOf(StringUtils.trim(properties.getProperty(
                                                                                                    PER_DATABASE_ONE_ENV,
                                                                                                    "false"))));
        config.setAllowCreate(Boolean.valueOf(StringUtils.trim(properties.getProperty(ALLOW_CREATE, "false"))));
        config.setDbBtreeFanout(Integer.valueOf(StringUtils.trim(properties.getProperty(DB_BTREE_FANOUT, "0"))));
        config.setSortedDuplicates(Boolean.valueOf(StringUtils.trim(properties.getProperty(DB_SORTED_DUPLICATES,
                                                                                           "false"))));
        config.setDeferredWrite(Boolean.valueOf(StringUtils.trim(properties.getProperty(DB_DEFERRED_WRITE, "false"))));
        // *****remove****//
        removeNonJeProperties(properties);

        EnvironmentConfig envConfig = new EnvironmentConfig(properties);
        envConfig.setAllowCreate(config.isAllowCreate());
        config.setEnvironmentConfig(envConfig);

        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setAllowCreate(config.isAllowCreate());
        databaseConfig.setSortedDuplicates(config.isSortedDuplicates());
        if (config.getDbBtreeFanout() > 0) {
            databaseConfig.setNodeMaxEntries(config.getDbBtreeFanout());
        }
        databaseConfig.setTransactional(envConfig.getTransactional());
        databaseConfig.setDeferredWrite(config.isDeferredWrite());
        config.setDatabaseConfig(databaseConfig);
        return config;
    }

    private static void removeNonJeProperties(Properties properties) {
        properties.remove(CURSOR_PRELOAD);
        properties.remove(DATABASE_DIRECTORY);
        properties.remove(ALLOW_CREATE);
        properties.remove(DB_BTREE_FANOUT);
        properties.remove(DB_SORTED_DUPLICATES);
        properties.remove(DB_DEFERRED_WRITE);
        properties.remove(PER_DATABASE_ONE_ENV);
    }

    public static String getDataBaseDirectory() {
        URL url = BDBConfigUtil.class.getClassLoader().getResource("");
        return url.getPath() + BDB_DATA_PATH;
    }

    private static final String BDB_DATA_PATH        = "bdb_db";
    private static final String CURSOR_PRELOAD       = "cursor.preload";
    private static final String DATABASE_DIRECTORY   = "database.directory";
    private static final String PER_DATABASE_ONE_ENV = "per.database.one.environment";
    private static final String ALLOW_CREATE         = "allow.create";
    private static final String DB_BTREE_FANOUT      = "db.btree.fanout";
    private static final String DB_SORTED_DUPLICATES = "db.sorted.duplicates";
    private static final String DB_DEFERRED_WRITE    = "db.deferred.write";
    private static final Logger logger               = LoggerFactory.getLogger(BDBConfigUtil.class);
}

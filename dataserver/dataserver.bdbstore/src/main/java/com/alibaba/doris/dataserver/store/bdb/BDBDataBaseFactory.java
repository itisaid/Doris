package com.alibaba.doris.dataserver.store.bdb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.dataserver.store.bdb.utils.EnvironmentInfomationManager;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.PreloadConfig;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class BDBDataBaseFactory {

    public BDBDataBaseFactory(EnvironmentConfig environmentConfig, DatabaseConfig databaseConfig,
                              BDBStorageConfig bdbConfig) {
        this.environmentConfig = environmentConfig;
        this.databaseConfig = databaseConfig;
        this.bdbConfig = bdbConfig;
        this.envMap = new ConcurrentHashMap<String, Environment>();
    }

    public void initFactory() {
        try {
            envInfoManager = new EnvironmentInfomationManager(bdbConfig.getDataDirectory());
            for (String envName : envInfoManager.loadAllEnvironmentNames()) {
                loadEnvironment(envName);
            }
        } catch (Exception e) {
            throw new BDBStorageException(e);
        }
    }

    public List<Environment> getEnvironmentList() {
        return new ArrayList<Environment>(envMap.values());
    }

    public BDBDataBase createDataBase(String databaseName) {
        Environment env = getEnvironment(databaseName);

        Database db = env.openDatabase(null, databaseName, databaseConfig);
        if (bdbConfig.isCursorPreload()) {
            PreloadConfig preloadConfig = new PreloadConfig();
            preloadConfig.setLoadLNs(false);
            db.preload(preloadConfig);
        }

        return new BDBDataBase(db);
    }

    public boolean removeDataBase(BDBDataBase database) {
        Database db = database.getDatabase();
        String dbName = db.getDatabaseName();
        database.close();
        Environment e = db.getEnvironment();
        e.removeDatabase(null, dbName);
        return true;
    }

    private Environment createEnvironment(String databaseName) {
        String dbPath = null;
        if (null == databaseName) {
            dbPath = bdbConfig.getDataDirectory();
        } else {
            dbPath = bdbConfig.getDataDirectory() + File.separatorChar + databaseName;
        }

        File dbDir = new File(dbPath);
        if (!dbDir.exists()) {
            // logger.info("Creating BDB data directory '" + bdbDir.getAbsolutePath() + ".");
            dbDir.mkdirs();
        }

        return new Environment(dbDir, environmentConfig);
    }

    private Environment getEnvironment(String databaseName) {
        String envName = DEFAULT_ENV_NAME;
        if (bdbConfig.isPerDatabaseOneEnvironment()) {
            envName = databaseName;
        }

        Environment env = envMap.get(envName);
        if (null != env) {
            return env;
        }

        synchronized (this) {
            env = envMap.get(envName);
            if (null != env) {
                return env;
            }

            env = createEnvironment(envName);
            envMap.put(envName, env);
            String[] envNameArray = new String[envMap.size()];
            envNameArray = envMap.keySet().toArray(envNameArray);
            envInfoManager.saveEnvironmentNames(envNameArray);
            return env;
        }
    }

    private void loadEnvironment(String envName) {
        synchronized (this) {
            String envPath = bdbConfig.getDataDirectory() + File.separatorChar + envName;
            File dbDir = new File(envPath);
            if (!dbDir.exists()) {
                logger.info("The bdb environment [" + envName + "] is not exists!");
                return;
            }

            envMap.put(envName, new Environment(dbDir, environmentConfig));
        }
    }

    private EnvironmentConfig            environmentConfig;
    private DatabaseConfig               databaseConfig;
    private BDBStorageConfig             bdbConfig;
    private Map<String, Environment>     envMap;
    private EnvironmentInfomationManager envInfoManager;
    private static final String          DEFAULT_ENV_NAME = "000000";
    private static final Logger          logger           = LoggerFactory.getLogger(BDBDataBaseFactory.class);
}

package com.alibaba.doris.dataserver.store.bdb;

import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.EnvironmentConfig;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class BDBStorageConfig {

    public String getDataDirectory() {
        return dataDirectory;
    }

    public void setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public boolean isPerDatabaseOneEnvironment() {
        return isPerDatabaseOneEnvironment;
    }

    public void setPerDatabaseOneEnvironment(boolean isPerDatabaseOneEnvironment) {
        this.isPerDatabaseOneEnvironment = isPerDatabaseOneEnvironment;
    }

    public EnvironmentConfig getEnvironmentConfig() {
        return environmentConfig;
    }

    public void setEnvironmentConfig(EnvironmentConfig environmentConfig) {
        this.environmentConfig = environmentConfig;
    }

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    public void setDatabaseConfig(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public boolean isCursorPreload() {
        return isCursorPreload;
    }

    public void setCursorPreload(boolean isCursorPreload) {
        this.isCursorPreload = isCursorPreload;
    }

    public boolean isAllowCreate() {
        return isAllowCreate;
    }

    public void setAllowCreate(boolean isAllowCreate) {
        this.isAllowCreate = isAllowCreate;
    }

    public int getDbBtreeFanout() {
        return dbBtreeFanout;
    }

    public void setDbBtreeFanout(int dbBtreeFanout) {
        this.dbBtreeFanout = dbBtreeFanout;
    }

    public boolean isSortedDuplicates() {
        return isSortedDuplicates;
    }

    public void setSortedDuplicates(boolean isSortedDuplicates) {
        this.isSortedDuplicates = isSortedDuplicates;
    }

    public boolean isDeferredWrite() {
        return deferredWrite;
    }

    public void setDeferredWrite(boolean deferredWrite) {
        this.deferredWrite = deferredWrite;
    }

    private boolean           deferredWrite;
    private boolean           isSortedDuplicates;
    private boolean           isCursorPreload             = true;
    private boolean           isAllowCreate               = true;
    private int               dbBtreeFanout;
    private EnvironmentConfig environmentConfig;
    private DatabaseConfig    databaseConfig;
    private String            dataDirectory;
    private boolean           isPerDatabaseOneEnvironment = false;
}

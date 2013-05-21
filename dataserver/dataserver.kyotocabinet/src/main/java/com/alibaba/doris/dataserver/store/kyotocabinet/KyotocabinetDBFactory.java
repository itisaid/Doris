package com.alibaba.doris.dataserver.store.kyotocabinet;

import java.io.File;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class KyotocabinetDBFactory {

    public KyotocabinetDBFactory(KyotocabinetStorageConfig config) {
        this.config = config;
    }

    public KyotocabinetDatabase createKyotocabinetDatabase(String databaseName) {
        KyotocabinetDatabase database = new KyotocabinetDatabase(databaseName, config);
        return database;
    }

    public boolean removeDatabase(KyotocabinetDatabase database) {
        database.close();
        File dbFile = new File(database.getDatabaseFileName());
        if (!dbFile.delete()) {
            throw new KyotocabinetStorageException("Delete database file failed. File name:"
                                                   + database.getDatabaseFileName());
        }
        return true;
    }

    private KyotocabinetStorageConfig config;
}

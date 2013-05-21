package com.alibaba.doris.dataserver.store.kyotocabinet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.alibaba.doris.common.config.ConfigTools;
import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.kyotocabinet.KyotocabinetStorage;
import com.alibaba.doris.dataserver.store.kyotocabinet.KyotocabinetStorageConfig;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class KyotocabinetStorageTest /* extends StorageTestUnit */{

    protected Storage getStorage() {
        if (null == storage) {
            storage = new KyotocabinetStorage(getConfig());
        }

        return storage;
    }

    protected void setUp() throws Exception {
        try {
            FileUtils.forceDelete(new File(getConfig().getDatabasePath()));
        } catch (FileNotFoundException ignore) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File f = new File(config.getDatabasePath());
        if (!f.exists()) {
            f.mkdir();
        }
    }

    private KyotocabinetStorageConfig getConfig() {
        if (null == config) {
            config = new KyotocabinetStorageConfig();
            String path = ConfigTools.getCurrentClassPath(KyotocabinetStorageTest.class);
            path = path.substring(1);
            config.setDatabasePath(path + File.separatorChar + "db_test");
        }

        return config;
    }

    private KyotocabinetStorage       storage;
    private KyotocabinetStorageConfig config;
}

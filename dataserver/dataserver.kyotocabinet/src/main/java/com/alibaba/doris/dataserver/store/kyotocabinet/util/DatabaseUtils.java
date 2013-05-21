package com.alibaba.doris.dataserver.store.kyotocabinet.util;

import com.alibaba.doris.dataserver.store.kyotocabinet.KyotocabinetStorageConfig;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DatabaseUtils {

    public static KyotocabinetStorageConfig loadStorageConfigFromFile(String p) {
        KyotocabinetStorageConfig config = new KyotocabinetStorageConfig();
        String path = DatabaseUtils.class.getClassLoader().getResource("").getPath();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        config.setDatabasePath(path);

        return config;
    }

    public static void main(String[] args) {
        loadStorageConfigFromFile(null);
    }
}

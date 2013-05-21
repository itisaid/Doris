package com.alibaba.doris.dataserver.store.kyotocabinet;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class KyotocabinetStorageConfig {

    public String getDatabasePath() {
        return path;
    }

    public void setDatabasePath(String path) {
        this.path = path;
    }

    private String path;
}

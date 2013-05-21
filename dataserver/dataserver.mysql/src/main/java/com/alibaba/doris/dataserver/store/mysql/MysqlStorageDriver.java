package com.alibaba.doris.dataserver.store.mysql;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;

import com.alibaba.doris.common.config.ConfigTools;
import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.StorageConfig;
import com.alibaba.doris.dataserver.store.StorageDriver;
import com.alibaba.doris.dataserver.store.StorageType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class MysqlStorageDriver implements StorageDriver {

    private DataSource datasource;
    private String     tableName;

    public Storage createStorage() {
        return new MysqlDatabase(tableName, datasource);
    }

    public StorageType getStorageType() {
        return MysqlStorageType.MYSQL;
    }

    public void init(StorageConfig config) {
        Properties properties = ConfigTools.loadProperties(config.getPropertiesFile());

        if (properties == null) {
            throw new RuntimeException("load file from " + config.getPropertiesFile() + " failed ");
        }

        tableName = properties.getProperty("doris_mysql.table", "doris_store");

        try {
            datasource = BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            throw new RuntimeException("data source init error" + e.getMessage(), e);
        }
    }

}

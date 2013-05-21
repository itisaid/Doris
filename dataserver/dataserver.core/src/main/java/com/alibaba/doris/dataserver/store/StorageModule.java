package com.alibaba.doris.dataserver.store;

import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.KeyFactory;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.ValueFactory;
import com.alibaba.doris.dataserver.BaseModule;
import com.alibaba.doris.dataserver.FatalModuleInitializationException;
import com.alibaba.doris.dataserver.ModuleContextAware;
import com.alibaba.doris.dataserver.ModuleStatusChecker;
import com.alibaba.doris.dataserver.action.data.CheckActionData;
import com.alibaba.doris.dataserver.action.data.CheckActionData.CheckType;
import com.alibaba.doris.dataserver.config.data.ModuleConfigure;
import com.alibaba.doris.dataserver.store.exception.StorageException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class StorageModule extends BaseModule implements ModuleStatusChecker, ModuleContextAware {

    /**
     * 初始化存储层模块
     */
    public void load(ModuleConfigure conf) {
        try {
            // 根据DataServer的配置信息，生成存储的配置文件对象。
            StorageConfig storageConfig = getStorageConfig(conf);
            Class<?> storageDriverClass = getStorageDriverClass(storageConfig);

            manager = new StorageManager();
            // 向驱动管理器注册当前节点配置的存储驱动。
            manager.registStorage(storageDriverClass);
            // 调用管理器的装载函数，初始化并装载存储驱动。
            manager.loadStorage(storageConfig);
            // 打开存储DB，并初始化存储层相关数据。
            manager.openStorage();
        } catch (Throwable e) {
            // 存储层初始化抛出任何异常，系统需要终止启动。
            throw new FatalModuleInitializationException(e);
        }
    }

    public void unload() {
        manager.closeStorage();
    }

    public boolean isReady(CheckActionData checkActionData) {
        try {
            Storage storage = manager.getStorage();
            String clazzName = storage.getClass().getName();
            CheckType checkType = checkActionData.getCheckType();
            // 判断是否是Log存储层
            if (clazzName.indexOf("LogStorage") >= 0) {
                if (checkType == null || checkType == CheckType.CHECK_TEMP_NODE
                    || checkType == CheckType.CHECK_STANDBY_NODE) {
                    Iterator<Pair> iterator = storage.iterator();
                    if (null != iterator) {
                        if (iterator instanceof ClosableIterator) {
                            ((ClosableIterator<Pair>) iterator).close();
                        }
                        return true;
                    }
                }
            } else {
                if (checkType == null || checkType == CheckType.CHECK_NORMAL_NODE
                    || checkType == CheckType.CHECK_STANDBY_NODE) {
                    storage.set(key, value);
                    Value v = storage.get(key);
                    if (v != null) {
                        if (v.equals(value)) {
                            return true;
                        }
                    }
                    return true;
                }
            }
        } catch (Throwable e) {
            logger.error("Checking module isReady.", e);
        }

        return false;
    }

    public Storage getStorage() {
        return manager.getStorage();
    }

    private StorageConfig getStorageConfig(ModuleConfigure conf) {
        StorageConfig storageConfig = new StorageConfig(conf);
        Properties commandLine = conf.getDataServerConfigure().getCommandLine();
        String dbPath = commandLine.getProperty("dbPath");
        if (StringUtils.isNotBlank(dbPath)) {
            storageConfig.setDatabasePath(dbPath);
        }

        String driverClass = conf.getParam("driver");
        storageConfig.setStorageDriverClass(driverClass);
        storageConfig.setPropertiesFile(conf.getParam("configFile"));
        storageConfig.setSize(conf.getParamAsInt("size", 100 * 1024 * 1024 * 1024));

        return storageConfig;
    }

    private Class<?> getStorageDriverClass(StorageConfig storageConfig) {
        try {
            Class<?> clazz = Class.forName(storageConfig.getStorageDriverClass());
            return clazz;
        } catch (ClassNotFoundException e) {
            throw new StorageException(e);
        }
    }

    private StorageManager      manager;
    private static final Logger logger = LoggerFactory.getLogger(StorageModule.class);
    private static final Key    key    = KeyFactory.createKey(999999999, "key1", 0);
    private static final Value  value  = ValueFactory.createValue("_check_value_".getBytes(),
                                                                  System.currentTimeMillis());
}

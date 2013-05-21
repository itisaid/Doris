/**
 * Project: doris.config.client-1.0-SNAPSHOT File Created at 2011-4-26 $Id: ConfigManagerImpl.java 97082 2011-06-27
 * 07:15:00Z mian.hem $ Copyright 1999-2100 Alibaba.com Corporation Limited. All rights reserved. This software is the
 * confidential and proprietary information of Alibaba Company. ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.doris.common.config;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.common.adminservice.AdminServiceFactory;
import com.alibaba.doris.common.adminservice.CommonConfigService;
import com.alibaba.doris.common.adminservice.connenctor.AdminConnector;
import com.alibaba.doris.common.util.PropertiesLoadUtil;

/**
 * 配置管理客户端， 定时（默认是3000ms） 通过http 调用方式访问配置管理服务器取得最新配置 并推送给注册在 <code>ConfigManagerImpl</code>中的<code>ConfigListener</code>
 * 。 如果配置管理客户端没有取得最新配置，就不会推送给<code>ConfigListener</code>。
 * 
 * @see ConfigListener
 * @see ConfigConnector
 * @author mianhe
 */
public class ConfigManagerImpl implements ConfigManager {

    private static final Log              logger              = LogFactory.getLog(ConfigManagerImpl.class);

    // configuration location
    private String                        location;
    private long                          interval            = 3000;
    // 客户端是否处于停止状态
    private volatile boolean              stopped             = false;

    // 是否初始化成功
    private volatile boolean              initialized         = false;

    private final Object                  lock                = new Object();

    private volatile List<ConfigListener> configListeners     = new ArrayList<ConfigListener>();
    
    private Properties                    properties          = null;
    // 初始化的时候生成的独立线程，此线程用来定时去取最新配置
    private Thread                        fetchThread;

    private CommonConfigService           commonConfigService = AdminServiceFactory.getCommonConfigService();

    private volatile Map<String, String>  configurationCache  = new ConcurrentHashMap<String, String>();

    private ClientConfiguration           clientConfiguration;

    /**
     * 初始化配置客户端。 初始化之前必须设置正确的<code>ConfigConnector</code>， 否则初始化失败。初始化后会有独立新线程定时发送请求去取最新的配置。
     * 
     * @throws ConfigException 初始化之前必须设置正确的 <code>ConfigConnector</code>， 否则初始化抛异常而失败。
     */
    public void initConfig() throws ConfigException {
        if (initialized) {
            logger.warn("ConfigManagerImpl is already initialized!");
            return;
        }
        
        Properties defaultProperties = loadDefaultConfig();

        // properties为空，则通过location加载配置，否则将properties中所有的值转换为String
        if (properties == null) {
            this.properties = PropertiesLoadUtil.loadProperties(location);
        } else {
            Properties tempProperties = new Properties();
            Enumeration<Object> keysEnum = properties.keys();
            while (keysEnum.hasMoreElements()) {
                Object key = keysEnum.nextElement();
                if (key instanceof String) {
                    tempProperties.setProperty((String)key, properties.get(key).toString());
                } else {
                    throw new ConfigException(String.format(
                                            "The key[%s]'type of client properties file must be 'String',not support the type:%s",
                                            key, key.getClass()));
                }
            }
            this.properties = tempProperties;
        }

        if( defaultProperties != null) {
            
        	defaultProperties.putAll( properties );
        	
        	properties = defaultProperties;
        }
        
        loadClientConfiguration(properties);
        // initialize the connector
        AdminConnector adminConnector = AdminConnector.getInstance();
        adminConnector.init(properties);// this init must be execute when client initialized

        if (!adminConnector.isConnected()) {
            String mainAdminUrl = properties.getProperty("doris.config.adminserver.main.url");
            String backupAdminUrl = properties.getProperty("doris.config.adminserver.backup.url");

            throw new ConfigException("admin server is not available,main admin:" + mainAdminUrl + ",backup admin:"
                                      + backupAdminUrl);
        }

        String autoFetchProp = properties.getProperty("doris.admin.config.autofetch.enable", "true");
        boolean autoFetchEnable = Boolean.parseBoolean(autoFetchProp);

        if (autoFetchEnable) {
            // the interval to dectect configuration changes.
            String intervalConfig = properties.getProperty("doris.config.fetch.interval");
            if (StringUtils.isEmpty(intervalConfig)) {
                throw new IllegalArgumentException("confg 'doris.config.fetch.interval' is not valid.");
            }
            this.interval = Long.parseLong(intervalConfig.trim());
            
            // start fetch thread.
            fetchThread = new Thread(new ConfigFetchTask(), "doris-config-fetcher");
            fetchThread.setDaemon(true);
            fetchThread.start();
        }

        // initialize completed.
        initialized = true;
    }

    public Properties loadDefaultConfig() {
        return null;
    }

    private void loadClientConfiguration(Properties property) {
        clientConfiguration = new ClientConfiguration();
        // To get the time out value;
        String timeout = property.getProperty("doris.config.client.operation.timeout");
        if (StringUtils.isNotBlank(timeout)) {
            clientConfiguration.setTimeoutOfOperation(NumberUtils.toLong(timeout));
        }
    }

    public ClientConfiguration getClientConfiguration() {
        return this.clientConfiguration;
    }

    /**
     * 关闭配置管理客户端。
     * 
     * @throws ConfigException
     */
    public void close() throws ConfigException {
        if (logger.isInfoEnabled()) {
            logger.info("Closing config fetcher...");
        }

        stopped = true;
        initialized = false;
        fetchThread.interrupt();
    }

    private class ConfigFetchTask implements Runnable {

        public void run() {
            try {
                while (!stopped) {
                    try {
                        Thread.sleep(interval);
                    } catch (Exception e) {
                        logger.warn("Exception when the key fetch task sleep: " + e.toString());
                    }

                    if (stopped) {
                        return;
                    }

                    try {
                        fetch(null, false);
                    } catch (Exception e) {
                        logger.error("Exception when fetch doris config: ", e);
                    }
                }
            } finally {
                logger.warn("the key fetch thread exit!");
            }
        }
    }

    /**
     * 取得所有监听器所关心的配置。
     * 
     * @param b
     */
    private void fetch(Map<String, String> map, boolean forceRefresh) {
        synchronized (lock) {
            Map<String, String> configurations = null;
            if (map == null) {
                if (this.configListeners == null || this.configListeners.isEmpty()) {
                    return;
                }
                Map<String, Long> actionVersions = new HashMap<String, Long>();
                for (ConfigListener confLstnr : configListeners) {
                    if (forceRefresh) {
                        actionVersions.put(confLstnr.getConfigListenerName(), null);
                    } else {
                        actionVersions.put(confLstnr.getConfigListenerName(), confLstnr.getConfigVersion());
                    }
                }

                if (forceRefresh) {
                    List<String> actions = new ArrayList<String>(actionVersions.keySet());
                    configurations = commonConfigService.getConfig(actions);
                } else {
                    configurations = commonConfigService.getConfig(actionVersions);
                }
            } else {
                configurations = map;
            }

            for (ConfigListener confLstnr : configListeners) {
                String lsnerName = confLstnr.getConfigListenerName();
                String conf = configurations.get(lsnerName);
                if (StringUtils.isNotEmpty(conf) && !"null".equalsIgnoreCase(conf)) {
                    configurationCache.put(lsnerName, conf);
                    confLstnr.onConfigChange(conf);
                }
            }
        }
    }

    public void refreshConfig() {
        fetch(null, true);
    }

    public void refreshConfig(Map<String, String> configurations) {
        fetch(configurations, false);
    }

    /**
     * 注册配置管理实例，这些实例在配置变更的时候将会被触发。
     * 
     * @param configManager 用来接受配置变更的Doris配置管理实例
     * @throws IllegalStateException 没有被初始化会抛异常
     */
    public void addConfigListener(ConfigListener configListener) {
        if (this.initialized) {
            this.configListeners.add(configListener);
            fetch(null, true);
        } else {
            throw new IllegalStateException("the configuration manager is not initialized yet.");
        }
    }

    /**
     * 解注册配置管理实例，这些实例在配置变更的时候将不会被触发。
     * 
     * @param configManager 用来接受配置变更的Doris配置管理实例
     * @throws IllegalStateException 没有被初始化会抛异常
     */
    public void removeConfigListener(ConfigListener configManager) {
        if (this.initialized) {
            this.configListeners.remove(configManager);
        } else {
            throw new IllegalStateException("the configuration manager is not initialized yet.");
        }
    }

    /**
     * set config properties.
     * @since 0.1.4
     */
    public void setConfigProperties(Properties properties){
        this.properties = properties;
    }

    /**
     * set config location, local or remote.
     */
    public void setConfigLocation(String location) {
        this.location = location;
    }

    /**
     * get config properties.
     */
    public Properties getProperties() {
        return properties;
    }

    public String getConfig(String actionName) {
        return getConfig(actionName, null);
    }
    
    public String getConfig(String actionName, Long version) {
        Map<String, Long> paras = new HashMap<String, Long>();
        paras.put(actionName, version);
        Map<String, String> rsltMap = commonConfigService.getConfig(paras);
        return rsltMap.get(actionName);
    }

    public String getCachedConfig(String actionName) {
        return configurationCache.get(actionName);
    }

}

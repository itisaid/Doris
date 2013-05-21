package com.alibaba.doris.common.config;

import java.util.Map;
import java.util.Properties;

/**
 * ConfigManager
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-17
 */
public interface ConfigManager {
    
    void refreshConfig(Map<String, String> configurations);

    public void setConfigLocation(String location);

    public void setConfigProperties(Properties properties);

    public void initConfig() throws ConfigException;

    public Properties getProperties();

    public void addConfigListener(ConfigListener listener);

    public void removeConfigListener(ConfigListener listener);

    public String getConfig(String listenerName);

    public String getConfig(String listenerName, Long version);
    
    public String getCachedConfig(String listenerName);
    
    public void refreshConfig();

    public Properties loadDefaultConfig();

    public ClientConfiguration getClientConfiguration();
}

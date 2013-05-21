/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * MockConfigManager
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-17
 */
public class MockConfigManager implements ConfigManager {

    private Properties properties;
    protected String   location;

    /*
     * (non-Javadoc)
     * @see
     * com.alibaba.doris.common.config.ConfigManager#addConfigListener(com.alibaba.doris.common.config.ConfigListener)
     */
    public void addConfigListener(ConfigListener listener) {

    }

    /*
     * (non-Javadoc)
     * @see
     * com.alibaba.doris.common.config.ConfigManager#deleteConfigListener(com.alibaba.doris.common.config.ConfigListener
     * )
     */
    public void removeConfigListener(ConfigListener listener) {

    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.doris.common.config.ConfigManager#getConfig()
     */
    public String getConfig() {

        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.doris.common.config.ConfigManager#getConfig(long)
     */
    public String getConfig(long version) {

        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.doris.common.config.ConfigManager#getProperties()
     */
    public Properties getProperties() {

        return properties;
    }

    /**
     * 初始化配置客户端。 初始化之前必须设置正确的<code>ConfigConnector</code>， 否则初始化失败。初始化后会有独立新线程定时发送请求去取最新的配置。
     * 
     * @throws ConfigException 初始化之前必须设置正确的 <code>ConfigConnector</code>， 否则初始化抛异常而失败。
     */
    public void initConfig() throws ConfigException {

        URL url = getClass().getClassLoader().getResource(location);
        InputStream is = null;

        try {
            is = url.openStream();
            properties = new Properties();
            properties.load(is);
        } catch (IOException e) {
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.doris.common.config.ConfigManager#setConfigLocation(java.lang.String)
     */
    public void setConfigLocation(String location) {
        this.location = location;
    }

    public String getConfig(String listenerName) {
        return null;
    }

    public String getConfig(String listenerName, Long version) {
        return null;
    }

    public void refreshConfig(Map<String, String> configurations) {
    }

    public String getCachedConfig(String listenerName) {
        return null;
    }

    public void refreshConfig() {
    }

    public Properties loadDefaultConfig() {
        return null;
    }

    public ClientConfiguration getClientConfiguration() {
        return null;
    }

    public void setConfigProperties(Properties properties) {

    }

}

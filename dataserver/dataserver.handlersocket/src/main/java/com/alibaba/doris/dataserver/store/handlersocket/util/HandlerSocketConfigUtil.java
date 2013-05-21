package com.alibaba.doris.dataserver.store.handlersocket.util;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.doris.common.config.ConfigTools;
import com.alibaba.doris.dataserver.store.handlersocket.HandlerSocketStorageConfig;


public class HandlerSocketConfigUtil {
	
    private static final Logger logger = LoggerFactory.getLogger(HandlerSocketConfigUtil.class);
    
	public static HandlerSocketStorageConfig loadHandlerSocketStorageConfigFromFile(String configFile) {
		
        if (StringUtils.isBlank(configFile)) {
            throw new IllegalArgumentException("Configure file can't be empty.");
        }

        Properties properties = ConfigTools.loadProperties(configFile);
        if (null == properties) {
            properties = ConfigTools.loadProperties("default_handlersocket.properties");
            if (logger.isDebugEnabled()) {
                logger.debug("Couldn't load properties file :" + configFile
                             + " Using default handlersocket properties file. (default_handlersocket.properties)");
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Loading handlersocket properties file: " + configFile);
            }
        }

        HandlerSocketStorageConfig config = new HandlerSocketStorageConfig();
        
        config.setHost(StringUtils.trim(StringUtils.trim(properties.getProperty("host"))));
        config.setrPort(Integer.parseInt(StringUtils.trim(properties.getProperty("rPort","9998"))));
        config.setWrPort(Integer.parseInt(StringUtils.trim(properties.getProperty("wrPort","9999"))));
        config.setTcpNoDelay(Boolean.valueOf(StringUtils.trim(properties.getProperty("tcpNoDelay","true"))));
        config.setReuseAddress(Boolean.valueOf(StringUtils.trim(properties.getProperty("reuseAddress","true"))));
        config.setReadOnly(Boolean.valueOf(StringUtils.trim(properties.getProperty("readOnly","false"))));
        config.setSoTimeout(Integer.parseInt(StringUtils.trim(properties.getProperty("soTimeout","30000"))));
        config.setSendBufferSize(Integer.parseInt(StringUtils.trim(properties.getProperty("sendBufferSize","8192"))));
        config.setReceiveBufferSize(Integer.parseInt(StringUtils.trim(properties.getProperty("receiveBufferSize","8192"))));
        config.setExecuteBufferSize(Integer.parseInt(StringUtils.trim(properties.getProperty("executeBufferSize","8192"))));
        config.setBlocking(Boolean.valueOf(StringUtils.trim(properties.getProperty("blocking","false"))));
        config.setHardClose(Boolean.valueOf(StringUtils.trim(properties.getProperty("hardClose","false"))));
        config.setAuth(Boolean.valueOf(StringUtils.trim(properties.getProperty("isAuth","false"))));
        config.setaKey(StringUtils.trim(properties.getProperty("aKey","null")));
        config.setDbName(StringUtils.trim(properties.getProperty("dbname")));
        return config;
	}
}

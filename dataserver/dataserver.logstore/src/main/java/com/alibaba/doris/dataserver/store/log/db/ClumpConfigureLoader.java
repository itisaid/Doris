package com.alibaba.doris.dataserver.store.log.db;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.config.ConfigTools;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ClumpConfigureLoader {

    public ClumpConfigureLoader(String configFile) {
        this.configFile = configFile;
    }

    public ClumpConfigure load() {
        Properties prop = null;
        if (StringUtils.isNotBlank(configFile)) {
            prop = ConfigTools.loadProperties(configFile);
        }

        if (null == prop) {
            prop = ConfigTools.loadProperties("default_log_storage.properties");
            if (logger.isDebugEnabled()) {
                logger.debug("Couldn't load properties file :" + configFile
                             + " Using default log storage properties file. (default_log_storage.properties)");
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Loading log storage properties file: " + configFile);
            }
        }

        ClumpConfigure conf = new ClumpConfigure();
        String path = prop.getProperty("log.storage.path");
        if (StringUtils.isBlank(path)) {
            path = getDefaultPath();
        }
        conf.setPath(path);

        String value = prop.getProperty("log.storage.read.buffer.size");
        if (StringUtils.isNotBlank(value)) {
            conf.setReadBufferSize(Integer.valueOf(value));
        }

        value = prop.getProperty("log.storage.write.buffer.size");
        if (StringUtils.isNotBlank(value)) {
            conf.setWriteBufferSize(Integer.valueOf(value));
        }

        value = prop.getProperty("log.storage.write.direct");
        if (StringUtils.isNotBlank(value)) {
            conf.setWriteDirect(Boolean.valueOf(value));
        }
        return conf;
    }

    private String getDefaultPath() {
        return this.getClass().getClassLoader().getResource("").getPath();
    }

    private String              configFile;
    private static final Logger logger = LoggerFactory.getLogger(ClumpConfigureLoader.class);
}

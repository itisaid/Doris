package com.alibaba.doris.common.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ConfigTools {

    public static Properties loadProperties(String fileName) {
        Properties properties = loadPropertiesFromAbsPath(fileName);
        if (null != properties) {
            return properties;
        }

        return loadPropertiesFromRelativePath(fileName);
    }

    public static Properties loadPropertiesFromAbsPath(String absFileName) {
        File f = new File(absFileName);
        if (f.exists()) {
            try {
                InputStream inStream = new FileInputStream(f);
                Properties properties = new Properties();
                properties.load(inStream);
                return properties;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    public static Properties loadPropertiesFromRelativePath(String relativeFileName) {
        return loadPropertiesFromRelativePath(ConfigTools.class.getClassLoader(), relativeFileName);
    }

    public static Properties loadPropertiesFromRelativePath(ClassLoader classLoader, String relativeFileName) {
        URL url = classLoader.getResource(relativeFileName);
        InputStream is = null;
        Properties properties = null;

        if (url != null) {
            try {
                is = url.openStream();
                properties = new Properties();
                properties.load(is);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return properties;
    }

    public static String getCurrentClassPath(Class<?> clazz) {
        String path = clazz.getClassLoader().getResource("").getPath();
        String clazzName = clazz.getName();
        int index = clazzName.lastIndexOf('.');
        if (index > 0) {
            clazzName = clazzName.substring(0, index);
        }

        return path + clazzName.replace('.', File.separatorChar);
    }
}

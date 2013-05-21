package com.alibaba.doris.common.config;

import java.util.Properties;

import junit.framework.TestCase;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ConfigToolsTest extends TestCase {

    public void testLoadAbsFile() {
        String path = ConfigToolsTest.class.getClassLoader().getResource("").getPath();
        String absFileName = path + fileName;
        System.out.println(absFileName+"==============");
        Properties prop = ConfigTools.loadPropertiesFromAbsPath(absFileName);
        assertNotNull(prop);
        assertEquals("field", prop.getProperty("field"));
        assertEquals("field2", prop.getProperty("field2"));
    }

    public void testLoadRelativeFile() {
        Properties prop = ConfigTools.loadPropertiesFromRelativePath(fileName);
        assertNotNull(prop);
        assertEquals("field", prop.getProperty("field"));
        assertEquals("field2", prop.getProperty("field2"));
    }

    private String fileName = "configToolsTestFile.properties";
}

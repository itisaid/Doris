package com.alibaba.doris.common.util;

import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Test;

public class PropertiesLoadUtilTest extends TestCase {

    @Test
    public void testProperty() {
        Properties properties = PropertiesLoadUtil.loadProperties("./test/test/test/testp.properties");
        assertEquals("value1", properties.get("key1"));
    }
}

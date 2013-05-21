package com.alibaba.doris.admin.core;

import junit.framework.TestCase;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.doris.admin.service.PropertiesService;

public class PropertiesServiceTest extends TestCase {

    PropertiesService ps;

    public void setUp() {
        new ClassPathXmlApplicationContext("classpath:/spring/doris_service_context.xml");
        ps = AdminServiceLocator.getPropertiesService();
    }

    @Test
    public void testGetProperties(){
        String value = ps.getProperty("foreverFailTime");
        assertEquals("100000", value);
        value = ps.getProperty("nodeCheckTimeout");
        assertEquals("1000", value);
        value = ps.getProperty("nodeCheckRetries");
        assertEquals("3", value);
        
        Integer i =ps.getProperty("foreverFailTime", Integer.class);
        assertEquals(100000, i.intValue());
        
        int defInt =ps.getProperty("notExisting", Integer.TYPE, 1);
        assertEquals(1, defInt);
    }
    
}

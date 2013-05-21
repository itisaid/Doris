package com.alibaba.doris.dataserver.store.bdb.utils;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class EnvironmentInfomationManagerTest extends TestCase {

    public void testReadAndWrite() throws IOException {
        EnvironmentInfomationManager manager = new EnvironmentInfomationManager(getCurrentClassPath());
        String[] envNames = new String[] { "0000", "0001", "0002", "0003", "0004" };
        manager.saveEnvironmentNames(envNames);
        String[] readEnvNames = manager.loadAllEnvironmentNames();
        assertNotNull(readEnvNames);
        assertEquals(envNames.length, readEnvNames.length);
    }

    public void testReadAndWrite2() throws IOException {
        String path = getClass().getClassLoader().getResource("").getPath();
        EnvironmentInfomationManager manager = new EnvironmentInfomationManager(path);
        String[] envNames = new String[] { "0000", "0001", "0002", "0003", "0004" };
        manager.saveEnvironmentNames(envNames);
        String[] readEnvNames = manager.loadAllEnvironmentNames();
        assertNotNull(readEnvNames);
        assertEquals(envNames.length, readEnvNames.length);
    }
    
    protected String getCurrentClassPath() {
        Class<?> clazz = this.getClass();
        String path = clazz.getClassLoader().getResource("").getPath();
        String clazzName = clazz.getName();
        int index = clazzName.lastIndexOf('.');
        if (index > 0) {
            clazzName = clazzName.substring(0, index);
        }

        return path + clazzName.replace('.', File.separatorChar);
    }
}

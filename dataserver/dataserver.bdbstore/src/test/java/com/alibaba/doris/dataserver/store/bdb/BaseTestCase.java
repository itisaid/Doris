package com.alibaba.doris.dataserver.store.bdb;

import java.io.File;

import junit.framework.TestCase;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public abstract class BaseTestCase extends TestCase {

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

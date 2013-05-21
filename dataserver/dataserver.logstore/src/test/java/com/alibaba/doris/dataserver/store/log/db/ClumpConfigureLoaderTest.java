package com.alibaba.doris.dataserver.store.log.db;

import junit.framework.TestCase;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ClumpConfigureLoaderTest extends TestCase {

    public void testLoadConfigure() {
        ClumpConfigureLoader loader = new ClumpConfigureLoader("log_storage_load_test.properties");
        ClumpConfigure conf = loader.load();
        assertNotNull(conf);

        assertEquals(5242880, conf.getReadBufferSize());
        assertEquals(5242880, conf.getWriteBufferSize());
        assertNotNull(conf.getPath());
        assertFalse(conf.isWriteDirect());
    }
}

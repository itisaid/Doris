package com.alibaba.doris.dataserver.store.bdb;

import junit.framework.TestCase;

import com.alibaba.doris.dataserver.store.bdb.utils.BDBConfigUtil;
import com.sleepycat.je.EnvironmentConfig;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class BDBConfigUtilsTest extends TestCase {

    public void testLoadConfig() {
        BDBStorageConfig config = BDBConfigUtil.loadBDBStorageConfigFromFile("bdb_test_mock.properties");
        assertNotNull(config);
        assertFalse(config.isAllowCreate());
        assertFalse(config.isCursorPreload());
        assertTrue(config.isSortedDuplicates());
        assertEquals(10, config.getDbBtreeFanout());

        EnvironmentConfig envConfig = config.getEnvironmentConfig();
        assertTrue(envConfig.getTransactional());
        assertEquals("30", envConfig.getConfigParam(EnvironmentConfig.LOCK_N_LOCK_TABLES));
    }
}

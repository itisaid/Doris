package com.alibaba.doris.dataserver.store.handlersocket;

import junit.framework.TestCase;
import com.alibaba.doris.dataserver.store.handlersocket.util.HandlerSocketConfigUtil;

public class HandlerSocketConfigUtilsTest extends TestCase {

    public void testLoadConfig() {
        HandlerSocketStorageConfig config = HandlerSocketConfigUtil.loadHandlerSocketStorageConfigFromFile("handlersocket_test.properties");
        assertNotNull(config);
    }
}

package com.alibaba.doris.dataserver.store.log.db;

import java.io.File;

import com.alibaba.doris.dataserver.store.log.BaseTestCase;
import com.alibaba.doris.dataserver.store.log.entry.ClumpHeadEntry;
import com.alibaba.doris.dataserver.store.log.utils.LogFileUtil;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class LogClumpManagerTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        config = getClumpConfigure();
        config.setPath(getCurrentClassPath() + File.separatorChar + "data" + File.separatorChar);
        manager = new LogClumpManager(config);
    }

    @Override
    protected void tearDown() throws Exception {
        manager.releaseAllResources();
    }

    public void testListAvaliableLogClumps() {
        LogClump[] clumpArray = manager.listAvaliableLogClumps();
        assertNotNull(clumpArray);
    }

    public void testMaxFileSize() {
        ClumpConfigure conf = null;
        LogClump clump2 = null;
        try {
            conf = getClumpConfigure();
            conf.setMaxFileSize(1024 * 10);
            conf.setPath(getCurrentClassPath() + File.separatorChar + "data" + File.separatorChar);
            LogClumpManager m = new LogClumpManager(conf);
            LogClump clump = m.getLogClump();
            WriteWindow writer = clump.getWriteWindow();
            writer.append(generateLogEntry());
            clump2 = m.getLogClump();
            writer.close();
            assertNotSame(clump.getName(), clump2.getName());
            assertEquals(2, clump2.getNo());
        } finally {
            if (null != clump2) {
                LogFileUtil.deleteClumpFile(conf.getPath(), clump2.getName());
            }
        }
    }

    public void testGetLogClump() {
        LogClump clump = manager.getLogClump();
        assertNotNull(clump);
        // 默认获取编号最大的日志文件
        assertEquals(1, clump.getNo());
        ClumpHeadEntry head = clump.getClumpHeadEntry();
        assertNotNull(head);
        assertTrue(head.getVnodeNum() > 0);
    }

    public void testGetLogClump0() {
        config.setPath(getCurrentClassPath() + File.separatorChar);
        LogClumpManager m = new LogClumpManager(config);
        LogClump clump = m.getLogClump();
        assertNotNull(clump);
    }

    public void testDeleteClumpByVnodes() {

    }

    private LogClumpManager manager;
    private ClumpConfigure  config;
}

package com.alibaba.doris.dataserver.store.log.utils;

import java.io.File;

import com.alibaba.doris.dataserver.store.log.BaseTestCase;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class LogFileUtilTest extends BaseTestCase {

    public void testListAllLogClumpFileName() {
        String[] fileNames = LogFileUtil.listAllLogClumpFileName(getCurrentClassPath() + File.separatorChar + "data");
        assertNotNull(fileNames);
        assertEquals(2, fileNames.length);
    }

    public void testGenerateFileName() {
        String path = getCurrentClassPath() + File.separatorChar;
        String clumpName = "000001";
        String headFileName = LogFileUtil.generateHeadFileName(path, clumpName);
        assertNotNull(headFileName);
        String dataFileName = LogFileUtil.generateDataFileName(path, clumpName);
        assertNotNull(dataFileName);

        assertEquals(clumpName, LogFileUtil.parseClumpNameFromFileName(headFileName));
        assertEquals(clumpName, LogFileUtil.parseClumpNameFromFileName(dataFileName));

        String cn = LogFileUtil.generateClumpName(0);
        assertNotNull(cn);
        assertTrue(cn.length() > 1);

        assertEquals(1, LogFileUtil.getClumpNoFromClumpName(clumpName));
    }
}

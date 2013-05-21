package com.alibaba.doris.admin.web.configer.util;

import junit.framework.TestCase;

public class PhysicalNodeUtilTest extends TestCase {

    public void testIsLegalMigrateNodes() {
        String[] newNodes0 = { "10.20.30.40:8000#1#10.20.30.40", "10.20.30.40:8001#2#10.20.30.40" };
        boolean isLegal = PhysicalNodeUtil.isLegalMigrateNodes(newNodes0);
        assertEquals(false, isLegal);
        String[] newNodes1 = { "10.20.30.40:8000#1#10.20.30.40", "10.20.30.40:8001#1#10.20.30.40" };
        isLegal = PhysicalNodeUtil.isLegalMigrateNodes(newNodes1);
        assertEquals(true, isLegal);
    }
}

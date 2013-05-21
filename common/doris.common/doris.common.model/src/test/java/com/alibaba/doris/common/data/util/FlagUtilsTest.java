package com.alibaba.doris.common.data.util;

import junit.framework.TestCase;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class FlagUtilsTest extends TestCase {

    public void testCompress() {
        short flag = 0;
        flag = FlagUtils.setCompressed(flag);
        assertFalse(0 == flag);
        assertTrue(FlagUtils.isCompressed(flag));
    }
}

package com.alibaba.doris.common.data;

import java.util.Date;

import junit.framework.TestCase;

import com.alibaba.doris.common.data.impl.ValueImpl;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ValueTest extends TestCase {

    public void testCompareVersion() throws InterruptedException {
        Value v1 = new ValueImpl("v".getBytes(), (new Date()).getTime());
        Thread.sleep(100);
        Value v2 = new ValueImpl("v".getBytes(), (new Date()).getTime());
        assertTrue(v2.compareVersion(v1) == CompareStatus.AFTER);
        assertTrue(v1.compareVersion(v2) == CompareStatus.BEFORE);
    }

    public void testEquals() throws InterruptedException {
        Value v1 = new ValueImpl("v".getBytes(), (new Date()).getTime());
        Thread.sleep(100);
        Value v2 = new ValueImpl("v".getBytes(), (new Date()).getTime());
        assertFalse(v1.equals(v2));
        assertFalse(v1.equals(null));
        assertTrue(v1.equals(v1));

        Value vv1 = new ValueImpl("v俊1".getBytes(), 11111);
        Value vv2 = new ValueImpl("v俊1".getBytes(), 11111);
        assertTrue(vv1.equals(vv2));

        Value vvv1 = new ValueImpl("v".getBytes(), 11111);
        vvv1.setFlag((short) 2);
        Value vvv2 = new ValueImpl("v".getBytes(), 11111);
        assertFalse(vvv1.equals(vvv2));

        Value vvvv1 = new ValueImpl("v".getBytes(), 11111);
        Value vvvv2 = new ValueImpl("v".getBytes(), 22222);
        assertFalse(vvvv1.equals(vvvv2));
    }
}

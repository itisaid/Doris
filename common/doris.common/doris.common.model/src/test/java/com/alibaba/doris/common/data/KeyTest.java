package com.alibaba.doris.common.data;

import junit.framework.TestCase;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class KeyTest extends TestCase {

    public void testConstructKeyImplByBytes() {
        byte[] keyBytes = new byte[] { '1', '0', '0', ':', 'k', 'e', 'y', '1' };
        Key key = KeyFactory.createKey(keyBytes, 1);
        assertEquals("key1", key.getKey());
        assertEquals(100, key.getNamespace());
        assertEquals(1, key.getVNode());

        keyBytes = new byte[] { 'k', 'e', 'y', '1' };
        key = KeyFactory.createKey(keyBytes, 1);
        assertEquals("key1", key.getKey());
        assertEquals(0, key.getNamespace());
    }

    public void testConstructKeyImplByClient() {
        Key key = KeyFactory.createKey(100, "key1", 1, 10);
        assertEquals("100:key1", key.getPhysicalKey());
        assertEquals(100, key.getNamespace());
    }
}

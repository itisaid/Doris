package com.alibaba.doris.common.data;

import com.alibaba.doris.common.data.impl.KeyImpl;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class KeyFactory {

    public static Key createKey(byte[] physicalKeyBytes, int vnode) {
        return new KeyImpl(physicalKeyBytes, vnode);
    }

    public static Key createKey(int namespace, String key, long routeVersion, int vnode) {
        return new KeyImpl(namespace, key, routeVersion, vnode);
    }

    public static Key createKey(int namespace, String key) {
        return new KeyImpl(namespace, key);
    }

    public static Key createKey(int namespace, String key, int vnode) {
        return new KeyImpl(namespace, key, vnode);
    }
}

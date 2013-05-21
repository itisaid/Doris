package com.alibaba.doris.common.router.virtual;

import com.alibaba.doris.common.route.VirtualRouter;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class VirtualRouterImpl implements VirtualRouter {

    private static VirtualRouterImpl instance = new VirtualRouterImpl();

    private VirtualRouterImpl() {

    }

    public static VirtualRouter getInstance() {
        return instance;
    }

    public int findVirtualNode(String key) {
        int index = Math.abs(key.hashCode()) % virtualNode.length;
        return virtualNode[index];
    }

    private int[] virtualNode = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };

    public int getVirtualNum() {
        return virtualNode.length;
    }
}

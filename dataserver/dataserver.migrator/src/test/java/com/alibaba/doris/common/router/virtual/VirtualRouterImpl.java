package com.alibaba.doris.common.router.virtual;

import com.alibaba.doris.common.route.VirtualRouter;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class VirtualRouterImpl implements VirtualRouter {

    public static VirtualRouter getInstance() {
        return router;
    }

    private static final VirtualRouter router = new com.alibaba.doris.common.route.MockVirtualRouter();

    public int findVirtualNode(String key) {
        return router.findVirtualNode(key);
    }

    public int getVirtualNum() {
        return router.getVirtualNum();
    }
}

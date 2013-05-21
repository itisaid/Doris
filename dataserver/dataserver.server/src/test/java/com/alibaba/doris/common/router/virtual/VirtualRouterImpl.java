package com.alibaba.doris.common.router.virtual;

import com.alibaba.doris.algorithm.RouteAlgorithm;
import com.alibaba.doris.algorithm.vpm.VpmRouterAlgorithm;
import com.alibaba.doris.common.adminservice.AdminServiceFactory;
import com.alibaba.doris.common.route.VirtualRouter;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class VirtualRouterImpl implements VirtualRouter {

    private static VirtualRouterImpl instance   = new VirtualRouterImpl();

    protected int                    virtualNum = 100;                    // default.

    protected RouteAlgorithm         algorithm;

    public static VirtualRouter getInstance() {
        return instance;
    }

    public int findVirtualNode(String key) {
        checkInitInstance();
        if (isDebug) {
            int index = Math.abs(key.hashCode()) % virtualNode.length;
            return virtualNode[index];
        } else {
            return algorithm.getVirtualByKey(key);
        }
    }

    private int[] virtualNode = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };

    public int getVirtualNum() {
        checkInitInstance();
        if (isDebug) {
            return virtualNode.length;
        } else {
            return virtualNum;
        }
    }

    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

    private void checkInitInstance() {
        if (!isDebug && !isInitialized) {
            virtualNum = AdminServiceFactory.getVirtualNumberService().getVirtualNumber();
            algorithm = new VpmRouterAlgorithm(1, virtualNum);
            isInitialized = true;
        }
    }

    private static boolean isDebug       = false;
    private boolean        isInitialized = false;
}

package com.alibaba.doris.common.data.util;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class FlagUtils {

    public static boolean isCompressed(short flag) {
        return (FLAG_COMPRESS & flag) != 0;
    }

    public static short setCompressed(short flag) {
        flag |= FLAG_COMPRESS;
        return flag;
    }

    public static boolean isContainVnode(short flag) {
        return (FLAG_CONTAIN_VNODE & flag) != 0;
    }

    public static short setContainVnode(short flag) {
        flag |= FLAG_CONTAIN_VNODE;
        return flag;
    }

    private static final short FLAG_COMPRESS      = 0x0001;
    private static final short FLAG_CONTAIN_VNODE = 0x4001;
}

package com.alibaba.doris.dataserver.core;

import com.alibaba.doris.dataserver.action.data.ActionData;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface Response {

    void write(ActionData md);

    void flush();

    /**
     * 强制关闭当前连接。
     */
    void close();
}

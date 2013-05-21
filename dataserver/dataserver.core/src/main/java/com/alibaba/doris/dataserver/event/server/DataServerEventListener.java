package com.alibaba.doris.dataserver.event.server;

import com.alibaba.doris.dataserver.event.EventListener;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface DataServerEventListener extends EventListener {

    /**
     * DataServer启动时会通知该事件。
     */
    public void onStartup();

    /**
     * DataServer关闭时会通知shutdown
     */
    public void onShutdown();

}

package com.alibaba.doris.dataserver.tools;

import com.alibaba.doris.dataserver.ApplicationContext;
import com.alibaba.doris.dataserver.event.server.DataServerEventListener;
import com.alibaba.doris.dataserver.event.server.ShutdownEvent;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DataServerJVMShutdownHook extends Thread implements DataServerEventListener {

    public DataServerJVMShutdownHook(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public void run() {
        // 防止重复发送shutdown事件。
        if (!isShutdownExecuted) {
            appContext.getEventListenerManager().fireEvent(new ShutdownEvent());
        }
    }

    public void onShutdown() {
        isShutdownExecuted = true;
    }

    public void onStartup() {
        ;
    }

    private volatile boolean   isShutdownExecuted = false;
    private ApplicationContext appContext;
}

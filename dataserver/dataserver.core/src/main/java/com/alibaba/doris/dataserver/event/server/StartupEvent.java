package com.alibaba.doris.dataserver.event.server;

import com.alibaba.doris.dataserver.event.EventListener;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class StartupEvent extends DataServerEvent {

    public void notify(EventListener eventListener) {
        DataServerEventListener listener = (DataServerEventListener) eventListener;
        listener.onStartup();
    }
}

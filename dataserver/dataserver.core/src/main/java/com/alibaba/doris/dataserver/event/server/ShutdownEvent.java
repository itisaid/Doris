package com.alibaba.doris.dataserver.event.server;

import com.alibaba.doris.dataserver.event.EventListener;

/**
 * DataServer关闭事件，当DataServer关闭时，系统会触发一个关闭事件。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ShutdownEvent extends DataServerEvent {

    public void notify(EventListener eventListener) {
        DataServerEventListener listener = (DataServerEventListener) eventListener;
        listener.onShutdown();
    }

}

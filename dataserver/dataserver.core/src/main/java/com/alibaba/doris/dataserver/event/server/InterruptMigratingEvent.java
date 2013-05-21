package com.alibaba.doris.dataserver.event.server;

import com.alibaba.doris.dataserver.event.EventListener;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class InterruptMigratingEvent extends DataServerEvent {

    public void notify(EventListener eventListener) {
        MigrateEventListener listener = (MigrateEventListener) eventListener;
        listener.onInterruptMigrating();
    }

}

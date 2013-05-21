package com.alibaba.doris.dataserver.event.server;

import com.alibaba.doris.dataserver.event.EventType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public abstract class MigrateEvent extends DataServerEvent {

    @Override
    public EventType getType() {
        return EventType.MIGRATE;
    }

}

package com.alibaba.doris.dataserver.event.server;

import com.alibaba.doris.dataserver.event.BaseEvent;
import com.alibaba.doris.dataserver.event.EventType;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public abstract class DataServerEvent extends BaseEvent {

    public EventType getType() {
        return EventType.DATA_SERVER_STATE;
    }
}

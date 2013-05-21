package com.alibaba.doris.dataserver.event.config;

import com.alibaba.doris.dataserver.event.BaseEvent;
import com.alibaba.doris.dataserver.event.EventListener;
import com.alibaba.doris.dataserver.event.EventType;

/**
 * 配置变更相关的事件。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public abstract class ConfigEvent extends BaseEvent {

    public EventType getType() {
        return EventType.CONFIG_CHANGED;
    }

    public void notify(EventListener eventListener) {
        ConfigEventListener configEventListener = (ConfigEventListener) eventListener;
        configEventListener.onRoutingConfigureChanged(this);
    }

    public void getConfig() {

    }

}

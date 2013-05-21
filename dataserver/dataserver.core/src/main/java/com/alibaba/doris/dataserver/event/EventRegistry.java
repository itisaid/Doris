package com.alibaba.doris.dataserver.event;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.alibaba.doris.dataserver.event.config.ConfigEventListener;
import com.alibaba.doris.dataserver.event.server.DataServerEventListener;
import com.alibaba.doris.dataserver.event.server.MigrateEventListener;

/**
 * 事件注册中心，为每种类型的事件都维护一个监听器队列，<br>
 * 在事件分发的过程中，不同类型的事件会转发给不同的事件监听队列。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class EventRegistry {

    @SuppressWarnings("unchecked")
    public EventRegistry() {
        EventType[] typeArray = EventType.values();
        listenerQueue = new List[typeArray.length];
        for (EventType eventType : typeArray) {
            listenerQueue[eventType.ordinal()] = new CopyOnWriteArrayList<EventListener>();
        }
    }

    public List<EventListener> getEventListenerRegistry(EventType eventType) {
        return listenerQueue[eventType.ordinal()];
    }

    public void registEventListener(EventListener listener) {
        EventType eventType = null;
        if (listener instanceof DataServerEventListener) {
            eventType = EventType.DATA_SERVER_STATE;
            regist(eventType, listener);
        }

        if (listener instanceof ConfigEventListener) {
            eventType = EventType.CONFIG_CHANGED;
            regist(eventType, listener);
        }

        if (listener instanceof MigrateEventListener) {
            eventType = EventType.MIGRATE;
            regist(eventType, listener);
        }

        if (eventType == null) {
            throw new EventListnerException("You have registed an unknown EventListener.", null, listener);
        }
    }

    public void unregistEventListener(EventListener listener) {
        EventType eventType = null;
        if (listener instanceof DataServerEventListener) {
            eventType = EventType.DATA_SERVER_STATE;
            unregist(eventType, listener);
        }

        if (listener instanceof MigrateEventListener) {
            eventType = EventType.MIGRATE;
            unregist(eventType, listener);
        }

        if (listener instanceof ConfigEventListener) {
            eventType = EventType.CONFIG_CHANGED;
            unregist(eventType, listener);
        }

        if (eventType == null) {
            throw new EventListnerException("You have registed an unknown EventListener.", null, listener);
        }
    }

    private void regist(EventType eventType, EventListener listener) {
        listenerQueue[eventType.ordinal()].add(listener);
    }

    private void unregist(EventType eventType, EventListener listener) {
        List<EventListener> listenerList = listenerQueue[eventType.ordinal()];
        Iterator<EventListener> itr = listenerList.iterator();
        while (itr.hasNext()) {
            if (listener == itr.next()) {
                itr.remove();
                break;
            }
        }
    }

    private List<EventListener>[] listenerQueue;
}

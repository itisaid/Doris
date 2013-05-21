package com.alibaba.doris.dataserver.event;

import java.util.List;

/**
 * 事件及监听器管理中心，事件的产生和监听都通过本管理中心统一维护并管理。<br>
 * <br>
 * 注册感兴趣的监听事件：<br>
 * <code>
 *  MyDataServerEventListener myListener = new MyDataServerEventListener();
 *  EventListenerManager.registEventListener(myListener);
 * </code> <br>
 * <br>
 * 触发一个事件：<br>
 * <code>
 *  EventListenerManager.fireEvent(new StartupEvent());
 * </code> <br>
 * <br>
 * 取消注册过的监听事件:<br>
 * <code>
 * EventListenerManager.unregistEventListener(myListener);
 * </code><br>
 * <br>
 * 详细使用方法，可以参考本类对应的单元测试代码。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class EventListenerManager {

    /**
     * 往事件管理中心注册一个监听器，注册中心会自动根据监听器的类型，<br>
     * 注册到不同的事件监听通道。
     * 
     * @param eventListener
     */
    public void registEventListener(EventListener eventListener) {
        registry.registEventListener(eventListener);
    }

    /**
     * 将当前监听器从事件注册管理中心删除掉，一旦删除以后，事件将不会通知到<br>
     * 被删除的事件监听对象。
     * 
     * @param eventListener
     */
    public void unregistEventListener(EventListener eventListener) {
        registry.unregistEventListener(eventListener);
    }

    /**
     * 触发一个事件。<br>
     * 当某个关键性的事件发生时，可以通过调用本接口将事件通知到所有关心本事件的监听者。
     * 
     * @param event
     */
    public void fireEvent(Event event) {
        List<EventListener> eventListenerList = registry.getEventListenerRegistry(event.getType());
        if (eventListenerList != null) {
            for (EventListener listener : eventListenerList) {
                event.notify(listener);
            }
        } else {
            throw new EventListnerException("Unknown event! (Please check you have registed this event.)", event, null);
        }
    }

    private EventRegistry registry = new EventRegistry();
}

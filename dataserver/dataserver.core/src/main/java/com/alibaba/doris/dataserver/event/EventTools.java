package com.alibaba.doris.dataserver.event;

/**
 * 全局性的事件管理器，
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
@Deprecated
public class EventTools {

    /**
     * 触发一个事件。<br>
     * 当某个关键性的事件发生时，可以通过调用本接口将事件通知到所有关心本事件的监听者。
     * 
     * @param event
     */
    public static void fireEvent(Event event) {
        eventManager.fireEvent(event);
    }

    /**
     * 往事件管理中心注册一个监听器，注册中心会自动根据监听器的类型，<br>
     * 注册到不同的事件监听通道。
     * 
     * @param eventListener
     */
    public static void registEventListener(EventListener eventListener) {
        eventManager.registEventListener(eventListener);
    }

    /**
     * 将当前监听器从事件注册管理中心删除掉，一旦删除以后，事件将不会通知到<br>
     * 被删除的事件监听对象。
     * 
     * @param eventListener
     */
    public static void unregistEventListener(EventListener eventListener) {
        eventManager.unregistEventListener(eventListener);
    }

    private static EventListenerManager eventManager = new EventListenerManager();
}

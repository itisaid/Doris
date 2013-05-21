package com.alibaba.doris.dataserver.event;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface Event {

    /**
     * 获取当前事件的类型；
     * 
     * @return
     */
    EventType getType();

    /**
     * 触发事件时，系统通知监听对象。
     * 
     * @param eventListener
     */
    void notify(EventListener eventListener);
}

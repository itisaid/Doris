package com.alibaba.doris.dataserver.core;

import java.util.Iterator;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.dataserver.ApplicationContext;
import com.alibaba.doris.dataserver.action.data.ActionData;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface Request {

    /**
     * 获取当前请求输入数据
     * 
     * @return
     */
    ActionData getActionData();

    /**
     * 获取Request中的Key对象，如果当前命令存在Key数据，<br>
     * 系统返回一个Key对象实例，否则返回null。
     * 
     * @return
     */
    Key getKey();

    /**
     * 获取Request中的Value对象，如果当前命令存在value数据，<br>
     * 系统返回一个value对象实例，否则返回null。
     * 
     * @return
     */
    Value getValue();

    /**
     * 获取请求端（client）的IP地址
     * 
     * @return
     */
    String getClientAddress();

    /**
     * 获取client端的端口号。
     * 
     * @return
     */
    int getClientPort();

    /**
     * 获取当前处理请求所在DataServer实例的监听端口号。
     * 
     * @return
     */
    int getServerPort();

    /**
     * 获取DataServer所在服务器的IP地址
     * 
     * @return
     */
    String getServerAddress();

    /**
     * @param name
     * @param item
     */
    void setAttribute(String name, Object item);

    /**
     * @param name
     * @return
     */
    Object getAttribute(String name);

    /**
     * 获取应用上下文对象。
     * 
     * @return
     */
    ApplicationContext getApplicationContext();

    /**
     * 获取Filter遍历对象
     * 
     * @return
     */
    Iterator<FilterEntry> getFilterEntryIterator();

    /**
     * @param filterEntryIterator
     */
    void setFilterEntryIteraor(Iterator<FilterEntry> filterEntryIterator);
}

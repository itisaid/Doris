package com.alibaba.doris.dataserver;

import java.util.Enumeration;

/**
 * 模块级上下文对象，对属性的修改，不保证线程安全
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface ModuleContext {

    public Object getAttribute(String name);

    public void setAttribute(String name, Object value);

    public Enumeration<String> getAttributeNames();

    public void removeAttribute(String name);

    public ApplicationContext getApplicationContext();
}

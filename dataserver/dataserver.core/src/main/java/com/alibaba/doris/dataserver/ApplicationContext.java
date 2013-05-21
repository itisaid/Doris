package com.alibaba.doris.dataserver;

import java.util.Enumeration;
import java.util.List;

import com.alibaba.doris.dataserver.config.DataServerConfigure;
import com.alibaba.doris.dataserver.event.EventListenerManager;

/**
 * 应用顶级上下文对象，对属性的修改，不保证线程安全。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface ApplicationContext {

    public Module getModule(Class<?> moduleClass);

    public Module getModule(String moduleClassName);

    public Module getModuleByName(String moduleName);

    public ModuleContext getModuleContext(Module module);

    public Object getAttribute(String name);

    public Enumeration<String> getAttributeNames();

    public void setAttribute(String name, Object value);

    public void removeAttribute(String name);

    public EventListenerManager getEventListenerManager();

    public DataServerConfigure getDataServerConfigure();

    public List<Module> getModules();
}

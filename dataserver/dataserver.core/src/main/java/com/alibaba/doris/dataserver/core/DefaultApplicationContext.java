package com.alibaba.doris.dataserver.core;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.alibaba.doris.dataserver.ApplicationContext;
import com.alibaba.doris.dataserver.Module;
import com.alibaba.doris.dataserver.ModuleContext;
import com.alibaba.doris.dataserver.config.DataServerConfigure;
import com.alibaba.doris.dataserver.event.EventListenerManager;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DefaultApplicationContext implements ApplicationContext {

    public DefaultApplicationContext(List<Module> moduleList) {
        this.moduleList = moduleList;
    }

    public Module getModule(Class<?> moduleClass) {
        return getModule(moduleClass.getName());
    }

    public Module getModule(String moduleClassName) {
        for (Module module : moduleList) {
            if (module.getClass().getName().equals(moduleClassName)) {
                return module;
            }
        }
        return null;
    }

    public Module getModuleByName(String moduleName) {
        for (Module module : moduleList) {
            if (module.getName().equals(moduleName)) {
                return module;
            }
        }
        return null;
    }

    public ModuleContext getModuleContext(Module module) {
        if (null != module) {
            String moduleName = module.getName();
            if (StringUtils.isNotBlank(moduleName)) {
                return moduleContextMap.get(moduleName);
            } else {
                return moduleContextMap.get(module.getClass().getName());
            }
        }
        return null;
    }

    public void addModuleContext(Module module, ModuleContext moduleContext) {
        String moduleName = module.getName();
        if (StringUtils.isNotBlank(moduleName)) {
            moduleContextMap.put(module.getName(), moduleContext);
        } else {
            moduleContextMap.put(module.getClass().getName(), moduleContext);
        }
    }

    public Object getAttribute(String name) {
        return context.get(name);
    }

    public Enumeration<String> getAttributeNames() {
        Set<String> keySet = context.keySet();
        final Iterator<String> itr = keySet.iterator();
        return new Enumeration<String>() {

            public boolean hasMoreElements() {
                return itr.hasNext();
            }

            public String nextElement() {
                return itr.next();
            }
        };
    }

    public EventListenerManager getEventListenerManager() {
        return (EventListenerManager) getAttribute("EVENT_LISTENER_MANAGER");
    }

    public void setEventListenerManager(EventListenerManager eventListenerManager) {
        setAttribute("EVENT_LISTENER_MANAGER", eventListenerManager);
    }

    public void removeAttribute(String name) {
        context.remove(name);
    }

    public void setAttribute(String name, Object value) {
        context.put(name, value);
    }

    public DataServerConfigure getDataServerConfigure() {
        return (DataServerConfigure) getAttribute("DATA_SERVER_CONFIGURE");
    }

    public void setDataServerConfigure(DataServerConfigure configure) {
        setAttribute("DATA_SERVER_CONFIGURE", configure);
    }

    public List<Module> getModules() {
        return moduleList;
    }

    private Map<String, Object>        context          = new HashMap<String, Object>();
    private Map<String, ModuleContext> moduleContextMap = new HashMap<String, ModuleContext>();
    private List<Module>               moduleList;
}

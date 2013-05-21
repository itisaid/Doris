package com.alibaba.doris.dataserver.core;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.alibaba.doris.dataserver.ApplicationContext;
import com.alibaba.doris.dataserver.ModuleContext;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DefaultModuleContext implements ModuleContext {

    public DefaultModuleContext(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    public ApplicationContext getApplicationContext() {
        return appContext;
    }

    public Object getAttribute(String name) {
        Object obj = context.get(name);
        if (null == obj) {
            // 当前容器找不到，则继续向application context容器去查找属性是否存在。
            return appContext.getAttribute(name);
        }

        return obj;
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

    public void removeAttribute(String name) {
        context.remove(name);
    }

    public void setAttribute(String name, Object value) {
        context.put(name, value);
    }

    private ApplicationContext  appContext;
    private Map<String, Object> context = new HashMap<String, Object>();
}

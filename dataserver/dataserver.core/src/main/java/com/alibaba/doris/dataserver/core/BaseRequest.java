package com.alibaba.doris.dataserver.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.alibaba.doris.dataserver.ApplicationContext;
import com.alibaba.doris.dataserver.action.data.ActionData;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public abstract class BaseRequest implements Request {

    public BaseRequest(ApplicationContext appContext, ActionData ad) {
        this.actionData = ad;
        this.appContext = appContext;
    }

    public ActionData getActionData() {
        return actionData;
    }

    public Object getAttribute(String name) {
        return getContext().get(name);
    }

    public void setAttribute(String name, Object item) {
        getContext().put(name, item);
    }

    public int getClientPort() {
        return -1;
    }

    private Map<String, Object> getContext() {
        if (null == context) {
            context = new HashMap<String, Object>();
        }

        return context;
    }

    public int getServerPort() {
        return -1;
    }

    public ApplicationContext getApplicationContext() {
        return this.appContext;
    }

    public Iterator<FilterEntry> getFilterEntryIterator() {
        return filterEntryIterator;
    }

    public void setFilterEntryIteraor(Iterator<FilterEntry> filterEntryIterator) {
        this.filterEntryIterator = filterEntryIterator;
    }

    private Iterator<FilterEntry> filterEntryIterator;
    private Map<String, Object>   context;
    private ActionData            actionData;
    private ApplicationContext    appContext;
}

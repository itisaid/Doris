package com.alibaba.doris.dataserver.core;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class FilterEntry {

    public FilterEntry(RequestFilter filter) {
        this(EntryPosition.NORMAL, filter);
    }

    public FilterEntry(EntryPosition position, RequestFilter filter) {
        this.name = filter.getClass().getName();
        this.filter = filter;
        this.position = position;
    }

    public String getName() {
        return this.name;
    }

    public EntryPosition getPosition() {
        return position;
    }

    public void setPosition(EntryPosition position) {
        this.position = position;
    }

    public RequestFilter getRequestFilter() {
        return filter;
    }

    protected enum EntryPosition {
        FIRST, LAST, NORMAL
    }

    protected RequestFilter filter;
    protected String        name;
    protected EntryPosition position;
}

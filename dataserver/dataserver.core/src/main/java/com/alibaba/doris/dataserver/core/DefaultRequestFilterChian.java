package com.alibaba.doris.dataserver.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * RequestFilterChian的一种非线程安全的实现，<br>
 * RequestFilterChian必须在系统初始化过程中以线程安全的方式载入。
 * 
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DefaultRequestFilterChian implements RequestFilterChian {

    public void doFilter(Request request, Response response) {
        Iterator<FilterEntry> itr = (Iterator<FilterEntry>) request.getFilterEntryIterator();
        if (null == itr) {
            itr = filterChain.iterator();
            request.setFilterEntryIteraor(itr);
        }

        if (itr.hasNext()) {
            RequestFilter filter = itr.next().filter;
            filter.doFilter(request, response, this);
        }
    }

    /**
     * 往FilterChain中增加一个Filter
     * 
     * @param filter
     */
    public void addFilter(RequestFilter filter) {
        filterChain.add(new FilterEntry(filter));
    }

    public void addLastFilter(RequestFilter filter) {
        FilterEntry last = filterChain.getLast();
        if (null == last || last.position != FilterEntry.EntryPosition.LAST) {
            filterChain.addLast(new FilterEntry(FilterEntry.EntryPosition.LAST, filter));
            return;
        }

        throw new RuntimeException("The last filter already exists! Class name:"
                                   + last.getRequestFilter().getClass().getName());
    }

    public void addFirstFilter(RequestFilter filter) {
        FilterEntry first = null;
        if (filterChain.size() > 0) {
            first = filterChain.getFirst();
        }

        if (null == first || first.position != FilterEntry.EntryPosition.FIRST) {
            filterChain.addFirst(new FilterEntry(FilterEntry.EntryPosition.FIRST, filter));
            return;
        }

        throw new RuntimeException("The first filter already exists! Class name:"
                                   + first.getRequestFilter().getClass().getName());
    }

    public void addFilterAfter(RequestFilter currentFilter, RequestFilter filter) {
        ListIterator<FilterEntry> itr = filterChain.listIterator();
        while (itr.hasNext()) {
            FilterEntry entry = itr.next();
            Class<?> clazz = currentFilter.getClass();
            if (clazz.getName().equals(entry.name)) {
                if (entry.position == FilterEntry.EntryPosition.LAST) {
                    throw new RuntimeException("The last filter already exists! Class name:"
                                               + entry.getRequestFilter().getClass().getName());
                }
                itr.add(new FilterEntry(filter));
                return;
            }
        }

        throw new RuntimeException("Couldn't not find the filter in filter chain! ");
    }

    public RequestFilter getRequestFilter(String requestFilterName) {
        Iterator<FilterEntry> itr = filterChain.iterator();
        while (itr.hasNext()) {
            FilterEntry entry = itr.next();
            if (entry.name.equalsIgnoreCase(requestFilterName)) {
                return entry.filter;
            }
        }

        return null;
    }

    private LinkedList<FilterEntry> filterChain = new LinkedList<FilterEntry>();
}

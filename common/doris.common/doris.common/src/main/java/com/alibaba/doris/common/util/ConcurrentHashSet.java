package com.alibaba.doris.common.util;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ConcurrentHashSet<E> extends AbstractSet<E> implements Serializable {

    public ConcurrentHashSet() {
        this.map = new ConcurrentHashMap<E, Boolean>();
    }

    public ConcurrentHashSet(Collection<E> c) {
        this.map = new ConcurrentHashMap<E, Boolean>();
        addAll(c);
    }

    @Override
    public boolean add(E o) {
        return ((ConcurrentMap<E, Boolean>) map).putIfAbsent(o, Boolean.TRUE) == null;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public boolean remove(Object o) {
        return map.remove(o) != null;
    }

    @Override
    public void clear() {
        map.clear();
    }

    private static final long     serialVersionUID = 7066728132287524989L;
    private final Map<E, Boolean> map;
}

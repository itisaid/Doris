package com.alibaba.doris.dataserver.store;

import java.util.Iterator;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface ClosableIterator<E> extends Iterator<E> {

    public void close();
}

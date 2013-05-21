package com.alibaba.doris.dataserver.store;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public abstract class BaseStorage implements Storage {

    public Map<Key, Value> getAll(Iterable<Key> keyIterator) {
        int size = ((Collection<?>) keyIterator).size();
        Map<Key, Value> result = new HashMap<Key, Value>(size);
        for (Key key : keyIterator) {
            Value value = get(key);
            if (value != null) {
                result.put(key, value);
            }
        }
        return result;
    }

    protected static class MultiDataBaseIterator<T extends Storage> implements ClosableIterator<Pair> {

        public MultiDataBaseIterator(Iterator<T> dbItr, List<Integer> vnodeList) {
            this.dbItr = dbItr;
            this.vnodeList = vnodeList;
        }

        public boolean hasNext() {
            Iterator<Pair> localIteraotrPair = getNextIterator();
            if (null == localIteraotrPair) {
                return false;
            }

            return localIteraotrPair.hasNext();
        }

        private Iterator<Pair> getNextIterator() {
            if (null == innerPairIter) {
                if (dbItr.hasNext()) {
                    Storage db = dbItr.next();
                    innerPairIter = (ClosableIterator<Pair>) db.iterator(vnodeList);
                    return innerPairIter;
                } else {
                    return null;
                }
            } else {
                boolean hasNext = innerPairIter.hasNext();
                if (!hasNext) {
                    innerPairIter.close();
                    if (dbItr.hasNext()) {
                        Storage db = dbItr.next();
                        innerPairIter = (ClosableIterator<Pair>) db.iterator(vnodeList);
                    } else {
                        innerPairIter = null;
                    }
                }
                return innerPairIter;
            }
        }

        public Pair next() {
            if (innerPairIter == null) {
                throw new NoSuchElementException();
            }
            return innerPairIter.next();
        }

        public void remove() {
            if (innerPairIter == null) {
                throw new NoSuchElementException();
            }
            innerPairIter.remove();
        }

        public void close() {
            if (null != innerPairIter) {
                innerPairIter.close();
            }
        }

        private ClosableIterator<Pair> innerPairIter;
        private Iterator<T>            dbItr;
        private List<Integer>          vnodeList;
    }
}

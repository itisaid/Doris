package com.g414.haildb.tpl;

import java.io.Closeable;
import java.util.Iterator;
import java.util.Map;

import com.g414.haildb.Cursor;
import com.g414.haildb.Cursor.CursorDirection;
import com.g414.haildb.Cursor.LockMode;
import com.g414.haildb.Cursor.SearchMode;
import com.g414.haildb.TableDef;
import com.g414.haildb.Transaction;
import com.g414.haildb.Tuple;

public class Functional {
    public interface Traversal<T> extends Closeable, Iterator<T> {
        public void traverseAll();

        public void close();
    }

    public enum TraversalMode {
        READ_ONLY, READ_WRITE;
    }

    public enum MutationType {
        NONE, INSERT_OR_UPDATE, DELETE;
    }

    public static class Target {
        private final TableDef tableDef;
        private final String indexDef;

        public Target(TableDef tableDef) {
            this(tableDef, null);
        }

        public Target(TableDef tableDef, String indexDef) {
            this.tableDef = tableDef;
            this.indexDef = indexDef;
        }

        public TableDef getTableDef() {
            return tableDef;
        }

        public String getIndexDef() {
            return indexDef;
        }
    }

    public static class Mutation {
        private final MutationType type;
        private final Map<String, Object> instance;

        public Mutation(MutationType type, Map<String, Object> instance) {
            this.type = type;
            this.instance = instance;
        }

        public MutationType getType() {
            return type;
        }

        public Map<String, Object> getInstance() {
            return instance;
        }
    }

    public interface Mapping<T> {
        public T map(Map<String, Object> row);
    }

    public interface Reduction<T> {
        public T reduce(Map<String, Object> row, T initial);
    }

    public interface Filter extends Mapping<Boolean> {
    }

    public static class TraversalSpec {
        private final Target target;
        private final CursorDirection cursorDirection;
        private final SearchMode searchMode;
        private final Map<String, Object> firstKey;
        private final Filter primaryFilter;
        private final Filter filter;

        public TraversalSpec(Target target, Map<String, Object> firstKey,
                Filter primaryFilter, Filter filter) {
            this(target, CursorDirection.ASC, SearchMode.GE, firstKey,
                    primaryFilter, filter);
        }

        public TraversalSpec(Target target, CursorDirection cursorDirection,
                SearchMode searchMode, Map<String, Object> firstKey,
                Filter primaryFilter, Filter filter) {
            this.target = target;
            this.cursorDirection = cursorDirection;
            this.searchMode = searchMode;
            this.firstKey = firstKey;
            this.primaryFilter = primaryFilter;
            this.filter = filter;
        }

        public Target getTarget() {
            return target;
        }

        public CursorDirection getCursorDirection() {
            return cursorDirection;
        }

        public SearchMode getSearchMode() {
            return searchMode;
        }

        public Map<String, Object> getFirstKey() {
            return firstKey;
        }

        public Filter getPrimaryFilter() {
            return primaryFilter;
        }

        public Filter getFilter() {
            return filter;
        }
    }

    public static <T> void foreach(final Transaction txn,
            final TraversalSpec traversalSpec, final Mapping<T> r) {
        map(txn, traversalSpec, r).traverseAll();
    }

    public static <T> Traversal<T> map(final Transaction txn,
            final TraversalSpec traversalSpec, final Mapping<T> mapping) {
        return new TraversalImpl<T>(txn, TraversalMode.READ_ONLY,
                traversalSpec, mapping);
    }

    public static <T> T reduce(final Transaction txn,
            final TraversalSpec traversalSpec, final Reduction<T> reduction,
            final T initial) {

        MapReduction<T> mr = new MapReduction<T>(initial, reduction);
        Traversal<T> iter = map(txn, traversalSpec, mr);
        try {
            while (iter.hasNext()) {
                iter.next();
            }

            return mr.getAccum();

        } finally {
            iter.close();
        }
    }

    public static Traversal<Mutation> apply(final Transaction txn,
            final DatabaseTemplate dbt, final TraversalSpec traversalSpec,
            final Mapping<Mutation> mutation) {

        final Mapping<Mutation> mapping = new Mapping<Functional.Mutation>() {
            public Mutation map(Map<String, Object> row) {
                Mutation m = mutation.map(row);
                Target target = traversalSpec.getTarget();

                switch (m.getType()) {
                case NONE:
                    break;
                case INSERT_OR_UPDATE:
                    dbt.insertOrUpdate(txn, target.getTableDef(),
                            m.getInstance());
                    break;
                case DELETE:
                    dbt.delete(txn, target.getTableDef(), m.getInstance());
                    break;
                default:
                    throw new IllegalArgumentException();
                }

                return m;
            }
        };

        return new TraversalImpl<Mutation>(txn, TraversalMode.READ_WRITE,
                traversalSpec, mapping);
    }

    private static class MapReduction<T> implements Mapping<T> {
        T accum;
        Reduction<T> r;

        public MapReduction(T initial, Reduction<T> r) {
            accum = initial;
            this.r = r;
        }

        public T map(Map<String, Object> row) {
            accum = r.reduce(row, accum);

            return accum;
        }

        public T getAccum() {
            return accum;
        }
    }

    private static class TraversalImpl<T> implements Traversal<T> {
        private final boolean isSecondary;
        private final boolean isReadOnly;
        private final TableDef tableDef;
        private final Filter primaryFilter;
        private final Filter filter;
        private final Mapping<T> mapping;
        private final boolean isAscending;

        private Cursor c0;
        private Cursor c1;
        private Map<String, Object> nextItem;

        public TraversalImpl(Transaction txn, TraversalMode traversalMode,
                TraversalSpec traversalSpec, Mapping<T> mapping) {
            Target target = traversalSpec.getTarget();
            this.primaryFilter = traversalSpec.getPrimaryFilter();
            this.filter = traversalSpec.getFilter();
            this.mapping = mapping;

            this.isReadOnly = traversalMode.equals(TraversalMode.READ_ONLY);
            this.isSecondary = target.getIndexDef() != null;
            this.tableDef = target.getTableDef();
            this.isAscending = traversalSpec.getCursorDirection().equals(
                    CursorDirection.ASC);

            this.c0 = txn.openTable(tableDef);

            if (this.isSecondary) {
                this.c1 = c0.openIndex(target.getIndexDef());
                this.c1.setClusterAccess();
            } else {
                this.c1 = this.c0;
            }

            if (!this.isReadOnly) {
                try {
                    this.c1.setLockMode(LockMode.INTENTION_EXCLUSIVE);
                    this.c1.lock(LockMode.LOCK_EXCLUSIVE);
                } catch (Exception e) {
                    this.close();

                    throw new RuntimeException(e);
                }
            }

            Map<String, Object> firstKey = traversalSpec.getFirstKey();

            if (firstKey != null) {
                Tuple tuple = isSecondary ? c1
                        .createSecondaryIndexSearchTuple(firstKey) : c1
                        .createClusteredIndexSearchTuple(firstKey);

                c1.find(tuple, traversalSpec.getSearchMode());
            } else {
                if (this.isAscending) {
                    c1.first();
                } else {
                    c1.last();
                }
            }

            nextItem = advance();
        }

        private Map<String, Object> advance() {
            Map<String, Object> toReturn = null;

            while (c1 != null && c1.isPositioned() && c1.hasNext()) {
                Tuple read = c1.createClusteredIndexReadTuple();
                try {
                    c1.readRow(read);
                    Map<String, Object> row = read.valueMap();

                    if (!primaryFilter.map(row)) {
                        close();
                        break;
                    }

                    if (filter == null || filter.map(row)) {
                        toReturn = row;
                        break;
                    }
                } catch (Exception e) {
                    close();

                    throw new RuntimeException(e);
                } finally {
                    read.delete();
                    if (c1 != null) {
                        if (this.isAscending) {
                            c1.next();
                        } else {
                            c1.prev();
                        }
                    }
                }
            }

            if (toReturn == null) {
                close();
            }

            return toReturn;
        }

        public boolean hasNext() {
            return nextItem != null;
        }

        public T next() {
            if (nextItem == null) {
                throw new IllegalStateException("next() called on empty iter");
            }

            Map<String, Object> orig = nextItem;

            nextItem = advance();

            try {
                return mapping.map(orig);
            } catch (Exception e) {
                close();

                throw new RuntimeException(e);
            }
        }

        public void close() {
            if (c1 != null) {
                c1.close();
                c1 = null;
            }

            if (isSecondary && c0 != null) {
                c0.close();
                c0 = null;
            }
        }

        public void traverseAll() {
            try {
                while (hasNext()) {
                    next();
                }
            } finally {
                close();
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

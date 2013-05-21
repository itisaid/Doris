package com.g414.haildb;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Map;

import com.g414.haildb.impl.jna.HailDB;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public class Cursor {
    public enum CursorDirection {
        ASC, DESC;
    }

    public enum SearchMode {
        /* see InnoDB.ib_srch_mode_t.IB_CUR_G */
        G(HailDB.ib_srch_mode_t.IB_CUR_G), GE(HailDB.ib_srch_mode_t.IB_CUR_GE), L(
                HailDB.ib_srch_mode_t.IB_CUR_L), LE(
                HailDB.ib_srch_mode_t.IB_CUR_LE);

        private final int code;

        private SearchMode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static SearchMode fromCode(int code) {
            return SearchMode.values()[code - 1];
        }
    }

    public enum MatchMode {
        /* see InnoDB.ib_match_mode_t */
        CLOSEST(HailDB.ib_match_mode_t.IB_CLOSEST_MATCH), EXACT(
                HailDB.ib_match_mode_t.IB_EXACT_MATCH), EXACT_PREFIX(
                HailDB.ib_match_mode_t.IB_EXACT_PREFIX);

        private final int code;

        private MatchMode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static MatchMode fromCode(int code) {
            return MatchMode.values()[code];
        }
    }

    public enum SearchResultCode {
        /* returns -1, 0 or 1 based on search result */
        BEFORE(0), EQUALS(1), AFTER(2);

        private final int code;

        private SearchResultCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code - 1;
        }

        public static SearchResultCode fromCode(int code) {
            if (code == 0) {
                return EQUALS;
            } else if (code > 0) {
                return AFTER;
            } else {
                return BEFORE;
            }
        }
    }

    public enum LockMode {
        /* see InnoDB.ib_lck_mode_t */
        INTENTION_SHARED(HailDB.ib_lck_mode_t.IB_LOCK_IS), INTENTION_EXCLUSIVE(
                HailDB.ib_lck_mode_t.IB_LOCK_IX), LOCK_SHARED(
                HailDB.ib_lck_mode_t.IB_LOCK_S), LOCK_EXCLUSIVE(
                HailDB.ib_lck_mode_t.IB_LOCK_X), NOT_USED(
                HailDB.ib_lck_mode_t.IB_LOCK_NOT_USED), NONE(
                HailDB.ib_lck_mode_t.IB_LOCK_NONE);

        private final int code;

        private LockMode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static LockMode fromCode(int code) {
            return LockMode.values()[code];
        }
    }

    private final PointerByReference crsr;
    private final TableDef table;
    private final IndexDef index;

    private volatile int err = HailDB.db_err.DB_SUCCESS;

    public Cursor(PointerByReference crsr, TableDef table, IndexDef index) {
        this.crsr = crsr;
        this.table = table;
        this.index = index;
    }

    public PointerByReference getCrsr() {
        return crsr;
    }

    public Tuple createClusteredIndexReadTuple() {
        // if (this.index != null) {
        // throw new IllegalArgumentException(
        // "Secondary index cursor may not create cluster read tuples");
        // }

        return new Tuple(HailDB.ib_clust_read_tuple_create(crsr.getValue()),
                table.getColDefs());
    }

    public Tuple createClusteredIndexSearchTuple(Map<String, Object> val) {
        if (this.index != null) {
            throw new IllegalArgumentException(
                    "Secondary index cursor may not create cluster search tuples");
        }

        Tuple searchTuple = new Tuple(HailDB.ib_clust_search_tuple_create(crsr
                .getValue()), table.getColDefs());

        for (int i = 0; i < table.getPrimaryIndex().getColumns().size(); i++) {
            ColumnDef colDef = table.getPrimaryIndex().getColumns().get(i);
            Object value = val.get(colDef.getName());
            setValue(searchTuple, colDef, i, value, true, true);
        }

        return searchTuple;
    }

    public Tuple createSecondaryIndexReadTuple() {
        if (this.index == null) {
            throw new IllegalArgumentException(
                    "Clustered index cursor may not create secondary index read tuples");
        }

        return new Tuple(HailDB.ib_sec_read_tuple_create(crsr.getValue()),
                index.getColumns());
    }

    public Tuple createSecondaryIndexSearchTuple(Map<String, Object> val) {
        if (this.index == null) {
            throw new IllegalArgumentException(
                    "Clustered index cursor may not create secondary index search tuples");
        }

        Tuple searchTuple = new Tuple(HailDB.ib_sec_search_tuple_create(crsr
                .getValue()), index.getColumns());

        for (int i = 0; i < index.getColumns().size(); i++) {
            ColumnDef colDef = index.getColumns().get(i);
            Object value = val.get(colDef.getName());

            setValue(searchTuple, colDef, i, value, true, true);
        }

        return searchTuple;
    }
    
    //open a innodb secondary index cursor and return a cursor to handle it
    public Cursor openIndex(String indexName) {
        if (this.index != null) {
            throw new IllegalArgumentException(
                    "cannot open index from a secondary index cursor");
        }

        if (!this.table.getIndexDefs().containsKey(indexName)) {
            throw new IllegalArgumentException("unknown index: " + indexName);
        }

        PointerByReference indexCrsr = new PointerByReference();

        Util.assertSuccess(HailDB.ib_cursor_open_index_using_name(
                crsr.getValue(), indexName, indexCrsr));

        return new Cursor(indexCrsr, table, table.getIndexDefs().get(indexName));
    }

    public void setClusterAccess() {
        HailDB.ib_cursor_set_cluster_access(crsr.getValue());
    }

    public void setMatchMode(MatchMode matchMode) {
        HailDB.ib_cursor_set_match_mode(crsr.getValue(), matchMode.getCode());
    }

    public SearchResultCode find(Tuple tupl, SearchMode searchMode) {
        IntBuffer result = ByteBuffer.allocateDirect(4).asIntBuffer();
        err = HailDB.ib_cursor_moveto(crsr.getValue(), tupl.tupl,
                searchMode.getCode(), result);

        assertCursorState(err);

        return SearchResultCode.fromCode(result.get());
    }

    public void readRow(Tuple tupl) {
        if (!this.isPositioned()) {
            throw new IllegalStateException("no row at cursor!");
        }

        err = HailDB.ib_cursor_read_row(crsr.getValue(), tupl.tupl);
        assertCursorState(err);
    }

    public boolean hasNext() {
        return (err == HailDB.db_err.DB_SUCCESS);
    }

    public boolean isPositioned() {
        return (HailDB.ib_cursor_is_positioned(crsr.getValue()) == HailDB.IB_TRUE);
    }

    public void deleteRow() {
        err = HailDB.ib_cursor_delete_row(crsr.getValue());
        assertCursorState(err);
    }

    public void first() {
        err = HailDB.ib_cursor_first(crsr.getValue());
        assertCursorState(err);
    }

    public void last() {
        err = HailDB.ib_cursor_last(crsr.getValue());
        assertCursorState(err);
    }

    public void prev() {
        err = HailDB.ib_cursor_prev(crsr.getValue());
        assertCursorState(err);
    }

    public void next() {
        err = HailDB.ib_cursor_next(crsr.getValue());
        assertCursorState(err);
    }

    public void lock(LockMode mode) {
        Util.assertSuccess(HailDB.ib_cursor_lock(crsr.getValue(),
                mode.getCode()));
    }

    public void setLockMode(LockMode mode) {
        Util.assertSuccess(HailDB.ib_cursor_set_lock_mode(crsr.getValue(),
                mode.getCode()));
    }

    public void insertRow(Tuple tupl, Map<String, Object> data) throws InnoException {
        if (data.size() != tupl.columns.size()) {
            throw new IllegalArgumentException("Must specify all column values");
        }

        try {
            for (int i = 0; i < table.getColDefs().size(); i++) {
                ColumnDef colDef = table.getColDefs().get(i);
                Object value = data.get(colDef.getName());

                setValue(tupl, colDef, i, value, false, true);
            }

            Util.assertSuccess(HailDB.ib_cursor_insert_row(crsr.getValue(),
                    tupl.tupl));
        } catch (InnoException exception) {
        	throw exception;
        } finally {
            tupl.clear();
        }
    }

    public void updateRow(Tuple oldTuple, Map<String, Object> data) {
        if (data.size() != oldTuple.columns.size()) {
            throw new IllegalArgumentException("Must specify all column values");
        }

        Tuple newTuple = this.createClusteredIndexReadTuple();

        try {
            Util.assertSuccess(HailDB.ib_tuple_copy(newTuple.tupl,
                    oldTuple.tupl));

            for (int i = 0; i < table.getColDefs().size(); i++) {
                ColumnDef colDef = table.getColDefs().get(i);
                Object value = data.get(colDef.getName());

                setValue(newTuple, colDef, i, value, false, true);
            }

            Util.assertSuccess(HailDB.ib_cursor_update_row(crsr.getValue(),
                    oldTuple.tupl, newTuple.tupl));
        } finally {
            oldTuple.clear();
            newTuple.delete();
        }
    }

    //不加synchronized这里多线程会deadlock
    synchronized private static void setValue(Tuple tupl, ColumnDef colDef, int i,
            Object val, boolean ignoreNull, boolean coerce) {
        if (val == null) {
            if (!ignoreNull
                    && colDef.getAttrs().contains(ColumnAttribute.NOT_NULL)) {
                throw new IllegalArgumentException(
                        "Cannot store null in non-null column: "
                                + colDef.getName());
            } else {
                Util.assertSuccess(HailDB.ib_col_set_value(tupl.tupl, i,
                        Pointer.NULL, HailDB.IB_SQL_NULL));
            }
        } else {
            if (val instanceof String) {
                val = TupleStorage.coerceType((String) val, colDef.getType());
            }
        	
            switch (colDef.getType()) {
            case BINARY:
            case VARBINARY:
            case BLOB:
                TupleStorage.storeBytes(tupl, i, (byte[]) val);
                break;
            case CHAR:
            case CHAR_ANYCHARSET:
            case VARCHAR:
            case VARCHAR_ANYCHARSET:
                TupleStorage.storeString(tupl, i, (String) val);
                break;
            case INT:
                Number numVal = (Number) val;
                TupleStorage.storeInteger(tupl, colDef, i, numVal);
                break;
            case DOUBLE:
                Number dubVal = (Number) val;
                Util.assertSuccess(HailDB.ib_tuple_write_double(tupl.tupl, i,
                        dubVal.doubleValue()));
                break;
            case FLOAT:
                Number fltVal = (Number) val;
                Util.assertSuccess(HailDB.ib_tuple_write_float(tupl.tupl, i,
                        fltVal.floatValue()));
                break;
            default:
                throw new IllegalArgumentException("unsupported type : "
                        + colDef.getType());
            }
        }
    }

    public void reset() {
        Util.assertSuccess(HailDB.ib_cursor_reset(crsr.getValue()));
    }

    public void close() {
        Util.assertSuccess(HailDB.ib_cursor_close(crsr.getValue()));
    }

    private static void assertCursorState(int err) {
        if (err != HailDB.db_err.DB_SUCCESS
                && err != HailDB.db_err.DB_END_OF_INDEX
                && err != HailDB.db_err.DB_RECORD_NOT_FOUND) {
            throw new IllegalStateException("Cursor in invalid state (code "
                    + err + ")");
        }
    }
}

package com.g414.haildb.impl.jna;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import com.g414.haildb.TupleStorage;
import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

public class HailDB implements Library {
    public static final String JNA_LIBRARY_NAME = "haildb";
    public static final NativeLibrary JNA_NATIVE_LIB = NativeLibrary
            .getInstance(HailDB.JNA_LIBRARY_NAME);
    static {
        Native.register(HailDB.JNA_LIBRARY_NAME);
    }

    public static interface db_err {
        // / < A successult result
        public static final int DB_SUCCESS = 10;
        /**
         * The following are error codes<br>
         * < This is a generic error code. It<br>
         * is used to classify error conditions<br>
         * that can't be represented by other<br>
         * codes
         */
        public static final int DB_ERROR = 11;
        /**
         * < An operation was interrupted by<br>
         * a user.
         */
        public static final int DB_INTERRUPTED = 12;
        /**
         * < Operation caused an out of memory<br>
         * error. Within InnoDB core code this is<br>
         * normally a fatal error
         */
        public static final int DB_OUT_OF_MEMORY = 13;
        /**
         * < The operating system returned<br>
         * an out of file space error when trying<br>
         * to do an IO operation.
         */
        public static final int DB_OUT_OF_FILE_SPACE = 14;
        /**
         * < A lock request by transaction<br>
         * resulted in a lock wait. The thread<br>
         * is suspended internally by InnoDB and<br>
         * is put on a lock wait queue.
         */
        public static final int DB_LOCK_WAIT = 15;
        /**
         * < A lock request by a transaction<br>
         * resulted in a deadlock. The transaction<br>
         * was rolled back
         */
        public static final int DB_DEADLOCK = 16;
        // / < Not used
        public static final int DB_ROLLBACK = 17;
        /**
         * < A record insert or update violates<br>
         * a unique contraint.
         */
        public static final int DB_DUPLICATE_KEY = 18;
        /**
         * < A query thread should be in state<br>
         * suspended but is trying to acquire a<br>
         * lock. Currently this is treated as a<br>
         * hard error and a violation of an<br>
         * invariant.
         */
        public static final int DB_QUE_THR_SUSPENDED = 19;
        /**
         * < Required history data has been<br>
         * deleted due to lack of space in<br>
         * rollback segment
         */
        public static final int DB_MISSING_HISTORY = 20;
        // / < This error is not used
        public static final int DB_CLUSTER_NOT_FOUND = 30;
        // / < The table could not be found
        public static final int DB_TABLE_NOT_FOUND = 31;
        /**
         * < The database has to be stopped<br>
         * and restarted with more file space
         */
        public static final int DB_MUST_GET_MORE_FILE_SPACE = 32;
        /**
         * < The user is trying to create a<br>
         * table in the InnoDB data dictionary but<br>
         * a table with that name already exists
         */
        public static final int DB_TABLE_IS_BEING_USED = 33;
        /**
         * < A record in an index would not fit<br>
         * on a compressed page, or it would<br>
         * become bigger than 1/2 free space in<br>
         * an uncompressed page frame
         */
        public static final int DB_TOO_BIG_RECORD = 34;
        // / < Lock wait lasted too long
        public static final int DB_LOCK_WAIT_TIMEOUT = 35;
        /**
         * < Referenced key value not found<br>
         * for a foreign key in an insert or<br>
         * update of a row
         */
        public static final int DB_NO_REFERENCED_ROW = 36;
        /**
         * < Cannot delete or update a row<br>
         * because it contains a key value<br>
         * which is referenced
         */
        public static final int DB_ROW_IS_REFERENCED = 37;
        /**
         * < Adding a foreign key constraint<br>
         * to a table failed
         */
        public static final int DB_CANNOT_ADD_CONSTRAINT = 38;
        /**
         * < Data structure corruption<br>
         * noticed
         */
        public static final int DB_CORRUPTION = 39;
        /**
         * < InnoDB cannot handle an index<br>
         * where same column appears twice
         */
        public static final int DB_COL_APPEARS_TWICE_IN_INDEX = 40;
        /**
         * < Dropping a foreign key constraint<br>
         * from a table failed
         */
        public static final int DB_CANNOT_DROP_CONSTRAINT = 41;
        /**
         * < No savepoint exists with the given<br>
         * name
         */
        public static final int DB_NO_SAVEPOINT = 42;
        /**
         * < We cannot create a new single-table<br>
         * tablespace because a file of the same<br>
         * name already exists
         */
        public static final int DB_TABLESPACE_ALREADY_EXISTS = 43;
        /**
         * < Tablespace does not exist or is<br>
         * being dropped right now
         */
        public static final int DB_TABLESPACE_DELETED = 44;
        /**
         * < Lock structs have exhausted the<br>
         * buffer pool (for big transactions,<br>
         * InnoDB stores the lock structs in the<br>
         * buffer pool)
         */
        public static final int DB_LOCK_TABLE_FULL = 45;
        /**
         * < Foreign key constraints<br>
         * activated but the operation would<br>
         * lead to a duplicate key in some<br>
         * table
         */
        public static final int DB_FOREIGN_DUPLICATE_KEY = 46;
        /**
         * < When InnoDB runs out of the<br>
         * preconfigured undo slots, this can<br>
         * only happen when there are too many<br>
         * concurrent transactions
         */
        public static final int DB_TOO_MANY_CONCURRENT_TRXS = 47;
        /**
         * < When InnoDB sees any artefact or<br>
         * a feature that it can't recoginize or<br>
         * work with e.g., FT indexes created by<br>
         * a later version of the engine.
         */
        public static final int DB_UNSUPPORTED = 48;
        /**
         * < A column in the PRIMARY KEY<br>
         * was found to be NULL
         */
        public static final int DB_PRIMARY_KEY_IS_NULL = 49;
        /**
         * < The application should clean up<br>
         * and quite ASAP. Fatal error, InnoDB<br>
         * cannot continue operation without<br>
         * risking database corruption.
         */
        public static final int DB_FATAL = 50;
        /**
         * The following are partial failure codes<br>
         * < Partial failure code.
         */
        public static final int DB_FAIL = 1000;
        /**
         * < If an update or insert of a record<br>
         * doesn't fit in a Btree page
         */
        public static final int DB_OVERFLOW = 1001;
        /**
         * < If an update or delete of a<br>
         * record causes a Btree page to be below<br>
         * a minimum threshold
         */
        public static final int DB_UNDERFLOW = 1002;
        /**
         * < Failure to insert a secondary<br>
         * index entry to the insert buffer
         */
        public static final int DB_STRONG_FAIL = 1003;
        /**
         * < Failure trying to compress<br>
         * a page
         */
        public static final int DB_ZIP_OVERFLOW = 1004;
        // / < Record not found
        public static final int DB_RECORD_NOT_FOUND = 1500;
        /**
         * < A cursor operation or search<br>
         * operation scanned to the end of the<br>
         * index.
         */
        public static final int DB_END_OF_INDEX = 1501;
        /**
         * The following are API only error codes.<br>
         * < Generic schema error
         */
        public static final int DB_SCHEMA_ERROR = 2000;
        /**
         * < Column update or read failed<br>
         * because the types mismatch
         */
        public static final int DB_DATA_MISMATCH = 2001;
        /**
         * < If an API function expects the<br>
         * schema to be locked in exclusive mode<br>
         * and if it's not then that API function<br>
         * will return this error code
         */
        public static final int DB_SCHEMA_NOT_LOCKED = 2002;
        /**
         * < Generic error code for "Not found"<br>
         * type of errors
         */
        public static final int DB_NOT_FOUND = 2003;
        /**
         * < Generic error code for "Readonly"<br>
         * type of errors
         */
        public static final int DB_READONLY = 2004;
        /**
         * < Generic error code for "Invalid<br>
         * input" type of errors
         */
        public static final int DB_INVALID_INPUT = 2005;
    }

    public static interface ib_cfg_type_t {
        /**
         * < The configuration parameter is<br>
         * of type ibool
         */
        public static final int IB_CFG_IBOOL = 0;
        /**
         * < The configuration parameter is<br>
         * of type ulint
         */
        public static final int IB_CFG_ULINT = 1;
        /**
         * < The configuration parameter is<br>
         * of type ulong
         */
        public static final int IB_CFG_ULONG = 2;
        /**
         * < The configuration parameter is<br>
         * of type char*
         */
        public static final int IB_CFG_TEXT = 3;
        /**
         * < The configuration parameter is<br>
         * a callback parameter
         */
        public static final int IB_CFG_CB = 4;
    }

    public static interface ib_col_type_t {
        /**
         * < Character varying length. The<br>
         * column is not padded.
         */
        public static final int IB_VARCHAR = 1;
        /**
         * < Fixed length character string. The<br>
         * column is padded to the right.
         */
        public static final int IB_CHAR = 2;
        /**
         * < Fixed length binary, similar to<br>
         * IB_CHAR but the column is not padded<br>
         * to the right.
         */
        public static final int IB_BINARY = 3;
        // / < Variable length binary
        public static final int IB_VARBINARY = 4;
        /**
         * < Binary large object, or<br>
         * a TEXT type
         */
        public static final int IB_BLOB = 5;
        /**
         * < Integer: can be any size<br>
         * from 1 - 8 bytes. If the size is<br>
         * 1, 2, 4 and 8 bytes then you can use<br>
         * the typed read and write functions. For<br>
         * other sizes you will need to use the<br>
         * ib_col_get_value() function and do the<br>
         * conversion yourself.
         */
        public static final int IB_INT = 6;
        /**
         * < System column, this column can<br>
         * be one of DATA_TRX_ID, DATA_ROLL_PTR<br>
         * or DATA_ROW_ID.
         */
        public static final int IB_SYS = 8;
        // / < C (float) floating point value.
        public static final int IB_FLOAT = 9;
        // / > C (double) floating point value.
        public static final int IB_DOUBLE = 10;
        /**
         * < Decimal stored as an ASCII<br>
         * string
         */
        public static final int IB_DECIMAL = 11;
        // / < Any charset, varying length
        public static final int IB_VARCHAR_ANYCHARSET = 12;
        // / < Any charset, fixed length
        public static final int IB_CHAR_ANYCHARSET = 13;
    }

    public static interface ib_tbl_fmt_t {
        /**
         * < Redundant row format, the column<br>
         * type and length is stored in the row.
         */
        public static final int IB_TBL_REDUNDANT = 0;
        /**
         * < Compact row format, the column<br>
         * type is not stored in the row. The<br>
         * length is stored in the row but the<br>
         * storage format uses a compact format<br>
         * to store the length of the column data<br>
         * and record data storage format also<br>
         * uses less storage.
         */
        public static final int IB_TBL_COMPACT = 1;
        /**
         * < Compact row format. BLOB prefixes<br>
         * are not stored in the clustered index
         */
        public static final int IB_TBL_DYNAMIC = 2;
        /**
         * < Similar to dynamic format but<br>
         * with pages compressed
         */
        public static final int IB_TBL_COMPRESSED = 3;
    }

    public static interface ib_col_attr_t {
        // / < No special attributes.
        public static final int IB_COL_NONE = 0;
        // / < Column data can't be NULL.
        public static final int IB_COL_NOT_NULL = 1;
        // / < Column is IB_INT and unsigned.
        public static final int IB_COL_UNSIGNED = 2;
        // / < Future use, reserved.
        public static final int IB_COL_NOT_USED = 4;
        /**
         * < Custom precision type, this is<br>
         * a bit that is ignored by InnoDB and so<br>
         * can be set and queried by users.
         */
        public static final int IB_COL_CUSTOM1 = 8;
        /**
         * < Custom precision type, this is<br>
         * a bit that is ignored by InnoDB and so<br>
         * can be set and queried by users.
         */
        public static final int IB_COL_CUSTOM2 = 16;
        /**
         * < Custom precision type, this is<br>
         * a bit that is ignored by InnoDB and so<br>
         * can be set and queried by users.
         */
        public static final int IB_COL_CUSTOM3 = 32;
    }

    public static interface ib_lck_mode_t {
        /**
         * < Intention shared, an intention<br>
         * lock should be used to lock tables
         */
        public static final int IB_LOCK_IS = 0;
        /**
         * < Intention exclusive, an intention<br>
         * lock should be used to lock tables
         */
        public static final int IB_LOCK_IX = 1;
        /**
         * < Shared locks should be used to<br>
         * lock rows
         */
        public static final int IB_LOCK_S = 2;
        /**
         * < Exclusive locks should be used to<br>
         * lock rows
         */
        public static final int IB_LOCK_X = 3;
        // / < Future use, reserved
        public static final int IB_LOCK_NOT_USED = 4;
        /**
         * < This is used internally to note<br>
         * consistent read
         */
        public static final int IB_LOCK_NONE = 5;
        // / < number of lock modes
        public static final int IB_LOCK_NUM = HailDB.ib_lck_mode_t.IB_LOCK_NONE;
    }

    public static interface ib_srch_mode_t {
        /**
         * < If search key is not found then<br>
         * position the cursor on the row that<br>
         * is greater than the search key
         */
        public static final int IB_CUR_G = 1;
        /**
         * < If the search key not found then<br>
         * position the cursor on the row that<br>
         * is greater than or equal to the search<br>
         * key
         */
        public static final int IB_CUR_GE = 2;
        /**
         * < If search key is not found then<br>
         * position the cursor on the row that<br>
         * is less than the search key
         */
        public static final int IB_CUR_L = 3;
        /**
         * < If search key is not found then<br>
         * position the cursor on the row that<br>
         * is less than or equal to the search<br>
         * key
         */
        public static final int IB_CUR_LE = 4;
    }

    public static interface ib_match_mode_t {
        // / < Closest match possible
        public static final int IB_CLOSEST_MATCH = 0;
        /**
         * < Search using a complete key<br>
         * value
         */
        public static final int IB_EXACT_MATCH = 1;
        /**
         * < Search using a key prefix which<br>
         * must match to rows: the prefix may<br>
         * contain an incomplete field (the<br>
         * last field in prefix may be just<br>
         * a prefix of a fixed length column)
         */
        public static final int IB_EXACT_PREFIX = 2;
    }

    public static interface ib_trx_state_t {
        /**
         * < Has not started yet, the<br>
         * transaction has not ben started yet.
         */
        public static final int IB_TRX_NOT_STARTED = 0;
        /**
         * < The transaction is currently<br>
         * active and needs to be either<br>
         * committed or rolled back.
         */
        public static final int IB_TRX_ACTIVE = 1;
        // / < Not committed to disk yet
        public static final int IB_TRX_COMMITTED_IN_MEMORY = 2;
        // / < Support for 2PC/XA
        public static final int IB_TRX_PREPARED = 3;
    }

    public static interface ib_trx_level_t {
        /**
         * < Dirty read: non-locking SELECTs are<br>
         * performed so that we do not look at a<br>
         * possible earlier version of a record;<br>
         * thus they are not 'consistent' reads<br>
         * under this isolation level; otherwise<br>
         * like level 2
         */
        public static final int IB_TRX_READ_UNCOMMITTED = 0;
        /**
         * < Somewhat Oracle-like isolation,<br>
         * except that in range UPDATE and DELETE<br>
         * we must block phantom rows with<br>
         * next-key locks; SELECT ... FOR UPDATE<br>
         * and ... LOCK IN SHARE MODE only lock<br>
         * the index records, NOT the gaps before<br>
         * them, and thus allow free inserting;<br>
         * each consistent read reads its own<br>
         * snapshot
         */
        public static final int IB_TRX_READ_COMMITTED = 1;
        /**
         * < All consistent reads in the same<br>
         * trx read the same snapshot; full<br>
         * next-key locking used in locking reads<br>
         * to block insertions into gaps
         */
        public static final int IB_TRX_REPEATABLE_READ = 2;
        /**
         * < All plain SELECTs are converted to<br>
         * LOCK IN SHARE MODE reads
         */
        public static final int IB_TRX_SERIALIZABLE = 3;
    }

    public static interface ib_shutdown_t {
        /**
         * < Normal shutdown, do insert buffer<br>
         * merge and purge before complete<br>
         * shutdown.
         */
        public static final int IB_SHUTDOWN_NORMAL = 0;
        /**
         * < Do not do a purge and index buffer<br>
         * merge at shutdown.
         */
        public static final int IB_SHUTDOWN_NO_IBUFMERGE_PURGE = 1;
        /**
         * < Same as NO_IBUFMERGE_PURGE<br>
         * and in addition do not even flush the<br>
         * buffer pool to data files. No committed<br>
         * transactions are lost
         */
        public static final int IB_SHUTDOWN_NO_BUFPOOL_FLUSH = 2;
    }

    public static interface ib_schema_visitor_version_t {
        public static final int IB_SCHEMA_VISITOR_TABLE = 1;
        public static final int IB_SCHEMA_VISITOR_TABLE_COL = 2;
        public static final int IB_SCHEMA_VISITOR_TABLE_AND_INDEX = 3;
        public static final int IB_SCHEMA_VISITOR_TABLE_AND_INDEX_COL = 4;
    }

    public static final int IB_TRUE = 1;
    public static final int MAX_TEXT_LEN = 4096;
    public static final int IB_MAX_COL_NAME_LEN = (64 * 3);
    public static final int IB_SQL_NULL = -1;
    public static final int IB_MAX_TABLE_NAME_LEN = (64 * 3);
    public static final int IB_FALSE = 0;
    public static final int IB_N_SYS_COLS = 3;

    public static class ib_col_meta_t extends Structure {
        /**
         * @see ib_col_type_t<br>
         *      < Type of the column
         */
        public int type;
        /**
         * @see ib_col_attr_t<br>
         *      < Column attributes
         */
        public int attr;
        // / < Length of type
        public int type_len;
        /**
         * < 16 bits of data relevant only to<br>
         * the client. InnoDB doesn't care
         */
        public short client_type;
        // / < Column charset
        public PointerByReference charset;

        public ib_col_meta_t() {
            super();
        }

        public ib_col_meta_t(int type, int attr, int type_len,
                short client_type, PointerByReference charset) {
            super();
            this.type = type;
            this.attr = attr;
            this.type_len = type_len;
            this.client_type = client_type;
            this.charset = charset;
        }

        protected ByReference newByReference() {
            ByReference s = new ByReference();
            s.useMemory(getPointer());
            write();
            s.read();
            return s;
        }

        protected ByValue newByValue() {
            ByValue s = new ByValue();
            s.useMemory(getPointer());
            write();
            s.read();
            return s;
        }

        protected ib_col_meta_t newInstance() {
            ib_col_meta_t s = new ib_col_meta_t();
            s.useMemory(getPointer());
            write();
            s.read();
            return s;
        }

        public static class ByReference extends ib_col_meta_t implements
                Structure.ByReference {
        }

        public static class ByValue extends ib_col_meta_t implements
                Structure.ByValue {
        }
    }

    public static class ib_schema_visitor_t extends Structure {
        // / @see ib_schema_visitor_version_t
        public int version;
        // / < Visitor version
        public HailDB.ib_schema_visitor_table_t table;
        // / < For travesing table info
        public HailDB.ib_schema_visitor_table_col_t table_col;
        // / < For travesing table column info
        public HailDB.ib_schema_visitor_index_t index;
        // / < For travesing index info
        public HailDB.ib_schema_visitor_index_col_t index_col;

        public ib_schema_visitor_t() {
            super();
        }

        public ib_schema_visitor_t(int version,
                HailDB.ib_schema_visitor_table_t table,
                HailDB.ib_schema_visitor_table_col_t table_col,
                HailDB.ib_schema_visitor_index_t index,
                HailDB.ib_schema_visitor_index_col_t index_col) {
            super();
            this.version = version;
            this.table = table;
            this.table_col = table_col;
            this.index = index;
            this.index_col = index_col;
        }

        protected ByReference newByReference() {
            ByReference s = new ByReference();
            s.useMemory(getPointer());
            write();
            s.read();
            return s;
        }

        protected ByValue newByValue() {
            ByValue s = new ByValue();
            s.useMemory(getPointer());
            write();
            s.read();
            return s;
        }

        protected ib_schema_visitor_t newInstance() {
            ib_schema_visitor_t s = new ib_schema_visitor_t();
            s.useMemory(getPointer());
            write();
            s.read();
            return s;
        }

        public static class ByReference extends ib_schema_visitor_t implements
                Structure.ByReference {
        }

        public static class ByValue extends ib_schema_visitor_t implements
                Structure.ByValue {
        }
    }

    public interface ib_cb_t extends Callback {
        void invoke();
    }

    public interface ib_schema_visitor_table_all_t extends Callback {
        int invoke(Pointer arg, Pointer name, int name_len);
    }

    public interface ib_schema_visitor_table_t extends Callback {
        int invoke(Pointer arg, Pointer name, int tbl_fmt, int page_size,
                int n_cols, int n_indexes);
    }

    public interface ib_schema_visitor_table_col_t extends Callback {
        int invoke(Pointer arg, Pointer name, int col_type, int len, int attr);
    }

    public interface ib_schema_visitor_index_t extends Callback {
        int invoke(Pointer arg, Pointer name, int clustered, int unique,
                int n_cols);
    }

    public interface ib_schema_visitor_index_col_t extends Callback {
        int invoke(Pointer arg, Pointer name, int prefix_len);
    }

    public interface ib_client_cmp_t extends Callback {
        int invoke(HailDB.ib_col_meta_t col_meta, Pointer p1, int p1_len,
                Pointer p2, int p2_len);
    }

    public static native long ib_api_version();

    public static native int ib_init();

    public static native int ib_startup(String format);

    public static native int ib_shutdown(int flag);

    public static native int ib_trx_start(Pointer ib_trx, int ib_trx_level);

    public static native Pointer ib_trx_begin(int ib_trx_level);

    public static native int ib_trx_state(Pointer ib_trx);

    public static native int ib_trx_release(Pointer ib_trx);

    public static native int ib_trx_commit(Pointer ib_trx);

    public static native int ib_trx_rollback(Pointer ib_trx);

    public static native int ib_table_schema_add_col(Pointer ib_tbl_sch,
            String name, int ib_col_type, int ib_col_attr, short client_type,
            int len);

    public static native int ib_table_schema_add_index(Pointer ib_tbl_sch,
            String name, PointerByReference ib_idx_sch);

    public static native void ib_table_schema_delete(Pointer ib_tbl_sch);

    public static native int ib_table_schema_create(String name,
            PointerByReference ib_tbl_sch, int ib_tbl_fmt, int page_size);

    public static native int ib_index_schema_add_col(Pointer ib_idx_sch,
            String name, int prefix_len);

    public static native int ib_index_schema_create(Pointer ib_usr_trx,
            String name, String table_name, PointerByReference ib_idx_sch);

    public static native int ib_index_schema_set_clustered(Pointer ib_idx_sch);

    public static native void ib_cursor_set_simple_select(Pointer ib_crsr);

    public static native int ib_index_schema_set_unique(Pointer ib_idx_sch);

    public static native void ib_index_schema_delete(Pointer ib_idx_sch);

    public static native int ib_table_create(Pointer ib_trx,
            Pointer ib_tbl_sch, LongBuffer id);

    public static native int ib_table_rename(Pointer ib_trx, Pointer old_name,
            Pointer new_name);

    public static native int ib_table_rename(Pointer ib_trx, String old_name,
            String new_name);

    public static native int ib_index_create(Pointer ib_idx_sch,
            LongBuffer index_id);

    public static native int ib_table_drop(Pointer trx, String name);

    public static native int ib_index_drop(Pointer trx, long index_id);

    public static native int ib_cursor_open_table_using_id(long table_id,
            Pointer ib_trx, PointerByReference ib_crsr);

    public static native int ib_cursor_open_index_using_id(long index_id,
            Pointer ib_trx, PointerByReference ib_crsr);

    public static native int ib_cursor_open_index_using_name(
            Pointer ib_open_crsr, String index_name, PointerByReference ib_crsr);

    public static native int ib_cursor_open_table(String name, Pointer ib_trx,
            PointerByReference ib_crsr);

    public static native int ib_cursor_reset(Pointer ib_crsr);

    public static native int ib_cursor_close(Pointer ib_crsr);

    public static native int ib_cursor_insert_row(Pointer ib_crsr,
            Pointer ib_tpl);

    public static native int ib_cursor_update_row(Pointer ib_crsr,
            Pointer ib_old_tpl, Pointer ib_new_tpl);

    public static native int ib_cursor_delete_row(Pointer ib_crsr);

    public static native int ib_cursor_read_row(Pointer ib_crsr, Pointer ib_tpl);

    public static native int ib_cursor_prev(Pointer ib_crsr);

    public static native int ib_cursor_next(Pointer ib_crsr);

    public static native int ib_cursor_first(Pointer ib_crsr);

    public static native int ib_cursor_last(Pointer ib_crsr);

    public static native int ib_cursor_moveto(Pointer ib_crsr, Pointer ib_tpl,
            int ib_srch_mode, IntBuffer result);

    public static native void ib_cursor_attach_trx(Pointer ib_crsr,
            Pointer ib_trx);

    public static native void ib_set_client_compare(
            HailDB.ib_client_cmp_t client_cmp_func);

    public static native void ib_cursor_set_match_mode(Pointer ib_crsr,
            int match_mode);

    public static native int ib_col_set_value(Pointer ib_tpl, int col_no,
            Pointer src, int len);

    public static native int ib_col_get_len(Pointer ib_tpl, int i);

    public static native int ib_col_copy_value(Pointer ib_tpl, int i,
            Pointer dst, int len);

    public static native int ib_tuple_read_i8(Pointer ib_tpl, int i,
            ByteBuffer ival);

    public static native int ib_tuple_read_u8(Pointer ib_tpl, int i,
            ByteBuffer ival);

    public static native int ib_tuple_read_i16(Pointer ib_tpl, int i,
            ShortBuffer ival);

    public static native int ib_tuple_read_u16(Pointer ib_tpl, int i,
            ShortBuffer ival);

    public static native int ib_tuple_read_i32(Pointer ib_tpl, int i,
            IntBuffer ival);

    public static native int ib_tuple_read_u32(Pointer ib_tpl, int i,
            IntBuffer ival);

    public static native int ib_tuple_read_i64(Pointer ib_tpl, int i,
            LongBuffer ival);

    public static native int ib_tuple_read_u64(Pointer ib_tpl, int i,
            LongBuffer ival);

    public static native Pointer ib_col_get_value(Pointer ib_tpl, int i);

    public static native int ib_col_get_meta(Pointer ib_tpl, int i,
            HailDB.ib_col_meta_t ib_col_meta);

    public static native Pointer ib_tuple_clear(Pointer ib_tpl);

    public static native int ib_tuple_get_cluster_key(Pointer ib_crsr,
            PointerByReference ib_dst_tpl, Pointer ib_src_tpl);

    public static native int ib_tuple_copy(Pointer ib_dst_tpl,
            Pointer ib_src_tpl);

    public static native Pointer ib_sec_search_tuple_create(Pointer ib_crsr);

    public static native Pointer ib_sec_read_tuple_create(Pointer ib_crsr);

    public static native Pointer ib_clust_search_tuple_create(Pointer ib_crsr);

    public static native Pointer ib_clust_read_tuple_create(Pointer ib_crsr);

    public static native int ib_tuple_get_n_user_cols(Pointer ib_tpl);

    public static native int ib_tuple_get_n_cols(Pointer ib_tpl);

    public static native void ib_tuple_delete(Pointer ib_tpl);

    public static native int ib_cursor_truncate(PointerByReference ib_crsr,
            LongBuffer table_id);

    public static native int ib_table_truncate(String table_name,
            LongBuffer table_id);

    public static native int ib_table_get_id(String table_name,
            LongBuffer table_id);

    public static native int ib_index_get_id(String table_name,
            String index_name, java.nio.LongBuffer index_id);

    public static native int ib_database_create(String dbname);

    public static native int ib_database_drop(String dbname);

    public static native int ib_cursor_is_positioned(Pointer ib_crsr);

    public static native int ib_schema_lock_shared(Pointer ib_trx);

    public static native int ib_schema_lock_exclusive(Pointer ib_trx);

    public static native int ib_schema_lock_is_exclusive(Pointer ib_trx);

    public static native int ib_schema_lock_is_shared(Pointer ib_trx);

    public static native int ib_schema_unlock(Pointer ib_trx);

    public static native int ib_cursor_lock(Pointer ib_crsr, int ib_lck_mode);

    public static native int ib_table_lock(Pointer ib_trx, long table_id,
            int ib_lck_mode);

    public static native int ib_cursor_set_lock_mode(Pointer ib_crsr,
            int ib_lck_mode);

    public static native void ib_cursor_set_cluster_access(Pointer ib_crsr);

    public static native int ib_table_schema_visit(Pointer ib_trx, String name,
            HailDB.ib_schema_visitor_t visitor, Pointer arg);

    public static native int ib_schema_tables_iterate(Pointer ib_trx,
            HailDB.ib_schema_visitor_table_all_t visitor, Pointer arg);

    public static native int ib_cfg_var_get_type(String name,
            java.nio.IntBuffer type);

    public static native int ib_cfg_set(String name, Pointer value);
    
    //public static native int ib_cfg_set_int(String name, int value);

    public static int ib_cfg_set(String name, String value) {
        return HailDB.ib_cfg_set(name,
                TupleStorage.getDirectMemoryString(value.getBytes()));
    }

    public static int ib_cfg_set(String name, int value) {
        return HailDB.ib_cfg_set(name, Pointer.createConstant(value));
    }

    public static int ib_cfg_set(String name, long value) {
        return HailDB.ib_cfg_set(name, Pointer.createConstant(value));
    }

    public static int ib_cfg_set_bool_on(String name) {
        return HailDB.ib_cfg_set(name, HailDB.IB_TRUE);
    }

    public static int ib_cfg_set_bool_off(String name) {
        return HailDB.ib_cfg_set(name, HailDB.IB_FALSE);
    }

    public static native int ib_cfg_get(String name, Pointer value);

    public static native int ib_cfg_get_all(PointerByReference names,
            IntBuffer names_num);

    public static native void ib_savepoint_take(Pointer ib_trx, Pointer name,
            int name_len);

    public static native int ib_savepoint_release(Pointer ib_trx, Pointer name,
            int name_len);

    public static native int ib_savepoint_rollback(Pointer ib_trx,
            Pointer name, int name_len);

    public static native int ib_tuple_write_i8(Pointer ib_tpl, int col_no,
            byte val);

    public static native int ib_tuple_write_i16(Pointer ib_tpl, int col_no,
            short val);

    public static native int ib_tuple_write_i32(Pointer ib_tpl, int col_no,
            int val);

    public static native int ib_tuple_write_i64(Pointer ib_tpl, int col_no,
            long val);

    public static native int ib_tuple_write_u8(Pointer ib_tpl, int col_no,
            byte val);

    public static native int ib_tuple_write_u16(Pointer ib_tpl, int col_no,
            short val);

    public static native int ib_tuple_write_u32(Pointer ib_tpl, int col_no,
            int val);

    public static native int ib_tuple_write_u64(Pointer ib_tpl, int col_no,
            long val);

    public static native void ib_cursor_stmt_begin(Pointer ib_crsr);

    public static native int ib_tuple_write_double(Pointer ib_tpl, int col_no,
            double val);

    public static native int ib_tuple_read_double(Pointer ib_tpl, int col_no,
            DoubleBuffer dval);

    public static native int ib_tuple_write_float(Pointer ib_tpl, int col_no,
            float val);

    public static native int ib_tuple_read_float(Pointer ib_tpl, int col_no,
            FloatBuffer fval);

    public static native Pointer ib_strerror(int db_errno);

    public static native int ib_status_get_i64(String name, LongBuffer dst);

    // / Callback function to compare InnoDB key columns in an index.
    public static final class ib_client_compare {
        private static Pointer ib_client_compare;

        public static Pointer get() {
            if (ib_client_compare == null)
                ib_client_compare = com.g414.haildb.impl.jna.HailDB.JNA_NATIVE_LIB
                        .getGlobalVariableAddress("ib_client_compare");
            return ib_client_compare;
        }
    }
}

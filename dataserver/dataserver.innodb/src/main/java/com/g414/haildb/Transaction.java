package com.g414.haildb;

import com.g414.haildb.impl.jna.HailDB;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public class Transaction {
    public enum TransactionLevel {
        READ_UNCOMMITTED(HailDB.ib_trx_level_t.IB_TRX_READ_UNCOMMITTED), READ_COMMITTED(
                HailDB.ib_trx_level_t.IB_TRX_READ_COMMITTED), REPEATABLE_READ(
                HailDB.ib_trx_level_t.IB_TRX_REPEATABLE_READ), SERIALIZABLE(
                HailDB.ib_trx_level_t.IB_TRX_SERIALIZABLE);

        private final int code;

        private TransactionLevel(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static TransactionLevel fromCode(int code) {
            return TransactionLevel.values()[code];
        }
    }

    public enum TransactionState {
        NOT_STARTED(HailDB.ib_trx_state_t.IB_TRX_NOT_STARTED), ACTIVE(
                HailDB.ib_trx_state_t.IB_TRX_ACTIVE), COMMITTED_IN_MEMORY(
                HailDB.ib_trx_state_t.IB_TRX_COMMITTED_IN_MEMORY), PREPARED(
                HailDB.ib_trx_state_t.IB_TRX_PREPARED);

        private final int code;

        private TransactionState(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static TransactionState fromCode(int code) {
            return TransactionState.values()[code];
        }
    }

    protected final Pointer trx;

    public Transaction(Pointer trx) {
        this.trx = trx;
    }

    public Cursor openTable(TableDef tableDef) {
        PointerByReference crsr = new PointerByReference();

        Util.assertSuccess(HailDB.ib_cursor_open_table(tableDef.getName(), trx,
                crsr));

        return new Cursor(crsr, tableDef, null);
    }

    public void commit() {
        Util.assertSuccess(HailDB.ib_trx_commit(trx));
    }

    public void rollback() {
        Util.assertSuccess(HailDB.ib_trx_rollback(trx));
    }

    public void release() {
        Util.assertSuccess(HailDB.ib_trx_release(trx));
    }

    public void start(TransactionLevel level) {
        Util.assertSuccess(HailDB.ib_trx_start(trx, level.getCode()));
    }

    public TransactionState getState() {
        return TransactionState.fromCode(HailDB.ib_trx_state(trx));
    }

    public Pointer getTrx() {
        return trx;
    }
}

package com.g414.haildb;

import com.g414.haildb.impl.jna.HailDB;

public enum ColumnType {
    /* see InnoDB.ib_col_type_t */
    UNUSED(0), VARCHAR(HailDB.ib_col_type_t.IB_VARCHAR), CHAR(
            HailDB.ib_col_type_t.IB_CHAR), BINARY(
            HailDB.ib_col_type_t.IB_BINARY), VARBINARY(
            HailDB.ib_col_type_t.IB_VARBINARY), BLOB(
            HailDB.ib_col_type_t.IB_BLOB), INT(HailDB.ib_col_type_t.IB_INT), SYS(
            HailDB.ib_col_type_t.IB_SYS), FLOAT(HailDB.ib_col_type_t.IB_FLOAT), DOUBLE(
            HailDB.ib_col_type_t.IB_DOUBLE), DECIMAL(
            HailDB.ib_col_type_t.IB_DECIMAL), VARCHAR_ANYCHARSET(
            HailDB.ib_col_type_t.IB_VARCHAR_ANYCHARSET), CHAR_ANYCHARSET(
            HailDB.ib_col_type_t.IB_CHAR_ANYCHARSET);

    private final int code;

    private ColumnType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ColumnType fromCode(int code) {
        return ColumnType.values()[code];
    }

    public boolean isByteArrayType() {
        switch (this.code) {
        case HailDB.ib_col_type_t.IB_BINARY:
        case HailDB.ib_col_type_t.IB_VARBINARY:
        case HailDB.ib_col_type_t.IB_BLOB:
            return true;
        default:
            return false;
        }
    }

    public boolean isStringType() {
        switch (this.code) {
        case HailDB.ib_col_type_t.IB_VARCHAR:
        case HailDB.ib_col_type_t.IB_CHAR:
        case HailDB.ib_col_type_t.IB_VARCHAR_ANYCHARSET:
        case HailDB.ib_col_type_t.IB_CHAR_ANYCHARSET:
            return true;
        default:
            return false;
        }
    }

    public boolean isIntegerType() {
        return this.code == HailDB.ib_col_type_t.IB_INT;
    }
}

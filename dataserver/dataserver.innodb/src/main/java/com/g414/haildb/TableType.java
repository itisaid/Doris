package com.g414.haildb;

import com.g414.haildb.impl.jna.HailDB;

public enum TableType {
    REDUNDANT(HailDB.ib_tbl_fmt_t.IB_TBL_REDUNDANT), COMPACT(
            HailDB.ib_tbl_fmt_t.IB_TBL_COMPACT), DYNAMIC(
            HailDB.ib_tbl_fmt_t.IB_TBL_DYNAMIC), COMPRESSED(
            HailDB.ib_tbl_fmt_t.IB_TBL_COMPRESSED);

    private final int code;

    private TableType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static TableType fromCode(int code) {
        return TableType.values()[code];
    }
}

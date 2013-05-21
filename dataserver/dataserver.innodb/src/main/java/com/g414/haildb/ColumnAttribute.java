package com.g414.haildb;


public enum ColumnAttribute {
    /* see InnoDB.ib_col_attr_t */
    NONE(0), NOT_NULL(1), UNSIGNED(2), NOT_USED(3), COL_CUSTOM1(4), COL_CUSTOM2(
            5), COL_CUSTOM3(6);

    private final int code;

    private ColumnAttribute(int code) {
        this.code = code;
    }

    public int getCode() {
        return code > 0 ? 1 << (code - 1) : code;
    }

    public ColumnAttribute fromCode(int code) {
        return ColumnAttribute.values()[code];
    }
}

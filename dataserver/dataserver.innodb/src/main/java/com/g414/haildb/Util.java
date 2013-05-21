package com.g414.haildb;

import com.g414.haildb.impl.jna.HailDB;

public class Util {
    public static void assertSuccess(int code) {
        if (code != HailDB.db_err.DB_SUCCESS) {
            throw new InnoException("INNODB Error " + code + " : "
                    + HailDB.ib_strerror(code).getString(0));
        }
    }

    public static void assertSchemaOperationSuccess(int code) {
        if (code != HailDB.IB_TRUE) {
            throw new InnoException("INNODB Error " + code + " : "
                    + HailDB.ib_strerror(code).getString(0));
        }
    }
}

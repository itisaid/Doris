package com.alibaba.doris.dataserver.store.mysql.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.alibaba.doris.dataserver.store.mysql.MysqlStorageException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class MysqlUtils {

    public static void close(PreparedStatement s) {
        try {
            if (s != null) {
                s.close();
            }
        } catch (Exception e) {
            throw new MysqlStorageException("Failed to close prepared statement.", e);
        }
    }

    public static void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            throw new MysqlStorageException("Failed to close resultset.", e);
        }
    }

    public static void close(Connection c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception e) {
            throw new MysqlStorageException("Failed to close connection.", e);
        }
    }

    public static void commit(Connection c) {
        try {
            c.commit();
        } catch (SQLException e) {
            throw new MysqlStorageException("Commit failed!", e);
        }
    }

    public static void rollback(Connection c) {
        try {
            c.rollback();
        } catch (SQLException e) {
            throw new MysqlStorageException("Rollback failed!", e);
        }
    }
}

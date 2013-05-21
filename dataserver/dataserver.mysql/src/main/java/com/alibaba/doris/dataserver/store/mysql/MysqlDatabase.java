package com.alibaba.doris.dataserver.store.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import com.alibaba.doris.common.data.CompareStatus;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.dataserver.store.BaseStorage;
import com.alibaba.doris.dataserver.store.StorageType;
import com.alibaba.doris.dataserver.store.mysql.util.MysqlUtils;
import com.alibaba.doris.dataserver.store.serialize.KeyValueSerializerFactory;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class MysqlDatabase extends BaseStorage {

    public MysqlDatabase(String databaseName, DataSource datasource) {
        this.datasource = datasource;
        this.databaseName = databaseName;
    }

    public void close() {
    }

    public void destroy() {
        // execute("drop table if exists " + getDatabaseName());
    }

    private void createTable() {
        // execute("create table " + getDatabaseName() + " (key_ varbinary(200) not null, "
        // + " value_ blob, primary key(key_)) engine = InnoDB");
    }

    private void createTable(Key key) {
        execute("create table " + getDatabaseName(key) + " (key_ varbinary(200) not null, "
                + " value_ blob, primary key(key_)) engine = InnoDB");
    }

    private void execute(String query) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = datasource.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new MysqlStorageException("SQLException while performing operation.", e);
        } finally {
            MysqlUtils.close(stmt);
            MysqlUtils.close(conn);
        }
    }

    // private String getDatabaseName() {
    // return databaseName;
    // }

    private String getDatabaseName(Key key) {
        return databaseName + "_" + key.getVNode();
    }

    private boolean checkTableExists() {
        // Connection conn = null;
        // PreparedStatement stmt = null;
        // ResultSet rs = null;
        // String select = "show tables like '" + getDatabaseName() + "'";
        // try {
        // conn = this.datasource.getConnection();
        // stmt = conn.prepareStatement(select);
        // rs = stmt.executeQuery();
        // return rs.next();
        // } catch (SQLException e) {
        // throw new MysqlStorageException("SQLException while checking for table existence!", e);
        // } finally {
        // MysqlUtils.close(rs);
        // MysqlUtils.close(stmt);
        // MysqlUtils.close(conn);
        // }
        return true;
    }

    public void open() {
        if (!checkTableExists()) {
            createTable();
        }
    }

    public boolean delete(Key key) {
        String delete = "delete from " + getDatabaseName(key) + " where key_ = ?";
        Connection conn = null;
        PreparedStatement deleteStmt = null;
        try {
            conn = datasource.getConnection();
            deleteStmt = conn.prepareStatement(delete);
            deleteStmt.setBytes(1, serializerFactory.encode(key).copyBytes());

            return deleteStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new MysqlStorageException("Sql exception on delete!" + key.getKey(), e);
        } finally {
            MysqlUtils.close(deleteStmt);
            MysqlUtils.close(conn);
        }
    }

    public boolean delete(Key key, Value value) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean delete(List<Integer> vnodeList) {
        return false;
    }

    public Value get(Key key) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String select = "select value_ from " + getDatabaseName(key) + " where key_ = ?";
        try {
            conn = datasource.getConnection();
            stmt = conn.prepareStatement(select);
            stmt.setBytes(1, serializerFactory.encode(key).copyBytes());
            rs = stmt.executeQuery();

            while (rs.next()) {
                byte[] valueBytes = rs.getBytes("value_");
                return serializerFactory.decodeValue(valueBytes);
            }
        } catch (SQLException e) {
            throw new MysqlStorageException("Sql exception!", e);
        } finally {
            MysqlUtils.close(rs);
            MysqlUtils.close(stmt);
            MysqlUtils.close(conn);
        }

        return null;
    }

    public void set(Key key, Value value) {
        boolean doCommit = false;
        Connection conn = null;
        PreparedStatement insert = null;
        PreparedStatement select = null;
        ResultSet results = null;
        String insertSql = "insert into " + getDatabaseName(key)
                           + " (key_, value_) values (?, ?)  ON DUPLICATE KEY UPDATE value_= ? ";

        try {
            conn = datasource.getConnection();
            conn.setAutoCommit(false);

            byte[] keyBytes = serializerFactory.encode(key).copyBytes();
            byte[] valueBytes = serializerFactory.encode(value).copyBytes();

            insert = conn.prepareStatement(insertSql);
            insert.setBytes(1, keyBytes);
            insert.setBytes(2, valueBytes);
            insert.setBytes(3, valueBytes);
            insert.executeUpdate();
            doCommit = true;
        } catch (SQLException e) {
            throw new MysqlStorageException("Fix me!" + key, e);
        } finally {
            if (conn != null) {
                if (doCommit) {
                    MysqlUtils.commit(conn);
                } else {
                    MysqlUtils.rollback(conn);
                }
            }
            MysqlUtils.close(results);
            MysqlUtils.close(insert);
            MysqlUtils.close(select);
            MysqlUtils.close(conn);
        }
    }

    public void set(Key key, Value value, boolean isSetWithCompareVersion) {
        boolean doCommit = false;
        Connection conn = null;
        PreparedStatement insert = null;
        PreparedStatement select = null;
        ResultSet results = null;
        String insertSql = "insert into " + getDatabaseName(key) + " (key_, value_) values (?, ?)";

        String selectSql = "select value_ from " + getDatabaseName(key) + " where key_ = ?";
        try {
            conn = datasource.getConnection();
            conn.setAutoCommit(false);

            byte[] keyBytes = serializerFactory.encode(key).copyBytes();
            byte[] valueBytes = serializerFactory.encode(value).copyBytes();
            // check for superior versions
            select = conn.prepareStatement(selectSql);
            select.setBytes(1, keyBytes);
            results = select.executeQuery();

            Value oldValue = null;
            while (results.next()) {
                serializerFactory.decodeValue(results.getBytes("value_"));
                if (!CompareStatus.AFTER.equals(value.compareVersion(oldValue))) {
                    return;
                } else {
                    delete(key);
                }

            }

            // Okay, cool, now put the value
            insert = conn.prepareStatement(insertSql);
            insert.setBytes(1, keyBytes);
            insert.setBytes(2, valueBytes);
            insert.executeUpdate();

            doCommit = true;
        } catch (SQLException e) {
            if (e.getErrorCode() == MYSQL_ERR_DUP_KEY || e.getErrorCode() == MYSQL_ERR_DUP_ENTRY) {
                throw new MysqlStorageException("Key or value already used.");
            } else {
                throw new MysqlStorageException("Fix me!", e);
            }
        } finally {
            if (conn != null) {
                if (doCommit) {
                    MysqlUtils.commit(conn);
                } else {
                    MysqlUtils.rollback(conn);
                }
            }
            MysqlUtils.close(results);
            MysqlUtils.close(insert);
            MysqlUtils.close(select);
            MysqlUtils.close(conn);
        }
    }

    public StorageType getType() {
        return MysqlStorageType.MYSQL;
    }

    public Iterator<Pair> iterator() {

        return null;
    }

    public Iterator<Pair> iterator(List<Integer> vnodeList) {

        return null;
    }

    private DataSource                             datasource;
    private MysqlStorageConfigure                  config;
    private String                                 databaseName;
    private static int                             MYSQL_ERR_DUP_KEY   = 1022;
    private static int                             MYSQL_ERR_DUP_ENTRY = 1062;
    private static final KeyValueSerializerFactory serializerFactory   = KeyValueSerializerFactory.getInstance();

}

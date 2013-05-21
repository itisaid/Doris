package com.alibaba.doris.dataserver.store.innodb.db;

import com.alibaba.doris.dataserver.store.innodb.config.InnoDBDatabaseConfiguration;
import com.g414.haildb.ColumnAttribute;
import com.g414.haildb.ColumnType;
import com.g414.haildb.Database;
import com.g414.haildb.TableBuilder;
import com.g414.haildb.TableDef;
import com.g414.haildb.TableType;

/**
 * @author long.mal long.mal@alibaba-inc.com
 */
public final class InnoDBBuilder {

    public InnoDBBuilder(InnoDBDatabaseConfiguration conf) {
        this.conf = conf;
    }

    public void initDataBase() {
        // init database;
        this.database = new Database(conf);
        this.database.createDatabase(conf.getSchema());
    }

    public InnoDBDataBase buildInnoDBDataBase(String databaseName) {
        InnoDBDataBase db = new InnoDBDataBase(this, databaseName);
        db.open();
        return db;
    }

    public boolean deleteInnoDBDataBase(InnoDBDataBase database) {
        this.database.dropTable(database.getTableDef());
        return true;
    }

    public TableDef buildTable(String tableName) {
        TableBuilder builder = new TableBuilder(conf.getSchema() + "/vn" + tableName);

        builder.addColumn(FIELD_KEY, ColumnType.VARBINARY, conf.getKeyLength(), ColumnAttribute.NOT_NULL);
        builder.addColumn(FIELD_VALUE, ColumnType.BLOB, 0);
//        if (conf.getValueLength() > INNO_MAX_ROW_LENGTH) {
//            builder.addColumn(FIELD_VALUE, ColumnType.BLOB, 0);
//        } else {
//            builder.addColumn(FIELD_VALUE, ColumnType.VARBINARY, (int) conf.getValueLength(), ColumnAttribute.NOT_NULL);
//        }
        builder.addColumn(FIELD_VERSION, ColumnType.INT, 8, ColumnAttribute.NOT_NULL);

        builder.addIndex("PRIMARY", FIELD_KEY, 0, true, true);

        TableDef tableDef = builder.build();

        if (!this.database.tableExists(tableDef)) {
            this.database.createTable(tableDef, TableType.DYNAMIC, 0);
        }

        return tableDef;
    }

    public Database getDatabase() {
        return this.database;
    }

    private Database                    database;
    private InnoDBDatabaseConfiguration conf;
    public static final String          FIELD_KEY      = "key_";
    public static final String          FIELD_VALUE    = "value_";
    public static final String          FIELD_VERSION  = "version_";
    public static final long            INNO_MAX_ROW_LENGTH = 65535;
}

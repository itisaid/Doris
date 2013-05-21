package com.g414.haildb;

import java.io.File;
import java.nio.LongBuffer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.g414.haildb.Transaction.TransactionLevel;
import com.g414.haildb.impl.jna.HailDB;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public class Database {

    public Database() {
        this(new DatabaseConfiguration());
    }

    public Database(DatabaseConfiguration c) {
        Util.assertSuccess(HailDB.ib_init());

        if (c.isAdaptiveHashEnabled()) {
            Util.assertSuccess(HailDB.ib_cfg_set_bool_on("adaptive_hash_index"));
        } else {
            Util.assertSuccess(HailDB.ib_cfg_set_bool_off("adaptive_hash_index"));
        }

        if (c.isAdaptiveFlushingEnabled()) {
            Util.assertSuccess(HailDB.ib_cfg_set_bool_on("adaptive_flushing"));
        } else {
            Util.assertSuccess(HailDB.ib_cfg_set_bool_off("adaptive_flushing"));
        }

        if (c.isDoublewriteEnabled()) {
            Util.assertSuccess(HailDB.ib_cfg_set_bool_on("doublewrite"));
        } else {
            Util.assertSuccess(HailDB.ib_cfg_set_bool_off("doublewrite"));
        }

        if (c.isFilePerTableEnabled()) {
            Util.assertSuccess(HailDB.ib_cfg_set_bool_on("file_per_table"));
        } else {
            Util.assertSuccess(HailDB.ib_cfg_set_bool_off("file_per_table"));
        }

        if (c.isPageChecksumsEnabled()) {
            Util.assertSuccess(HailDB.ib_cfg_set_bool_on("checksums"));
        } else {
            Util.assertSuccess(HailDB.ib_cfg_set_bool_off("checksums"));
        }

        if (c.isPrintVerboseLog()) {
            Util.assertSuccess(HailDB.ib_cfg_set_bool_on("print_verbose_log"));
        } else {
            Util.assertSuccess(HailDB.ib_cfg_set_bool_off("print_verbose_log"));
        }

        if (c.isRollbackOnTimeoutEnabled()) {
            Util.assertSuccess(HailDB.ib_cfg_set_bool_on("rollback_on_timeout"));
        } else {
            Util.assertSuccess(HailDB.ib_cfg_set_bool_off("rollback_on_timeout"));
        }

        if (c.isStatusFileEnabled()) {
            Util.assertSuccess(HailDB.ib_cfg_set_bool_on("status_file"));
        } else {
            Util.assertSuccess(HailDB.ib_cfg_set_bool_off("status_file"));
        }

        if (c.isSysMallocEnabled()) {
            Util.assertSuccess(HailDB.ib_cfg_set_bool_on("use_sys_malloc"));
        } else {
            Util.assertSuccess(HailDB.ib_cfg_set_bool_off("use_sys_malloc"));
        }

        Util.assertSuccess(HailDB.ib_cfg_set("data_file_path", c.getDatafilePath()));
        Util.assertSuccess(HailDB.ib_cfg_set("data_home_dir", c.getDataHomeDir()));
        Util.assertSuccess(HailDB.ib_cfg_set("log_group_home_dir", c.getLogFileHomeDirectory()));
        Util.assertSuccess(HailDB.ib_cfg_set("flush_log_at_trx_commit", c.getFlushLogAtTrxCommitMode().getCode()));
        Util.assertSuccess(HailDB.ib_cfg_set("flush_method", c.getFlushMethod().getCode()));
        Util.assertSuccess(HailDB.ib_cfg_set("force_recovery", c.getRecoveryMethod().getCode()));

        Util.assertSuccess(HailDB.ib_cfg_set("lru_block_access_recency", c.getLruBlockAccessRecency()));
        Util.assertSuccess(HailDB.ib_cfg_set("lru_old_blocks_pct", c.getLruOldBlocksPct()));
        Util.assertSuccess(HailDB.ib_cfg_set("max_dirty_pages_pct", c.getMaxDirtyPagesPct()));
        Util.assertSuccess(HailDB.ib_cfg_set("max_purge_lag", c.getMaxPurgeLagSeconds()));
        Util.assertSuccess(HailDB.ib_cfg_set("open_files", c.getOpenFilesLimit()));
        Util.assertSuccess(HailDB.ib_cfg_set("autoextend_increment", c.getAutoextendIncrementSizePages()));
        Util.assertSuccess(HailDB.ib_cfg_set("io_capacity", c.getIoCapacityIOPS()));
        Util.assertSuccess(HailDB.ib_cfg_set("sync_spin_loops", c.getSyncSpinLoops()));
        Util.assertSuccess(HailDB.ib_cfg_set("lock_wait_timeout", c.getLockWaitTimeoutSeconds()));

		if (c.getLogFileSize() > 0) {
			Util.assertSuccess(HailDB.ib_cfg_set("log_file_size",
					c.getLogFileSize()));
		}
		
		if (c.getLogBufferSize() > 0) {
			Util.assertSuccess(HailDB.ib_cfg_set("log_buffer_size",
					c.getLogBufferSize()));
		}
		
		if (c.getLogFilesInGroup() > 0) {
			Util.assertSuccess(HailDB.ib_cfg_set("log_files_in_group",
					c.getLogFilesInGroup()));
		}
		
		if (c.getBufferPoolSize() > 0) {
			Util.assertSuccess(HailDB.ib_cfg_set("buffer_pool_size",
					c.getBufferPoolSize()));
		}
		
		if (c.getAdditionalMemPoolSize() > 0) {
			Util.assertSuccess(HailDB.ib_cfg_set("additional_mem_pool_size",
					c.getAdditionalMemPoolSize()));
		}

		//        Util.assertSuccess(HailDB.ib_cfg_set("log_file_size", c.getLogFileSize()));
//        Util.assertSuccess(HailDB.ib_cfg_set("log_buffer_size", c.getLogBufferSize()));
//        Util.assertSuccess(HailDB.ib_cfg_set("log_files_in_group", c.getLogFilesInGroup()));
//        Util.assertSuccess(HailDB.ib_cfg_set("buffer_pool_size", c.getBufferPoolSize()));
//        Util.assertSuccess(HailDB.ib_cfg_set("additional_mem_pool_size", c.getAdditionalMemPoolSize()));

        Util.assertSuccess(HailDB.ib_startup(c.getFileFormat().getCode()));
        isOpenning.set(true);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            public void run() {
                Database.this.shutdown(false);
            }
        }));
    }

    public void createDatabase(String databaseName) {
        Util.assertSchemaOperationSuccess(HailDB.ib_database_create(databaseName));
    }

    public void dropDatabase(String databaseName) {
        File dbFile = new File(databaseName);
        if (!dbFile.exists()) {
            return;
        }

        Util.assertSuccess(HailDB.ib_database_drop(databaseName));
    }

    public Transaction beginTransaction(TransactionLevel level) {
        Pointer trx = HailDB.ib_trx_begin(level.getCode());

        return new Transaction(trx);
    }

    public void createTable(TableDef tableDef) {
        this.createTable(tableDef, TableType.DYNAMIC, 0);
    }

    public void createTable(TableDef tableDef, TableType type, int pageSize) {
        if (tableExists(tableDef)) {
            throw new InnoException("table already exists: " + tableDef.getName());
        }

        PointerByReference schema = new PointerByReference();
        Util.assertSuccess(HailDB.ib_table_schema_create(tableDef.getName(), schema, type.getCode(), pageSize));

        for (ColumnDef def : tableDef.getColumnDefs().values()) {
            int attr = 0;
            for (ColumnAttribute a : def.getAttrs()) {
                attr |= a.getCode();
            }

            if (!def.getType().equals(ColumnType.BLOB)) {
                Util.assertSuccess(HailDB.ib_table_schema_add_col(schema.getValue(), def.getName(),
                                                                  def.getType().getCode(), attr, (short) 0,
                                                                  def.getLength().intValue()));
            } else {
                Util.assertSuccess(HailDB.ib_table_schema_add_col(schema.getValue(), def.getName(),
                                                                  def.getType().getCode(), 0, (short) 0, 0));
            }
        }

        for (Map.Entry<String, IndexDef> entry : tableDef.getIndexDefs().entrySet()) {
            PointerByReference index = new PointerByReference();
            Util.assertSuccess(HailDB.ib_table_schema_add_index(schema.getValue(), entry.getKey(), index));
            IndexDef part = entry.getValue();
            Map<String, Integer> prefixLenOverrides = part.getPrefixLenOverrides();

            for (ColumnDef col : part.getColumns()) {
                int prefixLenOverride = prefixLenOverrides.containsKey(col.getName()) ? prefixLenOverrides.get(col.getName()) : 0;
                Util.assertSuccess(HailDB.ib_index_schema_add_col(index.getValue(), col.getName(), prefixLenOverride));
            }

            if (part.isClustered()) {
                Util.assertSuccess(HailDB.ib_index_schema_set_clustered(index.getValue()));
            } else if (part.isUnique()) {
                Util.assertSuccess(HailDB.ib_index_schema_set_unique(index.getValue()));
            }
        }

        LongBuffer tableId = LongBuffer.allocate(1);
        Transaction trx = this.beginTransaction(TransactionLevel.REPEATABLE_READ);
        try {
            Util.assertSuccess(HailDB.ib_schema_lock_exclusive(trx.getTrx()));
            Util.assertSuccess(HailDB.ib_table_create(trx.getTrx(), schema.getValue(), tableId));
            trx.commit();
        } catch (InnoException e) {
            trx.rollback();

            throw e;
        } finally {
            HailDB.ib_table_schema_delete(schema.getValue());
        }
    }

    public void dropTable(TableDef def) {
        Transaction trx = this.beginTransaction(TransactionLevel.REPEATABLE_READ);
        try {
            Util.assertSuccess(HailDB.ib_schema_lock_exclusive(trx.getTrx()));
            Util.assertSuccess(HailDB.ib_table_drop(trx.getTrx(), def.getName()));
            trx.commit();
        } catch (InnoException e) {
            throw e;
        }
    }

    public Long truncateTable(TableDef tableDef) {
        LongBuffer tableId = LongBuffer.allocate(1);
        Util.assertSuccess(HailDB.ib_table_truncate(tableDef.getName(), tableId));

        return tableId.get();
    }

    public boolean tableExists(TableDef tableDef) {
        boolean found = false;
        Transaction txn = null;
        Cursor check = null;
        try {
            txn = this.beginTransaction(TransactionLevel.REPEATABLE_READ);
            check = txn.openTable(tableDef);

            found = true;
        } catch (InnoException expected) {
            if (!expected.getMessage().contains("Table not found")) {
                throw expected;
            }
        } finally {
            if (check != null) {
                check.close();
            }

            if (txn != null) {
                txn.commit();
            }
        }
        return found;
    }

    public void shutdown(boolean fast) {
    	if(isOpenning.compareAndSet(true, false)){
	        int flag = fast ? HailDB.ib_shutdown_t.IB_SHUTDOWN_NO_BUFPOOL_FLUSH : HailDB.ib_shutdown_t.IB_SHUTDOWN_NORMAL;
	        Util.assertSuccess(HailDB.ib_shutdown(flag));
    	}
    }
    
    private volatile AtomicBoolean isOpenning = new AtomicBoolean(false);
}

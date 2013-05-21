package com.alibaba.doris.dataserver.store.innodb.util;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.config.ConfigTools;
import com.alibaba.doris.dataserver.store.innodb.config.InnoDBDatabaseConfiguration;
import com.g414.haildb.DatabaseConfiguration.FileFormat;
import com.g414.haildb.DatabaseConfiguration.FlushMethod;
import com.g414.haildb.DatabaseConfiguration.LogFlushMode;
import com.g414.haildb.DatabaseConfiguration.RecoveryMethod;

/*
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public final class InnoDBConfigUtils {

    public static InnoDBDatabaseConfiguration loadConfigFile(String configFile) {
        if (StringUtils.isEmpty(configFile)) {
            if (logger.isWarnEnabled()) {
                logger.warn("Couldn't find the configure file of InnoDB, using default configure information.");
            }
            return loadDefaultConfiguration();
        }

        Properties properties = ConfigTools.loadProperties(configFile);
        return loadConfigFileFromProperties(properties);
    }

    private static InnoDBDatabaseConfiguration loadDefaultConfiguration() {
        InnoDBDatabaseConfiguration dbConfiguration = new InnoDBDatabaseConfiguration();
        dbConfiguration.setBufferPoolSize(DEFAULT_BUFFER_POOL_SIZE);
        dbConfiguration.setFlushLogAtTrxCommitMode(LogFlushMode.ONCE_PER_SECOND);
        return dbConfiguration;
    }

    private static InnoDBDatabaseConfiguration loadConfigFileFromProperties(Properties properties) {
        InnoDBDatabaseConfiguration dbConfiguration = loadDefaultConfiguration();
        if (StringUtils.isNotEmpty((properties.getProperty("file_per_table")))) {
            dbConfiguration.setFilePerTableEnabled(Boolean.valueOf(properties.getProperty("file_per_table")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("flush_log_at_trx_commit")))) {
            dbConfiguration.setFlushLogAtTrxCommitMode(getLogFlushMode(properties.getProperty("flush_log_at_trx_commit")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("flush_method")))) {
            dbConfiguration.setFlushMethod(getLogFlushMethod(properties.getProperty("flush_method")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("force_recovery")))) {
            dbConfiguration.setRecoveryMethod(getRecoveryMethod(properties.getProperty("force_recovery")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("io_capacity")))) {
            dbConfiguration.setIoCapacityIOPS(Integer.valueOf(properties.getProperty("io_capacity")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("sync_spin_loops")))) {
            dbConfiguration.setSyncSpinLoops(Integer.valueOf(properties.getProperty("sync_spin_loops")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("print_verbose_log")))) {
            dbConfiguration.setPrintVerboseLog(Boolean.valueOf(properties.getProperty("print_verbose_log")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("status_file")))) {
            dbConfiguration.setStatusFileEnabled(Boolean.valueOf(properties.getProperty("status_file")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("use_sys_malloc")))) {
            dbConfiguration.setSysMallocEnabled(Boolean.valueOf(properties.getProperty("use_sys_malloc")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("rollback_on_timeout")))) {
            dbConfiguration.setRollbackOnTimeoutEnabled(Boolean.valueOf(properties.getProperty("rollback_on_timeout")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("adaptive_hash_index")))) {
            dbConfiguration.setAdaptiveHashEnabled(Boolean.valueOf(properties.getProperty("adaptive_hash_index")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("adaptive_flushing")))) {
            dbConfiguration.setAdaptiveFlushingEnabled(Boolean.valueOf(properties.getProperty("adaptive_flushing")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("additional_mem_pool_size")))) {
            dbConfiguration.setAdditionalMemPoolSize(Integer.valueOf(properties.getProperty("additional_mem_pool_size")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("autoextend_increment")))) {
            dbConfiguration.setAutoextendIncrementSizePages(Integer.valueOf(properties.getProperty("autoextend_increment")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("buffer_pool_size")))) {
            dbConfiguration.setBufferPoolSize(Long.valueOf(properties.getProperty("buffer_pool_size")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("checksums")))) {
            dbConfiguration.setPageChecksumsEnabled(Boolean.valueOf(properties.getProperty("checksums")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("data_file_path")))) {
            dbConfiguration.setDatafilePath(properties.getProperty("data_file_path"));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("doublewrite")))) {
            dbConfiguration.setDoublewriteEnabled(Boolean.valueOf(properties.getProperty("doublewrite")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("file_format")))) {
            dbConfiguration.setFileFormat(getFileFormat(properties.getProperty("file_format")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("open_files")))) {
            dbConfiguration.setOpenFilesLimit(Integer.valueOf(properties.getProperty("open_files")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("lock_wait_timeout")))) {
            dbConfiguration.setLockWaitTimeoutSeconds(Integer.valueOf(properties.getProperty("lock_wait_timeout")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("log_buffer_size")))) {
            dbConfiguration.setLogBufferSize(properties.getProperty("log_buffer_size"));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("log_file_size")))) {
            dbConfiguration.setLogFileSizeMegabytes(properties.getProperty("log_file_size"));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("log_files_in_group")))) {
            dbConfiguration.setLogFilesInGroup(Integer.valueOf(properties.getProperty("log_files_in_group")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("log_group_home_dir")))) {
            dbConfiguration.setLogFileHomeDirectory(properties.getProperty("log_group_home_dir"));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("data_home_dir")))) {
            dbConfiguration.setDataHomeDir(properties.getProperty("data_home_dir"));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("max_dirty_pages_pct")))) {
            dbConfiguration.setMaxDirtyPagesPct(Integer.valueOf(properties.getProperty("max_dirty_pages_pct")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("max_purge_lag")))) {
            dbConfiguration.setMaxPurgeLagSeconds(Integer.valueOf(properties.getProperty("max_purge_lag")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("lru_old_blocks_pct")))) {
            dbConfiguration.setLruOldBlocksPct(Integer.valueOf(properties.getProperty("lru_old_blocks_pct")));
        }

        if (StringUtils.isNotEmpty((properties.getProperty("lru_block_access_recency")))) {
            dbConfiguration.setLruBlockAccessRecency(Integer.valueOf(properties.getProperty("lru_block_access_recency")));
        }

        return dbConfiguration;
    }

    private static LogFlushMode getLogFlushMode(String logFlushMode) {
        if ("ONCE_PER_SECOND".equalsIgnoreCase(logFlushMode)) {
            return LogFlushMode.ONCE_PER_SECOND;
        }

        if ("AT_TRX_COMMIT_SYNC".equalsIgnoreCase(logFlushMode)) {
            return LogFlushMode.AT_TRX_COMMIT_SYNC;
        }

        if ("AT_TRX_COMMIT_NOSYNC".equalsIgnoreCase(logFlushMode)) {
            return LogFlushMode.AT_TRX_COMMIT_NOSYNC;
        }

        return LogFlushMode.ONCE_PER_SECOND;
    }

    private static FlushMethod getLogFlushMethod(String flushMethod) {
        if (FlushMethod.FSYNC.getCode().equalsIgnoreCase(flushMethod)) {
            return FlushMethod.FSYNC;
        }

        if (FlushMethod.O_DIRECT.getCode().equalsIgnoreCase(flushMethod)) {
            return FlushMethod.O_DIRECT;
        }

        if (FlushMethod.O_DSYNC.getCode().equalsIgnoreCase(flushMethod)) {
            return FlushMethod.O_DSYNC;
        }

        return FlushMethod.FSYNC;
    }

    private static RecoveryMethod getRecoveryMethod(String recoveryMethod) {
        if ("NONE".equalsIgnoreCase(recoveryMethod)) {
            return RecoveryMethod.NONE;
        }

        if ("FORCE_RECOVERY".equalsIgnoreCase(recoveryMethod)) {
            return RecoveryMethod.FORCE_RECOVERY;
        }

        if ("DISABLE_MASTER".equalsIgnoreCase(recoveryMethod)) {
            return RecoveryMethod.DISABLE_MASTER;
        }

        if ("NO_ROLLBACK_INCOMPLETE".equalsIgnoreCase(recoveryMethod)) {
            return RecoveryMethod.NO_ROLLBACK_INCOMPLETE;
        }

        if ("DISABLE_INSERT_BUFFER_MERGE".equalsIgnoreCase(recoveryMethod)) {
            return RecoveryMethod.DISABLE_INSERT_BUFFER_MERGE;
        }

        if ("DISABLE_UNDO_LOG".equalsIgnoreCase(recoveryMethod)) {
            return RecoveryMethod.DISABLE_UNDO_LOG;
        }

        if ("DISABLE_REDO_LOG".equalsIgnoreCase(recoveryMethod)) {
            return RecoveryMethod.DISABLE_REDO_LOG;
        } 
        
        return null;
    }

    private static FileFormat getFileFormat(String fileFormat) {
        if (FileFormat.ANTELOPE.getCode().equalsIgnoreCase(fileFormat)) {
            return FileFormat.ANTELOPE;
        }

        if (FileFormat.BARRACUDA.getCode().equalsIgnoreCase(fileFormat)) {
            return FileFormat.BARRACUDA;
        }
        
        return null;
    }

    private InnoDBConfigUtils() {

    }

    private static final int DEFAULT_BUFFER_POOL_SIZE = 512 * 1024 * 1024;                               // 512M
    private static Logger    logger                   = LoggerFactory.getLogger(InnoDBConfigUtils.class);
}

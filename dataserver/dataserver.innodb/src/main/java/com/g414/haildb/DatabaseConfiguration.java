package com.g414.haildb;

import java.io.File;

public class DatabaseConfiguration {
    private boolean filePerTableEnabled = true;
    private LogFlushMode flushLogAtTrxCommitMode = LogFlushMode.ONCE_PER_SECOND;
    private FlushMethod flushMethod = FlushMethod.O_DIRECT;
    private RecoveryMethod recoveryMethod = RecoveryMethod.NONE;
    private int ioCapacityIOPS = 1000;
    private int syncSpinLoops = 30;
    private boolean printVerboseLog = true;
    private boolean statusFileEnabled = false;
    private boolean sysMallocEnabled = false;
    private boolean rollbackOnTimeoutEnabled = true;
    private boolean adaptiveHashEnabled = true;
    private boolean adaptiveFlushingEnabled = true;
    private int autoextendIncrementSizePages = 8;
    private boolean pageChecksumsEnabled = true;
    private String datafilePath = "ibdata1:16M:autoextend";
    private boolean doublewriteEnabled = true;
    private FileFormat fileFormat = FileFormat.BARRACUDA;
    private int openFilesLimit = 300;
    private int lockWaitTimeoutSeconds = 120;
    private int logBufferSize = 16 * 1024 * 1024;
    private int logFileSize = 1024 * 1024 * 1024;
    private long additionalMemPoolSize = 16 * 1024 * 1024;
    private long bufferPoolSize = 16 * 1024 * 1024 * 1024;
    private int logFilesInGroup = 3;
    private String logFileHomeDirectory = "." + File.separator;
    private String dataHomeDir = "." + File.separator;
    private int maxDirtyPagesPct = 75;
    private int maxPurgeLagSeconds = 0;
    private int lruOldBlocksPct = 37;
    private int lruBlockAccessRecency = 0;


    public boolean isAdaptiveHashEnabled() {
        return adaptiveHashEnabled;
    }

    public void setAdaptiveHashEnabled(boolean adaptiveHashEnabled) {
        this.adaptiveHashEnabled = adaptiveHashEnabled;
    }

    public boolean isAdaptiveFlushingEnabled() {
        return adaptiveFlushingEnabled;
    }

    public void setAdaptiveFlushingEnabled(boolean adaptiveFlushingEnabled) {
        this.adaptiveFlushingEnabled = adaptiveFlushingEnabled;
    }

    public long getAdditionalMemPoolSize() {
        return additionalMemPoolSize;
    }

    public void setAdditionalMemPoolSize(long additionalMemPoolSize) {
        this.additionalMemPoolSize = additionalMemPoolSize;
    }

    public RecoveryMethod getRecoveryMethod() {
        return recoveryMethod;
    }

    public void setRecoveryMethod(RecoveryMethod recoveryMethod) {
        this.recoveryMethod = recoveryMethod;
    }

    public int getAutoextendIncrementSizePages() {
        return autoextendIncrementSizePages;
    }

    public void setAutoextendIncrementSizePages(int autoextendIncrementSizePages) {
        this.autoextendIncrementSizePages = autoextendIncrementSizePages;
    }

    public long getBufferPoolSize() {
        return bufferPoolSize;
    }

    public void setBufferPoolSize(long bufferPoolSize) {
        this.bufferPoolSize = bufferPoolSize;
    }

    public boolean isPageChecksumsEnabled() {
        return pageChecksumsEnabled;
    }

    public void setPageChecksumsEnabled(boolean pageChecksumsEnabled) {
        this.pageChecksumsEnabled = pageChecksumsEnabled;
    }

    public String getDatafilePath() {
        return datafilePath;
    }

    public void setDatafilePath(String datafilePath) {
        this.datafilePath = datafilePath;
    }

    public String getDataHomeDir() {
        return dataHomeDir;
    }

    public void setDataHomeDir(String dataHomeDir) {
        this.dataHomeDir = dataHomeDir;
    }

    public boolean isDoublewriteEnabled() {
        return doublewriteEnabled;
    }

    public void setDoublewriteEnabled(boolean doublewriteEnabled) {
        this.doublewriteEnabled = doublewriteEnabled;
    }

    public FileFormat getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(FileFormat fileFormat) {
        this.fileFormat = fileFormat;
    }

    public int getOpenFilesLimit() {
        return openFilesLimit;
    }

    public void setOpenFilesLimit(int openFilesLimit) {
        this.openFilesLimit = openFilesLimit;
    }

    public int getLockWaitTimeoutSeconds() {
        return lockWaitTimeoutSeconds;
    }

    public void setLockWaitTimeoutSeconds(int lockWaitTimeoutSeconds) {
        this.lockWaitTimeoutSeconds = lockWaitTimeoutSeconds;
    }

    public int getLogBufferSize() {
        return logBufferSize;
    }

    public void setLogBufferSize(String logBufferSize) {
        this.logBufferSize = Integer.parseInt(logBufferSize);
    }

    public int getLogFileSize() {
        return logFileSize;
    }

    public void setLogFileSizeMegabytes(String logFileSize) {
        this.logFileSize = Integer.parseInt(logFileSize);
    }

    public int getLogFilesInGroup() {
        return logFilesInGroup;
    }

    public void setLogFilesInGroup(int logFilesInGroup) {
        this.logFilesInGroup = logFilesInGroup;
    }

    public String getLogFileHomeDirectory() {
        return logFileHomeDirectory;
    }

    public void setLogFileHomeDirectory(String logFileHomeDirectory) {
        this.logFileHomeDirectory = logFileHomeDirectory;
    }

    public int getMaxDirtyPagesPct() {
        return maxDirtyPagesPct;
    }

    public void setMaxDirtyPagesPct(int maxDirtyPagesPct) {
        this.maxDirtyPagesPct = maxDirtyPagesPct;
    }

    public int getMaxPurgeLagSeconds() {
        return maxPurgeLagSeconds;
    }

    public void setMaxPurgeLagSeconds(int maxPurgeLagSeconds) {
        this.maxPurgeLagSeconds = maxPurgeLagSeconds;
    }

    public int getLruOldBlocksPct() {
        return lruOldBlocksPct;
    }

    public void setLruOldBlocksPct(int lruOldBlocksPct) {
        this.lruOldBlocksPct = lruOldBlocksPct;
    }

    public int getLruBlockAccessRecency() {
        return lruBlockAccessRecency;
    }

    public void setLruBlockAccessRecency(int lruBlockAccessRecency) {
        this.lruBlockAccessRecency = lruBlockAccessRecency;
    }

    public boolean isFilePerTableEnabled() {
        return filePerTableEnabled;
    }

    public void setFilePerTableEnabled(boolean filePerTableEnabled) {
        this.filePerTableEnabled = filePerTableEnabled;
    }

    public LogFlushMode getFlushLogAtTrxCommitMode() {
        return flushLogAtTrxCommitMode;
    }

    public void setFlushLogAtTrxCommitMode(LogFlushMode flushLogAtTrxCommitMode) {
        this.flushLogAtTrxCommitMode = flushLogAtTrxCommitMode;
    }

    public FlushMethod getFlushMethod() {
        return flushMethod;
    }

    public void setFlushMethod(FlushMethod flushMethod) {
        this.flushMethod = flushMethod;
    }

    public int getIoCapacityIOPS() {
        return ioCapacityIOPS;
    }

    public void setIoCapacityIOPS(int ioCapacityIOPS) {
        this.ioCapacityIOPS = ioCapacityIOPS;
    }

    public int getSyncSpinLoops() {
        return syncSpinLoops;
    }

    public void setSyncSpinLoops(int syncSpinLoops) {
        this.syncSpinLoops = syncSpinLoops;
    }

    public boolean isPrintVerboseLog() {
        return printVerboseLog;
    }

    public void setPrintVerboseLog(boolean printVerboseLog) {
        this.printVerboseLog = printVerboseLog;
    }

    public boolean isStatusFileEnabled() {
        return statusFileEnabled;
    }

    public void setStatusFileEnabled(boolean statusFileEnabled) {
        this.statusFileEnabled = statusFileEnabled;
    }

    public boolean isSysMallocEnabled() {
        return sysMallocEnabled;
    }

    public void setSysMallocEnabled(boolean sysMallocEnabled) {
        this.sysMallocEnabled = sysMallocEnabled;
    }

    public boolean isRollbackOnTimeoutEnabled() {
        return rollbackOnTimeoutEnabled;
    }

    public void setRollbackOnTimeoutEnabled(boolean rollbackOnTimeoutEnabled) {
        this.rollbackOnTimeoutEnabled = rollbackOnTimeoutEnabled;
    }

    public DatabaseConfiguration() {
    }

    public enum LogFlushMode {
        ONCE_PER_SECOND(0), AT_TRX_COMMIT_SYNC(1), AT_TRX_COMMIT_NOSYNC(2);

        private final int code;

        private LogFlushMode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public LogFlushMode fromCode(int code) {
            return LogFlushMode.values()[code];
        }
    }

    public enum FlushMethod {
        FSYNC("fsync"), O_DIRECT("O_DIRECT"), O_DSYNC("O_DSYNC");

        private final String code;

        private FlushMethod(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public FlushMethod fromCode(String code) {
            return FlushMethod.valueOf(code.toUpperCase());
        }
    }

    public enum FileFormat {
        ANTELOPE("antelope"), BARRACUDA("barracuda");

        private final String code;

        private FileFormat(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public FileFormat fromCode(String code) {
            return FileFormat.valueOf(code.toUpperCase());
        }
    }

    public enum RecoveryMethod {
        NONE(0), FORCE_RECOVERY(1), DISABLE_MASTER(2), NO_ROLLBACK_INCOMPLETE(3), DISABLE_INSERT_BUFFER_MERGE(
                4), DISABLE_UNDO_LOG(5), DISABLE_REDO_LOG(6);

        private final int code;

        private RecoveryMethod(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public RecoveryMethod fromCode(int code) {
            return RecoveryMethod.values()[code - 1];
        }
    }
}

package com.alibaba.doris.dataserver.store.log.db;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ClumpConfigure {

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public void setReadBufferSize(int readBufferSize) {
        if (readBufferSize < MIN_READ_BUFFER_SIZE) {
            this.readBufferSize = MIN_READ_BUFFER_SIZE;
        } else {
            this.readBufferSize = readBufferSize;
        }
    }

    public int getWriteBufferSize() {
        return writeBufferSize;
    }

    public void setWriteBufferSize(int writeBufferSize) {
        if (writeBufferSize < MIN_WRITE_BUFFER_SIZE) {
            this.writeBufferSize = MIN_WRITE_BUFFER_SIZE;
        } else {
            this.writeBufferSize = writeBufferSize;
        }
    }

    public boolean isWriteDirect() {
        return isWriteDirect;
    }

    public void setWriteDirect(boolean isWriteDirect) {
        this.isWriteDirect = isWriteDirect;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(int maxFileSize) {
        int minFileSize = 10 * 1024;// 10K;
        if (maxFileSize < minFileSize) {
            this.maxFileSize = minFileSize;
        } else {
            this.maxFileSize = maxFileSize;
        }
    }

    public boolean isSyncWrite() {
        return isSyncWrite;
    }

    public void setSyncWrite(boolean isSyncWrite) {
        this.isSyncWrite = isSyncWrite;
    }

    private boolean          isSyncWrite;
    private int              readBufferSize        = MIN_READ_BUFFER_SIZE;
    private int              writeBufferSize       = MIN_WRITE_BUFFER_SIZE;
    private boolean          isWriteDirect;
    private int              maxFileSize           = DEFAULT_MAX_FILE_SIZE;
    private String           path;
    private static final int MIN_WRITE_BUFFER_SIZE = 10 * 1024;            // min buffer 10K
    private static final int MIN_READ_BUFFER_SIZE  = 10 * 1024;            // min buffer 10K
    private static final int DEFAULT_MAX_FILE_SIZE = 1024 * 1024 * 100;    // 100M
}

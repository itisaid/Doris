package com.alibaba.doris.dataserver.store.log.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import org.apache.commons.lang.StringUtils;

import com.alibaba.doris.dataserver.store.log.LogStorageException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class LogFile {

    public enum AccessMode {
        R("r"), RW("rw"), RWS("rws"), RWD("rwd");

        private AccessMode(String accessMode) {
            this.accessMode = accessMode;
        }

        public String value() {
            return accessMode;
        }

        private String accessMode;
    }

    public LogFile(String fileName) {
        this.fileName = fileName;
    }

    public void open(AccessMode accessMode) throws IOException {
        if (StringUtils.isBlank(fileName)) {
            throw new RuntimeException("Invalid file name.");
        }
        checkFile(accessMode);
        file = new RandomAccessFile(fileName, accessMode.value());
        len = file.length();
        this.accessMode = accessMode;
    }

    public void close() {
        if (file != null) {
            try {
                file.close();
            } catch (IOException e) {
                throw new LogStorageException(e);
            }
        }
    }

    public boolean read(ByteBuffer buffer) throws IOException {
        int pos = buffer.position();
        int size = buffer.limit() - pos;
        
        if (offset != file.getFilePointer()) {
            file.seek(offset);
        }

        int bytesRead = file.read(buffer.array(), pos + buffer.arrayOffset(), size);
        if (bytesRead >= 0) {
            buffer.position(pos + bytesRead);
            offset += bytesRead;
            return true;
        } else {
            isEof = true;
            return false;
        }
    }

    public void seek(long newOffset) {
        this.offset = newOffset;
    }

    public long length() {
        if (AccessMode.R == accessMode) {
            return len;
        } else {
            try {
                return file.length();
            } catch (IOException e) {
                throw new LogStorageException(e);
            }
        }
    }

    public void write(ByteBuffer data) throws IOException {
        int pos = data.position();
        int size = data.limit() - pos;

        if (offset != file.getFilePointer()) {
            file.seek(offset);
        }
        
        file.write(data.array(), pos + data.arrayOffset(), size);
        if (needCheckFile) {
            tempBuffer = ByteBuffer.allocate(size);
            file.seek(offset);
            file.read(tempBuffer.array(), 0, size);
            checkData(data, tempBuffer);
        }

        data.position(pos + size);

        offset += size;
    }

    public boolean isEOF() {
        return offset == length() || isEof == true;
    }

    /**
     * 数据写成功后，检查写入的数据是否正确
     * 
     * @param src 待写入的数据。
     * @param target 成功写入文件中的数据。
     */
    private void checkData(ByteBuffer src, ByteBuffer target) {

    }

    private void checkFile(AccessMode accessMode) throws IOException {
        if (AccessMode.R == accessMode) {
            File f = new File(fileName);
            if (!f.exists()) {
                throw new FileNotFoundException(fileName);
            }
        } else if (AccessMode.RW == accessMode) {
            File f = new File(fileName);
            if (!f.exists() && createIfNotExists) {
                if (!f.createNewFile()) {
                    throw new LogStorageException("Create file failed! File name:" + this.fileName);
                }
            }
        }
    }

    private String           fileName;
    private RandomAccessFile file;
    private boolean          needCheckFile     = false;
    private boolean          createIfNotExists = true;
    private ByteBuffer       tempBuffer;
    private long             offset;
    private long             len;
    private boolean          isEof             = false;
    private AccessMode       accessMode;
}

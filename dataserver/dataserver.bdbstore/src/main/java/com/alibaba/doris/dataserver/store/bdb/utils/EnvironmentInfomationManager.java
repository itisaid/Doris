package com.alibaba.doris.dataserver.store.bdb.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.store.bdb.BDBStorageException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class EnvironmentInfomationManager {

    public EnvironmentInfomationManager(String path) {
        this.environmentInformationPath = path;
    }

    public String[] loadAllEnvironmentNames() throws IOException {
        RandomAccessFile envListFile = getRandomAccessFile();
        try {
            long len = envListFile.length();
            int readBytes = 0;
            long offset = 0;
            readBuffer.clear();
            List<String> envList = new ArrayList<String>();
            while ((len - readBytes) > 0) {
                int pos = readBuffer.position();
                int size = readBuffer.limit() - pos;

                envListFile.seek(offset);

                readBytes = envListFile.read(readBuffer.array(), pos + readBuffer.arrayOffset(), size);
                if (readBytes > 0) {
                    readBuffer.position(pos + readBytes);
                    readBuffer.flip();
                }

                while (readBuffer.hasRemaining()) {
                    String name = read(readBuffer);
                    if (null != name) {
                        envList.add(name);
                    } else {
                        break;
                    }
                }

                readBuffer.compact();

                offset += readBytes;
            }

            String[] envNamesArray = new String[envList.size()];
            return envList.toArray(envNamesArray);
        } finally {
            envListFile.close();
        }
    }

    public void saveEnvironmentNames(String[] envNames) {
        RandomAccessFile envListFile = null;
        try {
            envListFile = getRandomAccessFile();
            readBuffer.clear();
            for (String envName : envNames) {
                write(readBuffer, envName);
            }
            readBuffer.flip();

            int pos = readBuffer.position();
            int size = readBuffer.limit() - pos;

            envListFile.seek(0);
            envListFile.write(readBuffer.array(), pos + readBuffer.arrayOffset(), size);
        } catch (IOException e) {
            throw new BDBStorageException("Fail to save environment information.", e);
        } finally {
            if (null != envListFile) {
                try {
                    envListFile.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    private String read(ByteBuffer buffer) {
        int len = buffer.getInt();
        if (len > 1024) {
            throw new BDBStorageException("Read environment file failed. Invalid data length. [" + len + "]");
        }

        byte[] strBytes = new byte[len];
        buffer.get(strBytes);
        return ByteUtils.byteToString(strBytes);
    }

    private void write(ByteBuffer buffer, String envName) {
        buffer.putInt(envName.length());
        buffer.put(ByteUtils.stringToByte(envName));
    }

    private RandomAccessFile getRandomAccessFile() throws IOException {
        String fileName = getEnviromentListFileName();
        File f = new File(fileName);
        if (!f.exists()) {
            f.createNewFile();
        }

        return new RandomAccessFile(fileName, "rw");
    }

    private String getEnviromentListFileName() {
        return environmentInformationPath + File.separatorChar + "bdb_storage.env";
    }

    private String     environmentInformationPath;
    private ByteBuffer readBuffer = ByteBuffer.allocate(1024 * 512); // 10K
}

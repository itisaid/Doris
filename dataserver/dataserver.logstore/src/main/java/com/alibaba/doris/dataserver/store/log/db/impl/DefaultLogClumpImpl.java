package com.alibaba.doris.dataserver.store.log.db.impl;

import java.io.File;
import java.nio.ByteBuffer;

import com.alibaba.doris.dataserver.store.log.db.ClumpConfigure;
import com.alibaba.doris.dataserver.store.log.db.LogClump;
import com.alibaba.doris.dataserver.store.log.db.LogClumpHead;
import com.alibaba.doris.dataserver.store.log.db.ReadWindow;
import com.alibaba.doris.dataserver.store.log.db.WriteWindow;
import com.alibaba.doris.dataserver.store.log.entry.ClumpHeadEntry;
import com.alibaba.doris.dataserver.store.log.utils.LogFileUtil;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DefaultLogClumpImpl implements LogClump {

    public DefaultLogClumpImpl(ClumpConfigure config, String clumpName) {
        this.config = config;
        this.clumpName = clumpName;
        this.no = LogFileUtil.getClumpNoFromClumpName(clumpName);
    }

    public int getNo() {
        return this.no;
    }

    public ClumpHeadEntry getClumpHeadEntry() {
        synchronized (lock) {
            if (null != writeWindow) {
                return writeWindow.getClumpHeadEntry();
            }

            if (head == null) {
                LogClumpHead clumpHeader = new DefaultLogClumpHeadImpl(config, getName());
                clumpHeader.loadLogHeader();
                head = clumpHeader.getClumpHeadEntry();
            }

            return head;
        }
    }

    public long size() {
        synchronized (lock) {
            if (null != writeWindow && writeWindow.isOpen()) {
                return writeWindow.size();
            }

            if (null == file) {
                file = new File(LogFileUtil.generateDataFileName(config.getPath(), clumpName));
            }

            return file.length();
        }
    }

    public String getName() {
        return clumpName;
    }

    /**
     * ReadWindow 每次获取都生成一个新的ReadWindow实例
     */
    public ReadWindow getReadWindow() {
        return new DefaultReadWindowImpl(config, clumpName);
    }

    /**
     * 一个LogClump只能存在一个writewdindow
     */
    public WriteWindow getWriteWindow() {
        return this.getWriteWindow(null);
    }

    /**
     * 一个LogClump只能存在一个writewdindow
     */
    public WriteWindow getWriteWindow(ByteBuffer buffer) {
        synchronized (lock) {
            if (null != writeWindow && writeWindow.isOpen()) {
                return writeWindow;
            }

            writeWindow = new DefaultWriteWindowImpl(config, clumpName, buffer);
            return writeWindow;
        }
    }

    private ClumpConfigure config;
    private String         clumpName;
    private WriteWindow    writeWindow;
    private ClumpHeadEntry head;
    private Object         lock = new Object();
    private int            no;
    private File           file;
}

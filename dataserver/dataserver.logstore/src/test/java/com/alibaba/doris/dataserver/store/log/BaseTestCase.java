package com.alibaba.doris.dataserver.store.log;

import java.io.File;
import java.util.Random;

import junit.framework.TestCase;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.KeyImpl;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.store.log.db.ClumpConfigure;
import com.alibaba.doris.dataserver.store.log.entry.LogEntry;
import com.alibaba.doris.dataserver.store.log.entry.SetLogEntry;
import com.alibaba.doris.dataserver.store.log.utils.LogFileUtil;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public abstract class BaseTestCase extends TestCase {

    protected ClumpConfigure getClumpConfigure() {
        ClumpConfigure config = new ClumpConfigure();
        config.setPath(this.getClass().getClassLoader().getResource("").getPath() + "test_data" + File.separatorChar);
        config.setReadBufferSize(1024 * 512);
        config.setWriteBufferSize(1024 * 512);
        config.setWriteDirect(true);// 不缓存数据直接写入磁盘
        return config;
    }

    protected LogEntry generateLogEntry() {
        StringBuilder sb = new StringBuilder(512);
        for (int i = 0; i < 100; i++) {
            sb.append("1234567890");
        }
        String valueString = sb.toString() + r.nextDouble();
        final Key key = new KeyImpl(1, "key" + r.nextInt(), 0);
        final Value value = new ValueImpl(ByteUtils.stringToByte(valueString), System.currentTimeMillis());
        value.setFlag((short) 10);

        SetLogEntry logEntry = new SetLogEntry(key, value);
        return logEntry;
    }

    protected String getCurrentClassPath() {
        Class<?> clazz = this.getClass();
        String path = clazz.getClassLoader().getResource("").getPath();
        String clazzName = clazz.getName();
        int index = clazzName.lastIndexOf('.');
        if (index > 0) {
            clazzName = clazzName.substring(0, index);
        }

        return path + clazzName.replace('.', File.separatorChar);
    }

    protected void clear(String clumpName) {
        ClumpConfigure config = getClumpConfigure();
        String headFile = LogFileUtil.generateHeadFileName(config.getPath(), clumpName);
        String dataFile = LogFileUtil.generateDataFileName(config.getPath(), clumpName);
        File f = new File(headFile);
        if (f.exists()) {
            f.delete();
        }

        File f2 = new File(dataFile);
        if (f2.exists()) {
            f2.delete();
        }
    }

    private Random r = new Random();
}

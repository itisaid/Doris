package com.alibaba.doris.dataserver.extratools.replica;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.alibaba.doris.common.data.ActionPair;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.dataserver.extratools.replica.action.ExportActionData;
import com.alibaba.doris.dataserver.extratools.replica.action.ImportActionData;
import com.alibaba.doris.dataserver.store.ClosableIterator;
import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.log.db.ClumpConfigure;
import com.alibaba.doris.dataserver.store.log.db.LogClump;
import com.alibaba.doris.dataserver.store.log.db.LogClumpManager;
import com.alibaba.doris.dataserver.store.log.db.WriteWindow;
import com.alibaba.doris.dataserver.store.log.entry.LogEntry;
import com.alibaba.doris.dataserver.store.log.entry.SetLogEntry;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ReplicaTool {

    public int exportData(Storage storage, ExportActionData actionData) {
        checkPath(actionData);
        ClumpConfigure config = getClumpConfigure(actionData.getTarget());
        LogClumpManager manager = new LogClumpManager(config);

        int itemNum = 0;
        WriteWindow writeWindow = null;
        try {
            Iterator<Pair> iterator = storage.iterator();
            LogClump clump = manager.getLogClump();
            while (iterator.hasNext()) {
                Pair pair = iterator.next();
                if (clump.size() > config.getMaxFileSize()) {
                    if (null != writeWindow) {
                        writeWindow.close();
                        writeWindow = null;
                    }

                    clump = manager.getLogClump();
                }

                if (null == writeWindow) {
                    writeWindow = clump.getWriteWindow();
                }

                LogEntry logEntry = new SetLogEntry(pair.getKey(), pair.getValue());
                writeWindow.append(logEntry);
                itemNum++;
            }
        } finally {
            manager.releaseAllResources();
            if (null != writeWindow) {
                writeWindow.close();
            }
        }
        return itemNum;
    }

    public int importData(Storage storage, ImportActionData actionData) {
        File f = new File(actionData.getSource());
        if (!f.isDirectory() || !f.exists()) {
            throw new ReplicaException("The source path is not exists. path=" + actionData.getSource());
        }

        int itemNum = 0;
        boolean isCas = isCas(actionData.getCas());
        ClumpConfigure config = getClumpConfigure(actionData.getSource());
        LogClumpManager manager = new LogClumpManager(config);
        ClosableIterator<Pair> iterator = null;
        try {
            iterator = manager.iterator();
            while (iterator.hasNext()) {
                ActionPair actionPair = (ActionPair) iterator.next();
                if (actionPair.getActionType() == ActionPair.Type.SET) {
                    storage.set(actionPair.getKey(), actionPair.getValue(), isCas);
                    itemNum++;
                }
            }
        } finally {
            if (null != iterator) {
                iterator.close();
            }
            manager.releaseAllResources();
        }

        return itemNum;
    }

    private boolean isCas(String iscas) {
        if (StringUtils.isBlank(iscas)) {
            return false;
        }

        if ("true".equalsIgnoreCase(iscas)) {
            return true;
        }

        return false;
    }

    private void checkPath(ExportActionData actionData) {
        File f = new File(actionData.getTarget());
        if (!f.exists()) {
            if (!f.mkdir()) {
                throw new ReplicaException("Couldn't create path :" + actionData.getTarget());
            }
            actionData.setTarget(f.getPath());
        }
    }

    private ClumpConfigure getClumpConfigure(String path) {
        ClumpConfigure config = new ClumpConfigure();
        config.setPath(path);
        config.setReadBufferSize(1024 * 1024);
        config.setWriteBufferSize(1024 * 1024);
        config.setMaxFileSize(1024 * 1024 * 1024);// 1G
        config.setWriteDirect(false);// 不缓存数据直接写入磁盘
        return config;
    }

}

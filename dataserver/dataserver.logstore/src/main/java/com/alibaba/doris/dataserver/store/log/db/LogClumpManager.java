package com.alibaba.doris.dataserver.store.log.db;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.dataserver.store.ClosableIterator;
import com.alibaba.doris.dataserver.store.log.db.impl.DefaultLogClumpImpl;
import com.alibaba.doris.dataserver.store.log.entry.ClumpHeadEntry;
import com.alibaba.doris.dataserver.store.log.entry.LogEntry;
import com.alibaba.doris.dataserver.store.log.utils.LogFileUtil;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class LogClumpManager {

    public LogClumpManager(ClumpConfigure config) {
        avaliableLogClumps = new TreeSet<LogClump>(new Comparator<LogClump>() {

            public int compare(LogClump o1, LogClump o2) {
                return o1.getNo() - o2.getNo();
            }

        });

        this.config = config;

        listAvaliableLogClumps();
    }

    public LogClump getLogClump() {
        if (null == currentClump) {
            if (avaliableLogClumps.size() > 0) {
                currentClump = avaliableLogClumps.last();
            } else {
                currentClump = new DefaultLogClumpImpl(getClumpConfigure(), getLastFileName());
                avaliableLogClumps.add(currentClump);
            }
            return currentClump;
        }

        if (currentClump.size() > (config.getMaxFileSize() - 1024)) {
            currentClump = new DefaultLogClumpImpl(getClumpConfigure(), getNextFileName());
            avaliableLogClumps.add(currentClump);
        }

        return currentClump;
    }

    /**
     * 列出所有当前可见的Log Clump
     * 
     * @return
     */
    public synchronized LogClump[] listAvaliableLogClumps() {
        avaliableLogClumps.clear();

        String[] clumpNames = LogFileUtil.listAllLogClumpFileName(getClumpConfigure().getPath());
        if (null != clumpNames) {
            List<LogClump> clumps = new ArrayList<LogClump>(clumpNames.length);
            for (String name : clumpNames) {
                clumps.add(new DefaultLogClumpImpl(getClumpConfigure(), name));
            }

            avaliableLogClumps.addAll(clumps);
        }

        LogClump[] clumpArray = new LogClump[avaliableLogClumps.size()];
        return avaliableLogClumps.toArray(clumpArray);
    }

    private LogClump[] listAllLogClumps() {
        String[] clumpNames = LogFileUtil.listAllLogClumpFileName(getClumpConfigure().getPath());

        if (null != clumpNames && clumpNames.length > 0) {
            LogClump[] clumps = new LogClump[clumpNames.length];
            for (int i = 0; i < clumpNames.length; i++) {
                clumps[i] = new DefaultLogClumpImpl(getClumpConfigure(), clumpNames[i]);
            }
            return clumps;
        }

        return EMPTY_LOGCLUMP_ARRAY;
    }

    /**
     * 列出所有LogClumps，剔除当前正在处理的LogClump；
     * 
     * @return
     */
    public LogClump[] listAllNonProcessingLogClumps() {
        String[] clumpNames = LogFileUtil.listAllLogClumpFileName(getClumpConfigure().getPath());

        if (null != clumpNames && clumpNames.length > 1) {
            LogClump[] clumps = new LogClump[clumpNames.length];
            String currentLogClumpName = this.currentClump.getName();
            for (int i = 0; i < clumpNames.length; i++) {
                if (!clumpNames[i].equals(currentLogClumpName)) {
                    clumps[i] = new DefaultLogClumpImpl(getClumpConfigure(), clumpNames[i]);
                }
            }
            return clumps;
        }

        return EMPTY_LOGCLUMP_ARRAY;
    }

    /**
     * 关闭并释放所有的资源
     */
    public synchronized void releaseAllResources() {
        // for (LogClump clump : avaliableLogClumps) {
        // if (clump.isOpen()) {
        // clump.close();
        // }
        // }
    }

    private String getNextFileName() {
        synchronized (this) {
            LogClump lastClump = avaliableLogClumps.last();
            int clumpNo = 0;
            if (null != lastClump) {
                clumpNo = lastClump.getNo() + 1;
            }
            return LogFileUtil.generateClumpName(clumpNo);
        }
    }

    private String getLastFileName() {
        synchronized (this) {
            int clumpNo = 0;
            if (avaliableLogClumps.size() > 0) {
                LogClump lastClump = avaliableLogClumps.last();
                if (null != lastClump) {
                    return lastClump.getName();
                }
            } else {
                clumpNo = LogFileUtil.getMaxClumpNo(config.getPath());
            }
            return LogFileUtil.generateClumpName(clumpNo);
        }
    }

    protected ClumpConfigure getClumpConfigure() {
        return config;
    }

    public boolean deleteLogClump(LogClump logClump) {
        ClumpHeadEntry head = logClump.getClumpHeadEntry();
        if (head.getVnodeNum() > 0) {
            return false;
        }

        LogFileUtil.deleteClumpFile(getClumpConfigure().getPath(), logClump.getName());
        return true;
    }

    public synchronized boolean deleteLogClumps(List<Integer> vnodeList) {
        Iterator<LogClump> itr = avaliableLogClumps.iterator();
        boolean isSuccess = false;
        ByteBuffer buffer = ByteBuffer.allocate(this.config.getWriteBufferSize());
        while (itr.hasNext()) {
            LogClump clump = itr.next();
            WriteWindow writeWindow = clump.getWriteWindow();
            try {
                if (deleteLogClumpDataByVnodes(clump, vnodeList, buffer)) {
                    itr.remove();
                    isSuccess = true;
                }
            } catch (WriteWindowClosedException e) {
                // 如果出现WindowClosedException？？？
                writeWindow = clump.getWriteWindow();
                if (deleteLogClumpDataByVnodes(clump, vnodeList, buffer)) {
                    itr.remove();
                    isSuccess = true;
                }
            } finally {
                if (clump != currentClump) {
                    writeWindow.close();
                }
            }
        }

        return isSuccess;
    }

    /**
     * @param clump
     * @param vnodeList
     * @return true：表示logclump对应的文件已经被删除；
     */
    public boolean deleteLogClumpDataByVnodes(LogClump clump, List<Integer> vnodeList, ByteBuffer buffer) {
        if (null == clump) {
            return false;
        }

        WriteWindow writeWindow = clump.getWriteWindow(buffer);
        try {
            if (writeWindow.deleteByVnodes(vnodeList)) {
                if (clump != currentClump) {
                    writeWindow.close();
                    if (deleteLogClump(clump)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Delete log clump file. Clump name is " + clump.getName());
                        }
                        return true;
                    }
                } else {
                    return true;
                }
            } 
            
        } finally {
            writeWindow.close();
        }
        return false;
    }

    public ClosableIterator<Pair> iterator() {
        return new LogClumpsIterator(listAllLogClumps());
    }

    public ClosableIterator<Pair> iterator(final List<Integer> vnodeList) {
        final Map<Integer, Integer> vnodeMap = new HashMap<Integer, Integer>(vnodeList.size());
        for (Integer vnode : vnodeList) {
            vnodeMap.put(vnode, vnode);
        }
        
        LogClump[] clumpList = listLogClumpByVnodes(vnodeList);
        return new LogClumpsIterator(clumpList) {

            @Override
            protected boolean doFilter(LogEntry logEntry) {
                if (null == logEntry) {
                    return false;
                }
                return vnodeMap.get(logEntry.getVnode()) != null;
            }
        };
    }

    public LogClump[] listLogClumpByVnodes(List<Integer> vnodeList) {
        LogClump[] clumpArray = listAllLogClumps();

        List<LogClump> needIteratorClumpArray = new ArrayList<LogClump>(vnodeList.size());
        for (LogClump clump : clumpArray) {
            ClumpHeadEntry headEntry = clump.getClumpHeadEntry();
            Iterator<Integer> nodeItr = headEntry.getVnodes();
            while (nodeItr.hasNext()) {
                if (vnodeList.contains(nodeItr.next())) {
                    needIteratorClumpArray.add(clump);
                    break;
                }
            }
        }

        return needIteratorClumpArray.toArray(new LogClump[needIteratorClumpArray.size()]);
    }

    private LogClump                currentClump;
    private ClumpConfigure          config;
    private SortedSet<LogClump>     avaliableLogClumps;
    private static final LogClump[] EMPTY_LOGCLUMP_ARRAY = new LogClump[0];
    private static final Logger     logger               = LoggerFactory.getLogger(LogClumpManager.class);
}

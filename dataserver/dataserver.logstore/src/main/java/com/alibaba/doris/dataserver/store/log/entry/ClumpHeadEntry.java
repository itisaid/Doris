package com.alibaba.doris.dataserver.store.log.entry;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.alibaba.doris.common.util.ConcurrentHashSet;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ClumpHeadEntry {

    public ClumpHeadEntry() {
        this.vnodeSet = new ConcurrentHashSet<Integer>();
        this.isChanged = false;
    }

    public int getLogFileVersion() {
        return logFileVersion;
    }

    public void setLogFileVersion(int logFileVersion) {
        this.logFileVersion = logFileVersion;
    }

    public int getVnodeNum() {
        return vnodeSet.size();
    }

    public boolean addVnode(int vnode) {
        if (vnodeSet.add(new Integer(vnode))) {
            isChanged = true;
            return true;
        }
        return false;
    }

    public boolean removeVnode(int vnode) {
        if (vnodeSet.remove(new Integer(vnode))) {
            isChanged = true;
            return true;
        }
        return false;
    }

    public boolean removeVnodes(List<Integer> vnodeList) {
        if (vnodeList == null) {
            throw new IllegalArgumentException("vnodeList is null");
        }

        if (vnodeSet.removeAll(vnodeList)) {
            isChanged = true;
            return true;
        }
        return false;
    }

    public boolean isChanged() {
        return isChanged;
    }

    /**
     * TODO: not safety, The "isChanged" variable have problem. HAHA.
     * 
     * @param isChanged
     */
    public void setChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    public Iterator<Integer> getVnodes() {
        return vnodeSet.iterator();
    }

    private int              logFileVersion          = DEFAULT_LOGFILE_VERSION;
    private Set<Integer>     vnodeSet;
    private volatile boolean isChanged;
    private static final int DEFAULT_LOGFILE_VERSION = 1;
}

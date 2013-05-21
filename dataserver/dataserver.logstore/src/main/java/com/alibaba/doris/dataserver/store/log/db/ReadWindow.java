package com.alibaba.doris.dataserver.store.log.db;

import com.alibaba.doris.dataserver.store.log.entry.LogEntry;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface ReadWindow {

    /**
     * 判断是否到达当前log集中的最后一条记录。
     * 
     * @return
     */
    public boolean hasNext();

    /**
     * 获取下一条记录。
     * 
     * @return
     */
    public LogEntry next();

    /**
     * 获取文件大小
     * 
     * @return
     */
    public long size();

    public void close();
}

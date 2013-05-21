package com.alibaba.doris.dataserver.store.log.db;

import com.alibaba.doris.dataserver.store.log.entry.ClumpHeadEntry;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface LogClumpHead {

    public void open();

    public ClumpHeadEntry getClumpHeadEntry();

    public void flush();

    public void close();

    public void loadLogHeader();
}

package com.alibaba.doris.dataserver.store.log.db;

import java.util.List;

import com.alibaba.doris.dataserver.store.log.entry.ClumpHeadEntry;
import com.alibaba.doris.dataserver.store.log.entry.LogEntry;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface WriteWindow {

    /**
     * 通过写入窗口，向Log DB中插入一条Log记录。
     * 
     * @param logEntry
     */
    public void append(LogEntry logEntry);

    public void append(LogEntry[] logEntryArray);

    /**
     * 将buffer中的数据刷新到磁盘上；
     */
    public void flushAll();

    /**
     * 获取当前write window对应数据文件的大小；
     * 
     * @return
     */
    public long size();

    /**
     * 关闭当前write window，并释放文件句柄；
     * 
     * @return
     */
    public boolean close();

    /**
     * 获取当前Log文件的head文件信息；
     * 
     * @return
     */
    public ClumpHeadEntry getClumpHeadEntry();

    /**
     * 删除当前Log文件中指定虚拟节点下面的数据数据；<BR>
     * 备注：只在head文件中删除对应的虚拟节点数据，并不清楚数据文件中的数据；<BR>
     * 当head文件中不再存在任何虚拟节点数据时，head文件和data文件会一并清除；
     * 
     * @param vnodeList
     * @return
     */
    public boolean deleteByVnodes(List<Integer> vnodeList);

    /**
     * 检查当前write window对象是否处于打开状态；
     * 
     * @return
     */
    public boolean isOpen();
}

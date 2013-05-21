package com.alibaba.doris.dataserver.store.log.db;

import java.nio.ByteBuffer;

import com.alibaba.doris.dataserver.store.log.entry.ClumpHeadEntry;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface LogClump {

    /**
     * 获取DB对应的头部文件信息
     * 
     * @return
     */
    public ClumpHeadEntry getClumpHeadEntry();

    /**
     * 获取查询Log DB数据的游标
     * 
     * @return
     */
    public ReadWindow getReadWindow();

    /**
     * 获取插入数据的游标。
     * 
     * @return
     */
    public WriteWindow getWriteWindow();

    /**
     * 获取插入数据的写入窗口，指定写入数据共享buffer。
     * 
     * @param buffer
     * @return
     */
    public WriteWindow getWriteWindow(ByteBuffer buffer);

    /**
     * 获取当前Clump所占的空间大小。
     * 
     * @return
     */
    public long size();

    /**
     * 获取当前clump的编号
     * 
     * @return
     */
    public int getNo();

    /**
     * 获取当前clump的名称
     * 
     * @return
     */
    public String getName();

}

package com.alibaba.doris.dataserver.event.server;

import com.alibaba.doris.dataserver.event.EventListener;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface MigrateEventListener extends EventListener {

    /**
     * 当DataServer启动迁移时，会触发本事件。
     */
    public void onStartMigrating();

    /**
     * 当DataServer迁移完成时，会触发本事件。
     */
    public void onStopMigrating();

    /**
     * 迁移中断事件
     */
    public void onInterruptMigrating();
}

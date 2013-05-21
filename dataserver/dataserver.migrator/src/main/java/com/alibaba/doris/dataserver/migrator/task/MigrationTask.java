package com.alibaba.doris.dataserver.migrator.task;

import com.alibaba.doris.common.MigrateTypeEnum;
import com.alibaba.doris.common.migrate.NodeMigrateStatus;
import com.alibaba.doris.dataserver.migrator.action.MigrationActionData;
import com.alibaba.doris.dataserver.migrator.connection.ConnectionManager;

/*
 * 迁移任务接口，抽象出所有迁移任务的对外公共接口；
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface MigrationTask {

    /**
     * 获取当前迁移任务的状态；
     * 
     * @see NodeMigrateStatus
     * @return
     */
    public NodeMigrateStatus getMigrateStatus();

    /**
     * 获取当前迁移任务类型；
     * 
     * @see MigrateTypeEnum
     * @return
     */
    public MigrateTypeEnum getMigrateType();

    /**
     * 设置当前任务的状态；
     * 
     * @param b
     */
    public void setFinish(boolean b);

    /**
     * 获取当前迁移任务的标识符，能够唯一标识当前任务的key；
     * 
     * @return
     */
    public String getTaskKey();

    /**
     * 获取当前task的名称；
     * 
     * @return
     */
    public String getTaskName();

    /**
     * 获取当前task的启动参数；
     * 
     * @return
     */
    public MigrationActionData getMigrationActionData();

    /**
     * 一次集群迁移整体结束，本Node回复NORMAL状态
     */
    public void allFinish();

    /**
     * 准备启动迁移任务；
     */
    public boolean prepareTask();

    /**
     * 通过信号量通知数据数据清理开始.
     */
    public void dataCleanStart();

    /**
     * 检查任务是否已经取消
     * 
     * @return
     */
    public boolean isCancel();

    /**
     * 取消一个正在迁移的任务；
     */
    public void cancel();

    /**
     * 检查任务是否完成；
     * 
     * @return
     */
    public boolean isFinished();

    /**
     * 执行数据清理
     */
    public void dataClean();

    /**
     * 获取任务的迁移进度；返回0-100的值，返回-1表示迁移还未开始；
     * 
     * @return
     */
    public int getProgress();

    /**
     * 获取当前迁移任务的启动时间；
     * 
     * @return
     */
    public long getStartTime();

    /**
     * 获取指定vnode是否属于当前迁移任务范围内的vnode；
     * 
     * @return
     */
    public String getProxyTarget(int vnode);

    /**
     * 获取当前task对应的代理节点的连接；
     * 
     * @return
     */
    public ConnectionManager getConnectionManager();
}

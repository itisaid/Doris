package com.alibaba.doris.admin.service.common.migrate.status;

/**
 * 迁移状态监控回调,供监控线程调用
 * 
 * @author frank
 */
public interface MigrateStatusCallback {

    /**
     * 迁移结束
     */
    void finishAll();

    /**
     * 监控到迁移错误
     */
    void notifyError();

}

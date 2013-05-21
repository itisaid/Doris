package com.alibaba.doris.dataserver.store.log.db;

/*
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public interface LogCommand {

    /**
     * 标识当前Log命令是否成功执行；
     * 
     * @return
     */
    boolean isSuccess();

    /**
     * 获取当前命令的当前执行结果；
     * 
     * @return
     */
    public void waitingResult();

    /**
     * 通知等待线程,命令执行结束；
     * 
     * @param isSuccess
     */
    public void complete();

    /**
     * 设置当前Log命令的执行结果，成功或者失败；
     * 
     * @param isSuccess
     */
    void setSuccess(boolean isSuccess);

    /**
     * 获取当前Log命令的类型；
     * 
     * @return
     */
    Type getType();

    /**
     * 定义可能存在的Log命令类型，分为以下几类：<BR>
     * 1. Append: 表示需要追加一条log记录的命令；例如：set,delete,checkpoint等等；<br>
     * 2. Delete：表示当前为删除Log数据的命令。DELETE_BY_VNODES:根据虚拟节点来删除数据；<br>
     * DELETE_BY_TIMESTAMP:根据时间戳来删除数据，删除所有该时间戳以前的记录。<br>
     * 3. Exit: 退出命令，处理线程接收到该命令会终止当前线程；
     * 
     * @author ajun
     */
    enum Type {
        APPEND, DELETE_BY_VNODES, DELETE_BY_TIMESTAMP, EXIT
    }
}

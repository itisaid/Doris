package com.alibaba.doris.common.adminservice;

import com.alibaba.doris.common.MigrateStatusEnum;

/**
 * 迁移结束后处理过程报告服务
 * @author frank
 *
 */
public interface PostMigrateReportService {
    /**
     * data server 向admin server报告迁移结束后处理过程状态
     * 
     * @param physicalId 后处理中节点物理编号
     * @param schedule 后处理迁移进度百分比整数值
     * @param message 附加信息
     * @return
     */
    String report(String physicalPort, int schedule,MigrateStatusEnum status,String message);

}

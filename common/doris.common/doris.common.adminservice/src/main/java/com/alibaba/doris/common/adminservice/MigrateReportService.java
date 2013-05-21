package com.alibaba.doris.common.adminservice;

import com.alibaba.doris.common.MigrateStatusEnum;

/**
 * 迁移状态报告service
 * 
 * @author frank
 */
public interface MigrateReportService {

    /**
     * data server 向admin server报告迁移状态
     * 
     * @param srcPhysicalId 迁出的节点物理编号
     * @param targetPhysicalId 迁入的节点物理编号（如果是扩容迁移，该编号为null）
     * @param schedule 迁移进度，未开始：0;完成：100;迁移中：介于1到100的整数;迁移发生错误：-1
     * @return
     */
    String report(String srcPhysicalId, String targetPhysicalId, int schedule,MigrateStatusEnum status,String message);
}

package com.alibaba.doris.common.adminservice;

import com.alibaba.doris.common.ConsistentErrorType;

/**
 * 向admin报告一致性问题
 * 
 * @author zhw
 */
public interface ConsistentErrorReportService {

    /**
     * @param namespaceId
     * @param key
     * @param phisicalIps 物理节点ip列表（带端口号），中间以";"分隔
     * @param exceptionMsg 异常消息
     * @param errorType 出错类型
     * @param timestamp， value的时间戳
     * @return
     */
    Boolean report(int namespaceId, String key, String phisicalIps, String exceptionMsg, ConsistentErrorType errorType,
                   String timestamp);

}

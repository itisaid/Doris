package com.alibaba.doris.common.adminservice;

import java.util.List;
import java.util.Map;

public interface CommonConfigService {
    
    /**
     * 取得比当前版本号更新的配置
     */
    Map<String, String> getConfig(Map<String, Long> paras);
    
    /**
     * 强制刷新配置
     * 
     */
    Map<String, String> getConfig(List<String> paras);
}

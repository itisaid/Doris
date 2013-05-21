package com.alibaba.doris.admin.service;

/**
 * 配置变量管理服务
 * 
 * @author frank
 */
public interface PropertiesService {

    String getProperty(String key);
    
    <T> T getProperty(String key, Class<T> type);
    
    <T> T getProperty(String key, Class<T> type, T defaultValue);

}

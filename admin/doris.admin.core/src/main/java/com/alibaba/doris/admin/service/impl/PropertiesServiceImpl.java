package com.alibaba.doris.admin.service.impl;

import java.util.Map;

import com.alibaba.doris.admin.service.PropertiesService;

public class PropertiesServiceImpl implements PropertiesService {

    private Map<String, String> propertisMap;

    public String getProperty(String key) {
        if (propertisMap == null || propertisMap.isEmpty()) {
            return null;
        }
        return propertisMap.get(key);
    }

    public void setPropertisMap(Map<String, String> propertisMap) {
        this.propertisMap = propertisMap;
    }

    public <T> T getProperty(String key, Class<T> type) {
        String strValue = propertisMap.get(key);
        return (T) ValueParseUtil.parseStringValue(strValue, type);
    }

    public <T> T getProperty(String key, Class<T> type, T defaultValue) {
        String strValue = propertisMap.get(key);
        T value = ValueParseUtil.parseStringValue(strValue, type, false);
        return value == null ? defaultValue : value;
    }

}

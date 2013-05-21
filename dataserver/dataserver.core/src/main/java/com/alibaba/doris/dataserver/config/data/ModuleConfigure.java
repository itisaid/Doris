package com.alibaba.doris.dataserver.config.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.doris.dataserver.config.DataServerConfigure;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ModuleConfigure {

    public void addParam(String name, String value) {
        params.put(name, value);
    }

    public String getParam(String name) {
        return params.get(name);
    }

    public int getParamAsInt(String name, int defaultValue) {
        String value = params.get(name);
        try {
            if (StringUtils.isNotBlank(value)) {
                return Integer.valueOf(value);
            }
        } catch (Throwable e) {
            ;
        }
        return defaultValue;
    }

    public long getParamAsLong(String name, long defaultValue) {
        String value = params.get(name);
        try {
            if (StringUtils.isNotBlank(value)) {
                return Long.valueOf(value);
            }
        } catch (Throwable e) {
            ;
        }
        return defaultValue;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FilterConfigure> getFilterConfigList() {
        return filterConfigList;
    }

    public void setFilterConfigList(List<FilterConfigure> filterConfigList) {
        this.filterConfigList = filterConfigList;
    }

    public FilterConfigure getFilterConfigure(String filterClassName) {
        for (FilterConfigure conf : filterConfigList) {
            if (conf.getClassName().equals(filterClassName)) {
                return conf;
            }
        }
        return null;
    }

    public DataServerConfigure getDataServerConfigure() {
        return dataServerConfigure;
    }

    public void setDataServerConfigure(DataServerConfigure dataServerConfigure) {
        this.dataServerConfigure = dataServerConfigure;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private List<FilterConfigure> filterConfigList;
    private DataServerConfigure   dataServerConfigure;
    private String                className;
    private String                description;
    private String                name;
    private Map<String, String>   params = new HashMap<String, String>();
}

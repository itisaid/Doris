package com.alibaba.doris.dataserver.config.data;

import java.util.List;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class NetworkModuleConfigure extends ModuleConfigure {

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

    private List<FilterConfigure> filterConfigList;
}

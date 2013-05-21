package com.alibaba.doris.dataserver.core;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.dataserver.config.data.FilterConfigure;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class RequestFilterChainFactory {

    public RequestFilterChainFactory() {
        filterChian = new DefaultRequestFilterChian();
    }

    public void loadRequestFiltersFromConfigure(List<FilterConfigure> filterConfigList) {
        for (FilterConfigure filterConfig : filterConfigList) {
            RequestFilter filter = createFilter(filterConfig);
            filterChian.addFilter(filter);
        }
    }

    private RequestFilter createFilter(FilterConfigure filterConfigList) {
        String filterClassName = filterConfigList.getClassName();
        RequestFilter filter = null;
        try {
            if (StringUtils.isNotBlank(filterClassName)) {
                ClassLoader classLoader = this.getClass().getClassLoader();
                Class<?> clazz = classLoader.loadClass(filterClassName);
                filter = (RequestFilter) clazz.newInstance();
            }
        } catch (Exception e) {
            logger.error("Create filter failed! The reason is :" + e.getMessage(), e);
        }
        return filter;
    }

    public RequestFilterChian getFilterChian() {
        return filterChian;
    }

    private RequestFilterChian  filterChian;
    private static final Logger logger = LoggerFactory.getLogger(RequestFilterChainFactory.class);
}

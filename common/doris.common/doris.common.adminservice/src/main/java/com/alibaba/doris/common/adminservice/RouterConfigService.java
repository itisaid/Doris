package com.alibaba.doris.common.adminservice;


public interface RouterConfigService {

    String getRouteConfig();
    
    String getRouteConfig(long version);
}

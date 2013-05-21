package com.alibaba.doris.admin.service;

import com.alibaba.doris.admin.dataobject.RouterConfigInstanceDO;

public interface RouteConfigService {

    public RouterConfigInstanceDO loadLatestConfigInstance();

    public int insertConfigInstance(RouterConfigInstanceDO configInstanceDO);
    
}

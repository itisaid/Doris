package com.alibaba.doris.admin.service.impl;

import org.springframework.stereotype.Service;

import com.alibaba.doris.admin.dao.ConfigInstanceDao;
import com.alibaba.doris.admin.dataobject.RouterConfigInstanceDO;
import com.alibaba.doris.admin.service.RouteConfigService;

@Service
public class RouteConfigServiceImpl implements RouteConfigService {
    
    private ConfigInstanceDao configInstanceDao = null;

    public void setConfigInstanceDao(ConfigInstanceDao configInstanceDao) {
        this.configInstanceDao = configInstanceDao;
    }

    public RouterConfigInstanceDO loadLatestConfigInstance() {
        return configInstanceDao.loadLatestConfigInstance();
    }

    public int insertConfigInstance(RouterConfigInstanceDO configInstanceDO) {
        return configInstanceDao.insertConfigInstance(configInstanceDO);
    }
    
    

}

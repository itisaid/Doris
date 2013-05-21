package com.alibaba.doris.admin.service.impl;

import org.springframework.stereotype.Service;

import com.alibaba.doris.admin.dao.VirtualNodeDao;
import com.alibaba.doris.admin.service.VirtualNodeService;

@Service
public class VirtualNodeServiceImpl implements VirtualNodeService {

    private VirtualNodeDao virtualNodeDao;

    public void setVirtualNodeDao(VirtualNodeDao virtualNodeDao) {
        this.virtualNodeDao = virtualNodeDao;
    }

    public int getVirtualNodeNum() {
        return virtualNodeDao.getVirtualNodeNumber();
    }

}

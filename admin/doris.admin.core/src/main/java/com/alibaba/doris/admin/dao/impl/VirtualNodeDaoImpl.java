package com.alibaba.doris.admin.dao.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.alibaba.doris.admin.dao.VirtualNodeDao;

public class VirtualNodeDaoImpl extends SqlMapClientDaoSupport implements VirtualNodeDao {

    public int getVirtualNodeNumber() {
        return (Integer) getSqlMapClientTemplate().queryForObject("VirtualNode.getVirtualNodeNum");
    }

}

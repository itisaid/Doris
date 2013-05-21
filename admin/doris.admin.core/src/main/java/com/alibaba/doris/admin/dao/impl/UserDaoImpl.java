package com.alibaba.doris.admin.dao.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.alibaba.doris.admin.dao.UserDao;
import com.alibaba.doris.admin.dataobject.UserDO;

public class UserDaoImpl extends SqlMapClientDaoSupport implements UserDao {

    public UserDO findUserByName(String userName) {
        return (UserDO) getSqlMapClientTemplate().queryForObject("User.getUserByName", userName);
    }

}

package com.alibaba.doris.admin.service.impl;

import org.springframework.stereotype.Service;

import com.alibaba.doris.admin.dao.UserDao;
import com.alibaba.doris.admin.dataobject.UserDO;
import com.alibaba.doris.admin.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private UserDao userDao = null;

    public UserDO findUserByName(String userName) {
        return userDao.findUserByName(userName);
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    
}

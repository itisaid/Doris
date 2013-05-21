package com.alibaba.doris.admin.service;

import com.alibaba.doris.admin.dataobject.UserDO;

public interface UserService {

    public UserDO findUserByName(String userName);

}

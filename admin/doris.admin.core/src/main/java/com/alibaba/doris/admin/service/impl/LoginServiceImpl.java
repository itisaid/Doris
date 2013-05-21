package com.alibaba.doris.admin.service.impl;

import com.alibaba.doris.admin.service.LoginService;

public class LoginServiceImpl implements LoginService{

    public boolean login(String name, String password) {
        return "doris".equals(name) && "doris".equals(password);
    }

}

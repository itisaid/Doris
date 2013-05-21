package com.alibaba.doris.common.adminservice;

public interface UserAuthService {
    public int getUserAuth(String userName, String password);
}

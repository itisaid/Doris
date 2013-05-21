package com.alibaba.doris.dataserver.store.mysql;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class MysqlStorageConfigure {

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String url;
    private String userName;
    private String password;
}

package com.alibaba.doris.admin.dataobject;

import java.io.Serializable;
import java.util.Date;

/**
 * @project :Doris
 * @author : len.liu
 * @datetime : 2011-6-9 下午03:36:39
 * @version :0.1
 * @Modification:
 */
public class UserDO implements Serializable{

    /**
     */
    private static final long serialVersionUID = -5699059953242267851L;

    private int    id;

    private String name;

    private String password;

    private Date   gmtCreate;

    private Date   gmtModified;

    private int    privilege;

    private boolean  logined;

    public boolean isLogined() {
        return logined;
    }

    public void setLogined(boolean logined) {
        this.logined = logined;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrivilege() {
        return privilege;
    }

    public void setPrivilege(int privilege) {
        this.privilege = privilege;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}

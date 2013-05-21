package com.alibaba.doris.admin.service;

/**
 * @project :
 * @author : len.liu
 * @datetime : 2011-7-4 下午06:05:26
 * @version :
 * @Modification:
 */
public interface AdminService {

    boolean isMasterAdmin(String ip);

    boolean isMasterAdmin();
}

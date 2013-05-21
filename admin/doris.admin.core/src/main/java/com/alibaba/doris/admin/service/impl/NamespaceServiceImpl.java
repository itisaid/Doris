package com.alibaba.doris.admin.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.doris.admin.dao.NamespaceDao;
import com.alibaba.doris.admin.dataobject.NamespaceDO;
import com.alibaba.doris.admin.service.NamespaceService;

/**
 * @project :Doris
 * @author : len.liu
 * @datetime : 2011-5-14 下午08:58:15
 * @version :0.1
 * @Modification:
 */
@Service
public class NamespaceServiceImpl implements NamespaceService {

    private NamespaceDao namespaceDao;

    public void addNamespace(NamespaceDO namespaceDO) {
        namespaceDao.addNamespace(namespaceDO);
    }

    public List<NamespaceDO> queryAllNamespaces() {

        return namespaceDao.queryAllNamespaces();
    }

    public NamespaceDO queryNamespaceById(int id) {

        return namespaceDao.queryNamespaceById(id);
    }

    public NamespaceDO queryNamespaceByName(String name) {

        return namespaceDao.queryNamespaceByName(name);
    }

    public List<NamespaceDO> queryUnUsableNamespaces() {

        return namespaceDao.queryUnUsableNamespaces();
    }

    public List<NamespaceDO> queryUsableNamespaces() {

        return namespaceDao.queryUsableNamespaces();
    }

    public void setNamespaceDao(NamespaceDao namespaceDao) {
        this.namespaceDao = namespaceDao;
    }

    public void deleteNamespaceById(int id) {
        namespaceDao.deleteNamespaceById(id);

    }

    public void deleteNamespaceByName(String name) {
        namespaceDao.deleteNamespaceByName(name);

    }

}

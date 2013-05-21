package com.alibaba.doris.admin.dao.impl;

import java.util.List;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.alibaba.doris.admin.dao.NamespaceDao;
import com.alibaba.doris.admin.dataobject.NamespaceDO;

/**
 * @project :Doris
 * @author : len.liu
 * @datetime : 2011-5-14 下午08:58:15
 * @version :0.1
 * @Modification:
 */
public class NamespaceDaoImpl extends SqlMapClientDaoSupport implements NamespaceDao {

    // XXME Methods need to be implements
    public void addNamespace(NamespaceDO namespaceDO) {
        getSqlMapClientTemplate().insert("Namespace.addNamespace", namespaceDO);
    }

    @SuppressWarnings("unchecked")
    public List<NamespaceDO> queryAllNamespaces() {
        return (List<NamespaceDO>) getSqlMapClientTemplate().queryForList("Namespace.queryAllNamespaces");
    }

    public NamespaceDO queryNamespaceById(int id) {
        return (NamespaceDO) getSqlMapClientTemplate().queryForList("Namespace.queryNamespaceById", id);
    }

    public NamespaceDO queryNamespaceByName(String name) {
        return (NamespaceDO) getSqlMapClientTemplate().queryForObject("Namespace.queryNamespaceByName", name);
    }

    @SuppressWarnings("unchecked")
    public List<NamespaceDO> queryUnUsableNamespaces() {
        return (List<NamespaceDO>) getSqlMapClientTemplate().queryForList("Namespace.queryUnUsableNamespaces");
    }

    @SuppressWarnings("unchecked")
    public List<NamespaceDO> queryUsableNamespaces() {
        return (List<NamespaceDO>) getSqlMapClientTemplate().queryForList("Namespace.queryUsableNamespaces");
    }

    public void deleteNamespaceById(int id) {
        getSqlMapClientTemplate().delete("Namespace.deleteNamespacesById", id);

    }

    public void deleteNamespaceByName(String name) {
        getSqlMapClientTemplate().delete("Namespace.deleteNamespacesByName", name);

    }
}

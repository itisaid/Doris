package com.alibaba.doris.admin.dao;

import java.util.List;

import com.alibaba.doris.admin.dataobject.NamespaceDO;

/**
 * @project :Doris
 * @author : len.liu
 * @datetime : 2011-5-14 下午08:58:15
 * @version :0.1
 * @Modification:
 */
public interface NamespaceDao {

    /**
     * 新增一个Namespace
     * 
     * @param namespaceDO
     */
    void addNamespace(NamespaceDO namespaceDO);

    /**
     * 返回所有的Namespace列表
     */
    List<NamespaceDO> queryAllNamespaces();

    /**
     * 返回所有可用的Namespace列表
     */
    List<NamespaceDO> queryUsableNamespaces();

    /**
     * 返回不可用的Namespace列表
     */
    List<NamespaceDO> queryUnUsableNamespaces();

    /**
     * 依据Namespace名称返回Namespace对象
     * 
     * @param name ： Namespace名称
     */
    NamespaceDO queryNamespaceByName(String name);

    /**
     * 根据Namespace ID返回Namespace对象
     * 
     * @param id
     */
    NamespaceDO queryNamespaceById(int id);

    /**
     * 根据Namespace Name删除Namespace
     * 
     * @param name
     */
    void deleteNamespaceByName(String name);

    /**
     * 根据Namespace ID删除Namespace
     * 
     * @param id
     */
    void deleteNamespaceById(int id);
}

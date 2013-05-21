package com.alibaba.doris.common.router.service;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.doris.algorithm.RouteAlgorithm;
import com.alibaba.doris.common.StoreNode;

/**
 * 这一组变量需要同时完成更新，因此封装到一个class ，这里面的成员不许有读写并发操作
 * 
 * @project :
 * @author : len.liu
 * @datetime : 2011-5-4 下午05:23:22
 * @version :
 * @Modification:
 */

public class RouterListContainer {

    // 以下一组list按index有序存在对应关系，index表示group
    private List<RouteAlgorithm>       vpmrList = new ArrayList<RouteAlgorithm>(); // 每个group一个路由表
    private List<List<StoreNode>> mainStoreNodeList;                    // 每个group一组物理节点

    private RouteAlgorithm             backupVpmr;                           // 临时失效备份存储时使用的路由表
    private List<StoreNode>       backupStoreNodeList;                  // 临时失效存储物理节点

    public List<RouteAlgorithm> getVpmrList() {
        return vpmrList;
    }

    public void setVpmrList(List<RouteAlgorithm> vpmrList) {
        this.vpmrList = vpmrList;
    }

    public List<List<StoreNode>> getMainStoreNodeList() {
        return mainStoreNodeList;
    }

    public void setMainStoreNodeList(List<List<StoreNode>> mainStoreNodeList) {
        this.mainStoreNodeList = mainStoreNodeList;
    }


    public RouteAlgorithm getBackupVpmr() {
        return backupVpmr;
    }

    public void setBackupVpmr(RouteAlgorithm backupVpmr) {
        this.backupVpmr = backupVpmr;
    }

    public List<StoreNode> getBackupStoreNodeList() {
        return backupStoreNodeList;
    }

    public void setBackupStoreNodeList(List<StoreNode> backupStoreNodeList) {
        this.backupStoreNodeList = backupStoreNodeList;
    }
    
    @Override
	public String toString() {
		return "main nodes:" + mainStoreNodeList + " backup nodes:"
				+ backupStoreNodeList;
	}
}

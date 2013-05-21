package com.alibaba.doris.algorithm.vpm;

import java.util.Properties;

import com.alibaba.doris.algorithm.KetamaHashFunction;
import com.alibaba.doris.algorithm.RouteAlgorithm;

/**
 * Doris路由器原型
 * 
 * @author frank
 */
public class VpmRouterAlgorithm implements RouteAlgorithm {

    private int        vnn          = 10000;                   // 虚拟节点数
    private int        pnn;                                    // 物理节点数
    private int[]      vpm;
    KetamaHashFunction hashFunction = new KetamaHashFunction();
    private Properties configProperties;

    /**
     * @param pnn 物理节点数
     */
    public VpmRouterAlgorithm(int pnn, int vnn) {
        if (vnn >= 1 && vnn < 1000000) {
            this.vnn = vnn;
        }
        setNodeCount(pnn);
        init();
    }

    /**
     * 根据Key获得物理节点
     * 
     * @param key
     * @return
     */
    public Integer getNodeByKey(String key) {
        return mapPn(getVirtualByKey(key));
    }

    /**
     * 根据key获得虚拟节点
     * 
     * @param key
     * @return
     */
    public Integer getVirtualByKey(String key) {
        return vHash(simpleHashCode(key));
    }

    /**
     * 一种简单hashcode
     * 
     * @param key
     * @return
     */
    private int simpleHashCode(String key) {
        return hashFunction.hash(key);
    }

    /**
     * 虚拟节点上的hash
     * 
     * @param hashCode
     * @return 虚拟节点
     */
    private int vHash(int hashCode) {

        int hash = hashCode % vnn;
        return hash < 0 ? -hash : hash;
    }

    /**
     * 根据虚拟节点获得物理节点
     * 
     * @param vp
     * @return
     */
    private int mapPn(int vn) {

        return vpm[vn];

    }

    /**
     * set node count.
     */
    public void setNodeCount(int pnn) {
        if (pnn <= 0) {
            pnn = 1;
        }
        if (pnn > vnn) {
            pnn = vnn;
        }
        this.pnn = pnn;
    }

    public void setConfigProperties(Properties configProperties) {
        this.configProperties = configProperties;
    }

    public void init() {
        // this.vpm = RouterMap.getVpRoutMap1D(vnn, pnn);
        // this.vpm=RouterMapping.makeRouterMap(pnn,vnn);
        this.vpm = VpmMapping.makeVpm(pnn, vnn);
    }
}

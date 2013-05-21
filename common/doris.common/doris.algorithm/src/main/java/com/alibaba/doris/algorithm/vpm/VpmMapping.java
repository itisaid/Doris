package com.alibaba.doris.algorithm.vpm;

import java.util.ArrayList;
import java.util.List;
/**
 * Doris 路由算法核心映射关系构造算法
 * @author frank
 *
 */
public class VpmMapping {

    /**
     * 映射算法的主方法，根据物理节点数和虚拟节点数构造虚拟节点到物理节点的映射关系
     * @param physicalNodesNum 映射关系中物理节点的数目
     * @param virtualNodesNum 映射关系中虚拟节点的数目
     * @return 数组下标是虚拟节点编号，数组值是物理节点编号
     */
    public static int[] makeVpm(int physicalNodesNum,int virtualNodesNum){
        int[] r = new int[virtualNodesNum];

        List<List<Integer>> l = makeP2VMapping(physicalNodesNum,virtualNodesNum);
        for(int i=0;i<l.size();i++){
            List<Integer> temp = l.get(i);
            for(Integer k:temp){
                r[k]=i;
            }
        }
        return r;
    }
    
    /**
     * 映射关系算法主要算法过程，构造物理节点到虚拟节点的映射关系
     * 客户端路由一般不需要该方法，server端迁移的时候若以虚拟节点为单位迁移需要调用该方法
     * @param physicalNodesNum 映射关系中物理节点的数目
     * @param virtualNodesNum 映射关系中虚拟节点的数目
     * @return List方式的二维数组，第一维（级）物理节点，第二维（级）是虚拟节点
     */
    public static List<List<Integer>> makeP2VMapping(int physicalNodesNum, int virtualNodesNum) {
        List<List<Integer>> h = new ArrayList<List<Integer>>();
        List<Integer> t = new ArrayList<Integer>();

        for (int i = 0; i < virtualNodesNum; i++) {
            t.add(i);
        }
        h.add(t);
        if (physicalNodesNum == 1) {
            return h;
        }
        for (int k = 2; k <= physicalNodesNum; k++) {
            List<List<Integer>> temp1 = new ArrayList<List<Integer>>();
            List<Integer> temp3 = new ArrayList<Integer>();
            int y[] = new int[k];
            for (int i = 1; i <= k; i++) {
                y[i - 1] = (virtualNodesNum - sumY(y, i - 1)) / (k + 1 - i);// 初始化物理节点内虚拟节点数目
            }
            for (int j = 0; j < (k-1); j++) {
                List<Integer> temp2 = new ArrayList<Integer>();
                
                for (int x = 0; x < h.get(j).size(); x++) {
                    if (x < y[j]) {
                        temp2.add(h.get(j).get(x));
                    } else {
                        temp3.add(h.get(j).get(x));
                    }
                }
                temp1.add(temp2);
            }
            temp1.add(temp3);
            h = temp1;
        }
        return h;

    }

    private static int sumY(int[] y, int i) {
        int sum = 0;
        for (int k = 0; k < i; k++) {
            sum += y[k ];
        }
        return sum;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        long b =System.currentTimeMillis();
        System.out.println(makeP2VMapping(3, 12));
        long e = System.currentTimeMillis();
        System.out.println(e-b);
        System.out.println(makeP2VMapping(4, 10000));
        System.out.println(makeVpm(3,12));

    }

}

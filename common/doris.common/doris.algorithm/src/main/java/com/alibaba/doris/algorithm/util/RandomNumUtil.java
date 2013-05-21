package com.alibaba.doris.algorithm.util;

/**
 * 随机数工具
 * @author frank
 *
 */
public class RandomNumUtil {


    public static int getRandomNum(){
        return (int)(System.currentTimeMillis()%10000);
    }
    /**
     * 性能最好
     * @return
     */
    public static int getRandomNumMath(){
        return (int)(Math.random()*1000);
    }
    
    public static int getRandomNum(String key){
        return key.hashCode();
    }
    
    public static void main(String args[]){
        System.out.println(getRandomNumMath());
        System.out.println(System.currentTimeMillis());
        long b = System.currentTimeMillis();
        for(int i=0;i<1000000;i++){
            getRandomNum();
        }
        long e = System.currentTimeMillis();
        System.out.println("time:"+(e-b));
    }
}

package com.alibaba.doris.algorithm.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具
 * 
 * @author frank
 */
public class MD5Util {

    private static MessageDigest md5Digist;
    private static final char[]  hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    static {
        try {
            md5Digist = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /*
     * 构造md5 String
     * @param key
     * @return
     */
    public static String md5(String key) {
        if (key == null) {
            key = "";
        }
        try {
			MessageDigest messageDigest = (MessageDigest) md5Digist.clone();
			messageDigest.update(key.getBytes());
		        byte[] b = messageDigest.digest();
		        char[] charArr = new char[32];
		        for (int i = 0; i < b.length; i++) {
		            charArr[i * 2] = hex[b[i] >>> 4 & 0xF];
		            charArr[i * 2 + 1] = hex[b[i] & 0xF];
		        }
		        return new String(charArr);
		        
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("md5:" + e,e);
		}
       
    }

    /**
     * 使用MD5散列的hash code
     * 
     * @param key
     * @return
     */
    public static int md5HashCode(String key) {
        md5Digist.reset();
        md5Digist.update(key.getBytes());
        byte[] b = md5Digist.digest();
        int rv = ((int) (b[3] & 0xFF) << 24) | ((int) (b[2] & 0xFF) << 16) | ((int) (b[1] & 0xFF) << 8) | (b[0] & 0xFF);
        return rv > 0 ? rv : -rv;
    }

    public static void main(String[] args) {
        // md5Digist.reset();
        // md5Digist.update("s".getBytes());
        // byte[] b = md5Digist.digest();
        // for(int i=0;i<b.length;i++){
        // System.out.println(i+" "+b[i]);
        // }

        long start = System.currentTimeMillis();

        System.out.println();

        for (int i = 0; i < 100000; i++) {

            String key = "ABC" + i;
            int hash = md5HashCode(key);
            System.out.println("hash : " + hash);
        }

        System.out.println("time:" + (System.currentTimeMillis() - start));
    }

}

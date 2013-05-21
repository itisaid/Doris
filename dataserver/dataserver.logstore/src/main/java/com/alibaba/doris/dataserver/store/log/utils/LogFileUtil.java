package com.alibaba.doris.dataserver.store.log.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.doris.dataserver.store.log.LogStorageException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class LogFileUtil {

    /**
     * 根据路径和clump的名称构建数据文件的完整名称。
     * 
     * @param path
     * @param clumpName
     * @return
     */
    public static String generateHeadFileName(String path, String clumpName) {
        if (path.endsWith(String.valueOf(File.separatorChar))) {
            return path + clumpName + HEAD_FILE_SUFFIX;
        }
        return path + File.separatorChar + clumpName + HEAD_FILE_SUFFIX;
    }

    /**
     * 根据路径和clump的名称构建数据文件的完整名称。
     * 
     * @param path
     * @param clumpName
     * @return
     */
    public static String generateDataFileName(String path, String clumpName) {
        if (path.endsWith(String.valueOf(File.separatorChar))) {
            return path + clumpName + DATA_FILE_SUFFIX;
        }

        return path + File.separatorChar + clumpName + DATA_FILE_SUFFIX;
    }

    /**
     * 根据一个文件名称，解析出clump的名称
     * 
     * @param fileName
     * @return
     */
    public static String parseClumpNameFromFileName(String fileName) {
        int pos = fileName.lastIndexOf(File.separatorChar);
        if (pos > 0) {
            fileName = fileName.substring(pos + 1);
        }
        return fileName.substring(0, fileName.length() - HEAD_FILE_SUFFIX.length());
    }

    /**
     * 列出指定path下面所有的clump文件的名称。
     * 
     * @param path
     * @return
     */
    public static String[] listAllLogClumpFileName(String path) {
        File f = new File(path);
        if (f.exists() && f.isDirectory()) {
            String[] fileNames = f.list();
            if (null != fileNames) {
                Set<String> nameSet = new HashSet<String>(fileNames.length);
                for (String fileName : fileNames) {
                    if (isLogClumpFileName(fileName)) {
                        nameSet.add(parseClumpNameFromFileName(fileName));
                    }
                }
                return nameSet.toArray(new String[nameSet.size()]);
            }
            return new String[0];
        }

        return EMPTY_STRING_ARRAY;
    }

    public static boolean isLogClumpFileName(String fileName) {
        return fileName.endsWith(DATA_FILE_SUFFIX) || fileName.endsWith(HEAD_FILE_SUFFIX);
    }

    /**
     * 获取指定目录下面clump文件的最大编号。
     * 
     * @param path
     * @return
     */
    public static int getMaxClumpNo(String path) {
        String[] fileNames = listAllLogClumpFileName(path);
        if (fileNames.length > 0) {
            List<String> list = new ArrayList<String>(fileNames.length);
            Collections.sort(list);
            String clumpName = list.get(list.size() - 1);
            return getClumpNoFromClumpName(clumpName);
        } else {
            return 0;
        }
    }

    /**
     * 根据一个给定的Clump文件名获取对应的clump编号。
     * 
     * @param clumpName
     * @return
     */
    public static int getClumpNoFromClumpName(String clumpName) {
        return Integer.valueOf(clumpName);
    }

    /**
     * 根据clump编号生成一个规范的Clump名称
     * 
     * @param clumpNo
     * @return
     */
    public static String generateClumpName(int clumpNo) {
        return String.format("%08d", clumpNo);
    }

    public static void deleteClumpFile(String path, String clumpName) {
        String headFileName = generateHeadFileName(path, clumpName);
        String dataFileName = generateDataFileName(path, clumpName);
        File file = new File(headFileName);
        if (file.exists()) {
            if (!file.delete()) {
                String message = "Unable to delete file: " + file;
                throw new LogStorageException(message);
            }
        }

        file = new File(dataFileName);
        if (file.exists()) {
            file.delete();
        }
    }

    private static final String   HEAD_FILE_SUFFIX   = ".lh";
    private static final String   DATA_FILE_SUFFIX   = ".ld";
    private static final String[] EMPTY_STRING_ARRAY = new String[] {};
}

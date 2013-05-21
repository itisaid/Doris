/**
 * Project: dataserver.tools-0.1.0-SNAPSHOT
 * 
 * File Created at 2011-8-4
 * $Id$
 * 
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.alibaba.doris.client.tools.importdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;

import com.alibaba.doris.client.tools.concurrent.ParralelTaskImpl;

/**
 * TODO Comment of ReadTask
 * 
 * @author luyi.huangly
 */
public class FileReadTask extends ParralelTaskImpl {
    private Queue<KeyPair> keyQueue;
    private String         fileName;
    ReentrantLock          lock     = new ReentrantLock(); //锁
    //根据锁产生Condition对象
    Condition              produced = lock.newCondition();
    private AtomicBoolean  finish;

    /**
     * @return the finish
     */
    public AtomicBoolean getFinish() {
        return finish;
    }

    /**
     * @param finish the finish to set
     */
    public void setFinish(AtomicBoolean finish) {
        this.finish = finish;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public void setLock(ReentrantLock lock) {
        this.lock = lock;
    }

    public Condition getProduced() {
        return produced;
    }

    public void setProduced(Condition produced) {
        this.produced = produced;
    }

    public Queue<KeyPair> getKeyQueue() {
        return keyQueue;
    }

    public void setKeyQueue(Queue<KeyPair> keyQueue) {
        this.keyQueue = keyQueue;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void doRun(long index) {

        File file = new File(fileName);
        if (!file.exists() || !file.isFile()) {
            System.err.println("file path error!!");
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            System.err.println("file path error!!");
        }
        String tempString = null;
        int line = 1;
        // 一次读入一行，直到读入null为文件结束
        try {
            while ((tempString = reader.readLine()) != null) {
                String[] tempStrings = StringUtils.split(tempString, " ");
                if (tempStrings.length != 2) {
                    System.err.println(String.format("line %s format error!!", line));
                }
                try {
                    lock.lock();//获得锁
                    keyQueue.add(new KeyPair(tempStrings[0], tempStrings[1]));
                    produced.signal();
                } finally {
                    lock.unlock();
                }
                line++;
            }
        } catch (IOException e) {
            System.err.println("file path error!!");
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finish.set(true);
        }

    }
}

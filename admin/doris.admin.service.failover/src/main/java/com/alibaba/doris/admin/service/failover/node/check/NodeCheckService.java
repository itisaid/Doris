/**
 * Project: doris.admin.service.failover-0.1.0-SNAPSHOT
 * 
 * File Created at 2011-5-27
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
package com.alibaba.doris.admin.service.failover.node.check;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @deprecated
 * @author mian.hem
 */
public class NodeCheckService {

    private static final Log log                  = LogFactory.getLog(NodeCheckService.class);

    /**
     * 默认线程池大小
     */
    public static final int  DEFAULT_POOL_SIZE    = 5;

    /**
     * 默认一个任务的超时时间，单位为毫秒
     */
    public static final long DEFAULT_TASK_TIMEOUT = 5000;
    private int              poolSize             = DEFAULT_POOL_SIZE;
    private ExecutorService  executorService;

    public NodeCheckService() {
        setPoolSize(DEFAULT_POOL_SIZE);
    }

    public NodeCheckService(int poolSize) {
        setPoolSize(poolSize);
    }

    /**
     * 在线程池中执行所有给定的任务并取回运行结果，使用默认超时时间
     * 
     * @see #invokeAll(List, long)
     */
    public List<NodeCheckResult> invokeAll(List<NodeCheckTask> tasks) {
        return invokeAll(tasks, DEFAULT_TASK_TIMEOUT * tasks.size());
    }

    /**
     * 在线程池中执行所有给定的任务并取回运行结果
     * 
     * @param timeout 以毫秒为单位的超时时间，小于0表示不设定超时
     * @see java.util.concurrent.ExecutorService#invokeAll(java.util.Collection)
     */
    public List<NodeCheckResult> invokeAll(List<? extends NodeCheckTask> tasks, long timeout) {
        List<NodeCheckResult> nodes = new ArrayList<NodeCheckResult>(tasks.size());
        try {
            List<Future<NodeCheckResult>> futures = null;
            if (timeout < 0) {
                futures = executorService.invokeAll(tasks, DEFAULT_TASK_TIMEOUT * tasks.size(), TimeUnit.MILLISECONDS);
            } else {
                futures = executorService.invokeAll(tasks, timeout, TimeUnit.MILLISECONDS);
            }
            for (Future<NodeCheckResult> future : futures) {
                try {
                    nodes.add(future.get());
                } catch (ExecutionException e) {
                    log.error("failed to check node, skip and continue.", e);
                }
            }
        } catch (InterruptedException e) {
            log.error("failed to check node, skip and continue.", e);
        }
        return nodes;
    }

    /**
     * 关闭当前ExecutorService
     * 
     * @param timeout 以毫秒为单位的超时时间
     */
    private void destoryExecutorService(long timeout) {
        if (executorService != null && !executorService.isShutdown()) {
            try {
                executorService.awaitTermination(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executorService.shutdown();
        }
    }

    /**
     * 关闭当前ExecutorService，随后根据poolSize创建新的ExecutorService
     */
    private void createExecutorService() {
        destoryExecutorService(1000);
        executorService = Executors.newFixedThreadPool(poolSize);
    }

    /**
     * 调整线程池大小
     * 
     * @see #createExecutorService()
     */
    private void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
        createExecutorService();
    }
}

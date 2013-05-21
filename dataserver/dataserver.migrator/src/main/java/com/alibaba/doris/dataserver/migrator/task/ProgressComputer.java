/*
 * Copyright(C) 2010 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.dataserver.migrator.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.alibaba.doris.common.route.MigrationRoutePair;

/**
 * ProgressComputer
 * 
 * @author Raymond He ( He Kun), raymond.he.kk@gmail.com
 * @since 1.0 2011-6-23
 */
public class ProgressComputer {

    private Map<String, ProgressUnit> migrationProgressMap = new ConcurrentHashMap<String, ProgressUnit>();
    private List<MigrationRoutePair>  migrationRoutePairs;

    private int                       finishCount;
    private int                       grossProgress        = -1;

    private ReentrantLock reentrantLock  = new ReentrantLock();
    
    public ProgressComputer(List<MigrationRoutePair> migrationRoutePairs) {
        this.migrationRoutePairs = migrationRoutePairs;

        for (int i = 0; i < migrationRoutePairs.size(); i++) {
            MigrationRoutePair routePair = migrationRoutePairs.get(i);

            String targetId = routePair.getTargetPhysicalId();
            ProgressUnit unit = migrationProgressMap.get(routePair.getTargetPhysicalId());
            if (unit == null) {
                unit = new ProgressUnit();
                unit.setTargetNodeId(targetId);
            }
            unit.getMigrateVNodes().add(routePair.getVnode());

            migrationProgressMap.put(targetId, unit);
        }
    }

    /**
     * 做完一个虚拟节点的迁移
     * 
     * @param targetNodeId
     * @param vnodeId
     */
    public synchronized boolean completeOneVNode(String targetNodeId, Integer vnodeId) {
    	
    	reentrantLock.lock();
    	
    	try {
    	    /* 源节点的总进度 */
            finishCount++;
            int newGrossProgress = (finishCount * 100) / migrationRoutePairs.size();

            ProgressUnit unit = migrationProgressMap.get(targetNodeId);

            int p = unit.getProgress();

            int newProgress = p + (1 * 100) / unit.getMigrateVNodes().size();

            unit.setProgress(newProgress);
            unit.setLastTime(System.currentTimeMillis());

            if (newGrossProgress > this.grossProgress) { // true: need to notify new progress
                this.grossProgress = newGrossProgress;
                return true;
            } else {
                this.grossProgress = newGrossProgress;
                return false;
            }
    	}finally {
    		reentrantLock.unlock();
    	}
    
    }

    public int getGrossProgress() {
    	reentrantLock.lock();
        try {
        	return grossProgress;
        }finally {
        	reentrantLock.unlock();
        }
    }

    public int getFinishCount() {
        return finishCount;
    }

    /**
     * 迁移进度
     * 
     * @param tNodeId 目标节点Id
     * @return
     */
    public int getProgressOfTarget(String tNodeId) {
    	reentrantLock.lock();
    	
    	try {
        ProgressUnit unit = migrationProgressMap.get(tNodeId);
        return unit.getProgress();
    	}finally {
    		reentrantLock.unlock();
    	}
    }

    public static class ProgressUnit {

        private String        targetNodeId;
        private List<Integer> migrateVNodes = new ArrayList<Integer>();
        private int           progress;
        private long          lastTime;

        public String getTargetNodeId() {
            return targetNodeId;
        }

        public void setTargetNodeId(String targetNodeId) {
            this.targetNodeId = targetNodeId;
        }

        public List<Integer> getMigrateVNodes() {
            return migrateVNodes;
        }

        public void setMigrateVNodes(List<Integer> migrateVNodes) {
            this.migrateVNodes = migrateVNodes;
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public void setLastTime(long lastTime) {
            this.lastTime = lastTime;
        }

        public long getLastTime() {
            return lastTime;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            return s.append("[ProgressUnit:").append("targetNodeId=").append(targetNodeId).append(",").append(
                                                                                                              "progress=").append(
                                                                                                                                  progress).append(
                                                                                                                                                   "]").toString();
        }
    }
}

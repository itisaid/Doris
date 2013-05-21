/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
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
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.route.MigrationRoutePair;
import com.alibaba.doris.dataserver.migrator.MigrationException;
import com.alibaba.doris.dataserver.migrator.MigrationManager;
import com.alibaba.doris.dataserver.migrator.action.MigrationActionData;
import com.alibaba.doris.dataserver.store.Storage;

/**
 * DataMigrationExecutor
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-6-1
 */
public class DataMigrationExecutor {

    public static final int             MAX_MAGRATION_TRYCOUNT = 3;

    protected Logger                    logger                 = LoggerFactory.getLogger(DataMigrationExecutor.class);

    protected int                       retryCount             = 3;

    protected BaseMigrationTask         migrationTask;

    protected MigrationManager          migrationManager;
    protected Storage                   storage;

    protected MigrationActionData       migrationActionData;

    protected volatile ProgressComputer progressComputer;

    public DataMigrationExecutor() {

    }

    protected String getOperationName() {
        return "Migrate";
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getRetryCount() {
        return retryCount;
    }

    /**
     * 执行实际的数据迁移. 根据迁移路由，在 storage 中遍历每个虚拟节点，将数据写入目标节点的 connection 中。
     * 
     * @throws MigrationException
     */
    public void execute() throws MigrationException {
        if (logger.isInfoEnabled()) {
            logger.info("Starting real data " + getOperationName() + " task. ");
        }

        List<MigrationRoutePair> pairs = migrationActionData.getMigrationRoutePairs();

        progressComputer = new ProgressComputer(pairs);

        if (logger.isInfoEnabled()) {
            logger.info("Finish prepare connections.");
        }

        if (migrationTask.isCancel()) {
            if (logger.isInfoEnabled()) {
                logger.info(getOperationName() + " Task has been cancelled. Terminate it.");
            }
            return;
        }

        long start = System.currentTimeMillis();

        migrateAllVNodes(pairs);

        long end = System.currentTimeMillis();

        long ellapse = end - start;

        if (logger.isInfoEnabled()) {
            logger.info("Migration all vnodes finished, time use:" + (ellapse));
        }
    }

    /**
     * migrate0
     * 
     * @param pairs
     * @param connectionManager
     * @throws MigrationException
     */
    protected void migrateAllVNodes(List<MigrationRoutePair> pairs) throws MigrationException {
        int totoalMCount = 0;
        List<TaskItem> taskList = generateTasks(pairs);

        ExecutorService executor = Executors.newFixedThreadPool(taskList.size() + 1);
        boolean isCatchException = false;

        try {
            List<Future<Integer>> taskFuterList = new ArrayList<Future<Integer>>(taskList.size());
            BlockingQueue<Pair> dataQueue = new ArrayBlockingQueue<Pair>(10000);
            List<Integer> vnodeList = new ArrayList<Integer>(pairs.size());

            for (TaskItem task : taskList) {
                vnodeList.addAll(task.getVnodeList());
                Callable<Integer> callableTask = new MigrateDataThread(migrationTask, task.getMachine(), dataQueue);
                taskFuterList.add(executor.submit(callableTask));
            }

            readDataThread = new ReadDataThread(storage, vnodeList, dataQueue, taskList.size());
            executor.submit(readDataThread);

            // 阻塞并等待各子任务的执行结果。
            for (int index = 0; index < taskFuterList.size(); index++) {
                Future<Integer> future = taskFuterList.get(index);
                try {
                    totoalMCount += future.get().intValue();

                    TaskItem task = taskList.get(index);
                    List<Integer> taskVNodeList = task.getVnodeList();
                    // 迁移完所有虚拟节点 ，计算迁移进度
                    for (Integer vnode : taskVNodeList) {
                        progressComputer.completeOneVNode(task.getMachine(), vnode);
                    }

                    migrationTask.notifyMigrateProgress(progressComputer.getGrossProgress());
                } catch (Exception e) {
                    logger.error("Waiting migrate result failed. we need throw this exception", e);
                    isCatchException = true;
                    // 一旦迁移过程中，某个线程发生异常退出，立即终止所有的迁移线程，终止迁移过程。
                    readDataThread.stopReadThread();
                }
            }
        } finally {
            executor.shutdownNow();
        }

        if (isCatchException) {
            throw new MigrationException("Migrating data failed.");
        }

        migrationTask.notifyMigrateNodeFinish();

        if (logger.isInfoEnabled()) {
            logger.info("Complete data " + getOperationName() + " all all vnodes  " + totoalMCount + ", vnodes count: "
                        + pairs.size() + ". ");
        }
    }

    private int getVnodeNumForPerThread(int size) {
        int vnodeNum = size / migrateDataThreadNum;
        if (vnodeNum < MIN_VNODE_NUM_PER_THREAD) {
            return MIN_VNODE_NUM_PER_THREAD;
        }
        return vnodeNum;
    }

    /**
     * 将迁移的pair分解成多个可以并行执行的子任务。
     * 
     * @param pairs
     * @return
     */
    private List<TaskItem> generateTasks(List<MigrationRoutePair> pairs) {
        int vnodeCount = pairs.size();
        int vnodeNumForPerThread = getVnodeNumForPerThread(vnodeCount);

        Map<String, List<Integer>> machineVnodeMapper = sortTaskByMachine(pairs);

        Set<Entry<String, List<Integer>>> set = machineVnodeMapper.entrySet();
        List<TaskItem> taskItemList = new ArrayList<TaskItem>();
        for (Entry<String, List<Integer>> entry : set) {
            taskItemList.addAll(splitOneMachineTask(entry.getKey(), entry.getValue(), vnodeNumForPerThread));
        }

        return taskItemList;
    }

    /**
     * 将一个机器上带迁移的虚拟节点，根据迁移线程数，划分成多个可并行的子任务。
     * 
     * @param machine
     * @param vnodeList
     * @param vnodeNumForPerThread
     * @return
     */
    private List<TaskItem> splitOneMachineTask(String machine, List<Integer> vnodeList, int vnodeNumForPerThread) {
        List<TaskItem> taskItemList = new ArrayList<TaskItem>();
        int size = vnodeList.size();

        for (int i = 0; i < size;) {
            int len = vnodeNumForPerThread;
            if ((i + len) >= size) {
                len = size - i;
            }

            taskItemList.add(new TaskItem(machine, vnodeList.subList(i, i + len)));
            i += len;
        }

        return taskItemList;
    }

    /**
     * 将所有route pair根据目标机器分类。
     * 
     * @param pairs
     * @return
     */
    private Map<String, List<Integer>> sortTaskByMachine(List<MigrationRoutePair> pairs) {
        Map<String, List<Integer>> machineVnodesMapping = new HashMap<String, List<Integer>>();

        for (MigrationRoutePair pair : pairs) {
            List<Integer> vnodeList = machineVnodesMapping.get(pair.getTargetPhysicalId());
            if (null == vnodeList) {
                vnodeList = new ArrayList<Integer>();
                machineVnodesMapping.put(pair.getTargetPhysicalId(), vnodeList);
            }
            vnodeList.add(pair.getVnode());
        }

        return machineVnodesMapping;
    }

    private static class TaskItem {

        public TaskItem(String machine, List<Integer> vnodeList) {
            this.machine = machine;
            this.vnodeList = vnodeList;
        }

        public String getMachine() {
            return machine;
        }

        public void setMachine(String machine) {
            this.machine = machine;
        }

        public List<Integer> getVnodeList() {
            return vnodeList;
        }

        public void setVnodeList(List<Integer> vnodeList) {
            this.vnodeList = vnodeList;
        }

        private String        machine;
        private List<Integer> vnodeList;
    }

    protected void notifyMigrationProgress(int progress) {
        migrationTask.notifyMigrateProgress(progress);
    }

    public void setMigrationTask(BaseMigrationTask migrationTask) {
        this.migrationTask = migrationTask;
        this.migrationManager = migrationTask.getMigrationManager();
        this.migrationActionData = migrationTask.getMigrationActionData();
        this.storage = migrationManager.getStorage();
        if (migrationManager.getMigrateThreads() > 0) {
            this.migrateDataThreadNum = migrationManager.getMigrateThreads();
        }
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public Storage getStorage() {
        return storage;
    }

    public ProgressComputer getProgressComputer() {
        return progressComputer;
    }

    public void cancel() {
        isCancelled = true;
        if (null != readDataThread) {
            readDataThread.stopReadThread();
        }
    }

    protected boolean isCancelled() {
        return isCancelled;
    }

    private ReadDataThread   readDataThread;
    private volatile boolean isCancelled              = false;
    private int              migrateDataThreadNum     = 20;   // 默认启动20个线程执行迁移。
    private final static int MIN_VNODE_NUM_PER_THREAD = 1;
}

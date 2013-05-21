/*
 * Copyright(C) 2010 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.dataserver.migrator.task.dataclean;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.alibaba.doris.common.route.MigrationRoutePair;
import com.alibaba.doris.dataserver.migrator.MigrationException;
import com.alibaba.doris.dataserver.migrator.task.DataMigrationExecutor;

/**
 * DataCleanExecutor.
 * <p/>
 * 数据清理任务
 * 
 * @author Raymond He ( He Kun), raymond.he.kk@gmail.com
 * @since 1.0 2011-6-27
 */
public class DataCleanExecutor extends DataMigrationExecutor {

    public DataCleanExecutor() {
        logger = LoggerFactory.getLogger(DataCleanExecutor.class);
    }

    @Override
    protected String getOperationName() {
        return "Delete";
    }

    @Override
    protected void migrateAllVNodes(List<MigrationRoutePair> pairs) throws MigrationException {

        if (migrationTask.isCancel()) {
            if (logger.isInfoEnabled()) {
                logger.info(getOperationName() + " Task has been cancelled.Terminate it.");
            }
            return;
        }

        List<Integer> vnodes = buildVNodesList(pairs);

        long start = System.currentTimeMillis();

        if (logger.isInfoEnabled()) {
            logger.info("Start delete local data by vnodes. Wait for completing. VNodes: " + vnodes);
        }

        try {
            boolean sucess = storage.delete(vnodes);
            if (!sucess) {
                logger.error("Cleaning data failed. The vnode list is " + vnodes);
            }
        } catch (Exception e) {
            logger.error("Delete data failed.", e);
            throw new MigrationException(e);
        }

        long end = System.currentTimeMillis();
        long ellapse = end - start;

        int progress = 100;
        if (logger.isInfoEnabled()) {
            logger.info("Complete data " + getOperationName() + " all vnodes  " + vnodes.size() + ", vnodes count: "
                        + pairs.size() + ". Progress:" + progress + ", ellapse:" + ellapse);
        }

        notifyMigrationProgress(progress);
    }

    private List<Integer> buildVNodesList(List<MigrationRoutePair> pairs) {
        List<Integer> vnodes = new ArrayList<Integer>(pairs.size());
        for (int i = 0; i < pairs.size(); i++) {

            MigrationRoutePair routePair = pairs.get(i);
            vnodes.add(routePair.getVnode());
        }
        return vnodes;
    }

    @Override
    protected void notifyMigrationProgress(int newProgress) {

        if (logger.isDebugEnabled()) {
            logger.debug("notifyDataCleanProgress:" + newProgress);
        }
        migrationTask.notifyDataCleanProcess(newProgress);
    }
}

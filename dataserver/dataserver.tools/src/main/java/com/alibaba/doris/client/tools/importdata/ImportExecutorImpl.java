/**
 * 
 */
package com.alibaba.doris.client.tools.importdata;

import java.util.concurrent.CountDownLatch;

import com.alibaba.doris.client.tools.concurrent.ParralelExecutorImpl;
import com.alibaba.doris.client.tools.concurrent.ParralelTask;

/**
 * @author raymond
 */
public class ImportExecutorImpl extends ParralelExecutorImpl {

    /**
     * @param start
     * @param end
     * @param concurrent
     * @param needProfiling
     * @param clazz
     */
    public ImportExecutorImpl(int start, int end, int concurrent, boolean needProfiling,
                              Class<? extends ParralelTask> clazz) {
        super(start, end, concurrent, needProfiling, clazz);
    }

    public void start() {
        CountDownLatch startCountDownLatch = new CountDownLatch(concurrent);
        resultCountDownLatch = new CountDownLatch(concurrent);
        for (int i = 0; i < concurrent; i++) {
            ParralelTask parralelTask = parralelTaskFactory.createTask(parralelTaskClass,
                    concurrent, 0, 0, startCountDownLatch, resultCountDownLatch);
            parralelTask.setNeedProfiling(needPofiling);
            parralelTask.setPermMeter(permMeter);
            executorService.execute(parralelTask);
        }
    }

}

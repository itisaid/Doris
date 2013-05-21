/**
 * 
 */
package com.alibaba.doris.client.tools.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author raymond
 */
public class ParralelExecutorImpl implements ParralelExecutor {

    private int                             start;
    private int                             end;
    protected int                           concurrent;
    protected boolean                       needPofiling;
    private Object                          result;

    protected ExecutorService               executorService;

    protected CountDownLatch                resultCountDownLatch;

    protected PermMeter                     permMeter = new PermMeter();

    protected ParralelTaskFactory           parralelTaskFactory;
    protected Class<? extends ParralelTask> parralelTaskClass;

    public ParralelExecutorImpl(int start, int end, int concurrent, boolean needProfiling,
                                Class<? extends ParralelTask> clazz) {
        this.start = start;
        this.end = end;
        this.concurrent = concurrent;
        this.needPofiling = needProfiling;
        this.parralelTaskClass = clazz;
        executorService = Executors.newFixedThreadPool(concurrent);
    }

    public ParralelExecutorImpl(int start, int end, int concurrent, boolean needProfiling,
                                ParralelTaskFactory parralelTaskFactory) {
        this.start = start;
        this.end = end;
        this.concurrent = concurrent;
        this.needPofiling = needProfiling;
        this.parralelTaskFactory = parralelTaskFactory;

        executorService = Executors.newFixedThreadPool(concurrent);
    }

    public ParralelTaskFactory getParralelTaskFactory() {
        return parralelTaskFactory;
    }

    public void setParralelTaskFactory(ParralelTaskFactory parralelTaskFactory) {
        this.parralelTaskFactory = parralelTaskFactory;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getConcurrent() {
        return concurrent;
    }

    public void setConcurrent(int concurrent) {
        this.concurrent = concurrent;
    }

    public boolean isNeedPofiling() {
        return needPofiling;
    }

    public void setNeedPofiling(boolean needPofiling) {
        this.needPofiling = needPofiling;
    }

    public PermMeter getPermMeter() {
        return permMeter;
    }

    public Object getResult() {
        try {
            resultCountDownLatch.await();
        } catch (InterruptedException e) {
            //			e.printStackTrace();
        }
        //		resultCountDownLatch.countDown();
        return result;
    }

    public void start() {

        long amount = end - start;

        long everyAmount = amount / concurrent;

        List<Range> rangeList = new ArrayList<Range>();

        long last = start;
        for (int i = 0; i < concurrent; i++) {

            Range range = new Range(last, last + everyAmount - 1);
            last = last + everyAmount;
            rangeList.add(range);
        }

        CountDownLatch startCountDownLatch = new CountDownLatch(concurrent);
        resultCountDownLatch = new CountDownLatch(concurrent);

        for (int i = 0; i < rangeList.size(); i++) {
            Range range = rangeList.get(i);
            ParralelTask parralelTask = parralelTaskFactory.createTask(parralelTaskClass, i, range
                    .getStart(), range.getEnd(), startCountDownLatch, resultCountDownLatch);
            parralelTask.setNeedProfiling(needPofiling);
            parralelTask.setPermMeter(permMeter);
            executorService.execute(parralelTask);
        }
    }
}

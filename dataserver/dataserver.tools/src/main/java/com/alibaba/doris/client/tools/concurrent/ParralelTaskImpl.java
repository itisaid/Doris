/**
 * 
 */
package com.alibaba.doris.client.tools.concurrent;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @author raymond
 */
public class ParralelTaskImpl implements ParralelTask {

    int                      no;
    long                     start;
    long                     end;
    protected boolean        needPofiling;

    protected CountDownLatch startCountDownLatch;
    protected CountDownLatch resultCountDownLatch;

    protected PermMeter      permMeter;

    public ParralelTaskImpl() {

    }

    public ParralelTaskImpl(int no, long start, long end, CountDownLatch countDownLatch,
                            CountDownLatch resultCountDownLatch) {
        this.no = no;
        this.start = start;
        this.end = end;
        this.startCountDownLatch = countDownLatch;
        this.resultCountDownLatch = resultCountDownLatch;
    }

    public void setPermMeter(PermMeter permMeter) {
        this.permMeter = permMeter;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getNo() {
        return no;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void setNeedProfiling(boolean needPofiling) {
        this.needPofiling = needPofiling;
    }

    public boolean isNeedPofiling() {
        return needPofiling;
    }

    public void run() {
        startCountDownLatch.countDown();

        for (long i = start; i <= end; i++) {

            long startTime = System.currentTimeMillis();
            if (needPofiling) {

                permMeter.startRecord();
            }

            doRun(i);

            if (needPofiling) {
                permMeter.endRecord();
                long endTime = System.currentTimeMillis();
                long ellapseTime = endTime - startTime;

                permMeter.addItem(ellapseTime);
                //				permMeter.printReport();
            }
        }
        resultCountDownLatch.countDown();
    }

    public void doRun(long index) {
        try {
            Random random = new Random();
            Thread.sleep(random.nextInt(500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Atom.doRun[" + no + "-" + start + "," + end + "]" + index);
    }

    public CountDownLatch getCountDownLatch() {
        return startCountDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.startCountDownLatch = countDownLatch;
    }

    public CountDownLatch getResultCountDownLatch() {
        return resultCountDownLatch;
    }

    public void setResultCountDownLatch(CountDownLatch resultCountDownLatch) {
        this.resultCountDownLatch = resultCountDownLatch;
    }

    public PermMeter getPermMeter() {
        return permMeter;
    }

    public void setNeedPofiling(boolean needPofiling) {
        this.needPofiling = needPofiling;
    }

}

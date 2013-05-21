package com.alibaba.doris.client.tools;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.doris.cli.CommandLineHandler;
import com.alibaba.doris.cli.Option;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.KeyFactory;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.ValueFactory;
import com.alibaba.doris.common.data.util.ByteUtils;

/*
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class PressureTestingBase extends CommandLineHandler {

    public PressureTestingBase() {
        options.add(new Option("-vl", "vl", "Setting the length of value.", false, true));
        options.add(new Option("-threads", "thread", "Setting the thread number for this test."));
        options.add(new Option("-ns", "namespace", "Setting the namespace.", false, true));
        options.add(new Option("-s", "start", "Setting the start position of key."));
        options.add(new Option("-e", "end", "Setting the end position of key."));
    }

    @Override
    public void prepareParameters() {
        if (commandLine.getValue("-vl") != null) {
            vl = commandLine.getInt("-vl");
        }
        threads = commandLine.getInt("-threads");
        namespace = commandLine.getValue("-ns");
        start = commandLine.getInt("-s");
        end = commandLine.getInt("-e");
    }

    @Override
    public void handleCommand() {
        try {
            startGate = new CountDownLatch(1);
            endGate = new CountDownLatch(threads);

            ExecutorService threadFactory = Executors.newFixedThreadPool(threads);
            int step = len() / threads;
            for (int i = 0; i < threads; i++) {
                BaseRunnableTask task = (BaseRunnableTask) createRunnableTask(i, threads);
                task.setStartPos(i * step);
                task.setEndPos((i + 1) * step);
                threadFactory.execute(task);
            }

            startGate.countDown();
            PrintThread printer = new PrintThread();
            new Thread(printer).start();
            try {
                endGate.await();
                printer.setStop(true);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            printStatisticInfo();
            System.out.println("Exit testing program.");
        } finally {
            release();
        }
    }

    protected void release() {

    }

    private void printStatisticInfo() {
        long time = timeCounter.get();
        long count = countCounter.get();
        if (count > 0) {
            System.out.println("Testing statistic: Time:" + time + " count:" + count + " error:" + errorCounter.get()
                               + " avg:" + (time / count));
        }
    }

    public Runnable createRunnableTask(int index, int count) {
        return new BaseRunnableTask();
    }

    protected class BaseRunnableTask implements Runnable {

        public final void run() {
            prepare();
            try {
                long start = 0;
                int length = endPos - startPos;
                startGate.await();
                for (int i = startPos; i < endPos; i++) {
                    start = System.currentTimeMillis();
                    boolean isSuccess = execute(i, length);
                    timeCounter.addAndGet(System.currentTimeMillis() - start);
                    countCounter.incrementAndGet();
                    if (!isSuccess) {
                        errorCounter.incrementAndGet();
                    }
                }
                endGate.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                destory();
            }
        }

        public void prepare() {

        }

        public void destory() {

        }

        public boolean execute(int index, int length) {
            return false;
        }

        public void setStartPos(int startPos) {
            this.startPos = startPos;
        }

        public void setEndPos(int endPos) {
            this.endPos = endPos;
        }

        private int startPos;
        private int endPos;
    }

    protected String getValue() {
        StringBuilder sb = new StringBuilder(vl);
        for (int i = 0; i < vl; i++) {
            sb.append("V");
        }
        return sb.toString();
    }

    protected Key getKey(String key) {
        return KeyFactory.createKey(Integer.valueOf(namespace), key/*
                                                                    * , algorithm.getVirtualByKey(namespace + ":" + key)
                                                                    */);
    }

    protected Value getValueObject(String value) {
        return ValueFactory.createValue(ByteUtils.stringToByte(value), System.currentTimeMillis());
    }

    private class PrintThread implements Runnable {

        public void run() {
            while (!isStop) {
                sleep();
                printStatisticInfo();
            }
        }

        private void sleep() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void setStop(boolean isStop) {
            this.isStop = isStop;
        }

        private void printStatisticInfo() {
            long time = timeCounter.get();
            long count = countCounter.get();

            long timeExpired = time - prevTime;
            long countInner = count - prevCount;
            prevCount = count;
            prevTime = time;

            if (countInner > 0) {
                if (count > 0) {
                    System.out.println("Total time:" + time + " Total count:" + count + " Time:" + timeExpired
                                       + " count:" + countInner + " error:" + errorCounter.get() + " avg:"
                                       + (timeExpired / countInner));
                }
            }
        }

        private long             prevTime;
        private long             prevCount;

        private volatile boolean isStop = false;
    }

    protected int len() {
        return end - start;
    }

    private AtomicLong     timeCounter  = new AtomicLong();
    private AtomicLong     countCounter = new AtomicLong();
    private AtomicInteger  errorCounter = new AtomicInteger();
    private CountDownLatch startGate;
    private CountDownLatch endGate;
    private int            threads;
    private int            vl;
    private String         namespace;
    private int            start;
    private int            end;
}

package com.alibaba;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.math.RandomUtils;

import com.alibaba.doris.cli.CommandLineHandler;
import com.alibaba.doris.cli.Option;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class BdbPressureTesting extends CommandLineHandler {

    /**
     * @param args
     */
    public static void main(String[] args) {
        BdbPressureTesting pressureTestingTool = new BdbPressureTesting();
        pressureTestingTool.handle(args);
        System.exit(0);
    }

    public BdbPressureTesting() {
        options.add(new Option("-c", "config", "Setting the config file name of bdb storage."));
        options.add(new Option("-threads", "thread", "Setting the thread number for this test."));
        options.add(new Option("-s", "start", "Setting the start position of key."));
        options.add(new Option("-e", "end", "Setting the end position of key."));
        options.add(new Option("-repeat", "repeat", "Setting the repeat times for each thread."));
    }

    @Override
    public void handleCommand() {
        // BDBStorageDriver stroageDriver = new BDBStorageDriver();
        // StorageConfig config = new StorageConfig();
        // config.setPropertiesFile(propertiesFile);
        //
        // stroageDriver.init(config);
        // storage = stroageDriver.createStorage();
        //
        // startGate = new CountDownLatch(1);
        // endGate = new CountDownLatch(nThreads);
        //
        // ExecutorService threadFactory = Executors.newFixedThreadPool(nThreads);
        // for (int i = 0; i < nThreads; i++) {
        // threadFactory.execute(new GetThread());
        // }
        //
        // startGate.countDown();
        // PrintThread printer = new PrintThread();
        // new Thread(printer).start();
        // try {
        // endGate.await();
        // printer.setStop(true);
        // Thread.sleep(100);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        //
        // printStatisticInfo();
        System.out.println("Exit testing program.");
    }

    @Override
    public void prepareParameters() {
        propertiesFile = commandLine.getValue("-c");
        nThreads = commandLine.getInt("-threads");
        start = commandLine.getInt("-s");
        end = commandLine.getInt("-e");
        repeat = commandLine.getInt("-repeat");
        if (end == 0) {
            end = 500000;
        }
        length = end - start;
    }

    private void printStatisticInfo() {
        long time = timeCounter.get();
        long count = countCounter.get();
        if (count > 0) {
            System.out.println("Time:" + time + " count:" + count + " error:" + errorCounter.get() + " avg:"
                               + (time / count));
        }
    }

    private int randomPosition() {
        return start + RandomUtils.nextInt(length);
    }

    private int getVnode(int key) {
        return key % 1000;
    }

    private class GetThread implements Runnable {

        public void run() {
            // Object value = null;
            // long start = 0;
            // int loops = repeat;
            //
            // try {
            // startGate.await();
            // while (loops-- > 0) {
            // int position = randomPosition();
            // start = System.currentTimeMillis();
            // value = storage.get(KeyFactory.createKey(1, String.valueOf(position), getVnode(position)));
            // timeCounter.addAndGet(System.currentTimeMillis() - start);
            // countCounter.incrementAndGet();
            // if (value == null) {
            // errorCounter.incrementAndGet();
            // }
            // }
            // endGate.countDown();
            // } catch (Exception e) {
            // e.printStackTrace();
            // }

        }
    }

    private class PrintThread implements Runnable {

        public void run() {
            while (!isStop) {
                sleep();
                this.printStatisticInfo();
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

    private String         propertiesFile;
    // private Storage storage;
    private int            start;
    private int            end;
    private int            length;
    private int            repeat;
    private int            nThreads;
    private AtomicLong     timeCounter  = new AtomicLong();
    private AtomicLong     countCounter = new AtomicLong();
    private AtomicInteger  errorCounter = new AtomicInteger();
    private CountDownLatch startGate;
    private CountDownLatch endGate;
}

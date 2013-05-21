package com.alibaba.doris.client.test;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.math.RandomUtils;

import com.alibaba.doris.algorithm.vpm.VpmRouterAlgorithm;
import com.alibaba.doris.cli.CommandLineHandler;
import com.alibaba.doris.cli.Option;
import com.alibaba.doris.client.DataStore;
import com.alibaba.doris.client.DataStoreFactory;
import com.alibaba.doris.client.DataStoreFactoryImpl;
import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.ConnectionFactory;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.KeyFactory;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class PressureTestingTool extends CommandLineHandler {

    /**
     * @param args
     */
    public static void main(String[] args) {
        PressureTestingTool pressureTestingTool = new PressureTestingTool();
        pressureTestingTool.handle(args);
        System.exit(0);
    }

    public PressureTestingTool() {
        options.add(new Option("-c", "config", "Location of the config file.", false, true));
        options.add(new Option("-threads", "thread", "Setting the thread number for this test."));
        options.add(new Option("-s", "start", "Setting the start position of key."));
        options.add(new Option("-e", "end", "Setting the end position of key."));
        options.add(new Option("-ns", "namespace", "Setting the namespace."));
        options.add(new Option("-repeat", "repeat", "Setting the repeat times for each thread."));
        options.add(new Option("-ip", "ip", "Setting the ipaddress for remote dataserver.", false, true));
        options.add(new Option("-kp", "ip", "Setting the key prefix."));
        options.add(new Option("-op", "op", "Setting the operation which we want to execute. 'get' 'put'", false, true));
        options.add(new Option("-vl", "vl", "Setting the length of value.", false, true));
        options.add(new Option("-ssf", "ssf", "Use single store factory.", false, true));
    }

    @Override
    public void handleCommand() {
        if (config != null) {
            dataStoreFactory = new DataStoreFactoryImpl(config);
        }
        startGate = new CountDownLatch(1);
        endGate = new CountDownLatch(threads);

        ExecutorService threadFactory = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            if (op == null || (!"put".equals(op))) {
                if (config == null) {
                    threadFactory.execute(new SimpleGetThread());
                } else {
                    threadFactory.execute(new GetThread());
                }
            } else {
                threadFactory.execute(new PutThread());
            }
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
    }

    @Override
    public void prepareParameters() {
        threads = commandLine.getInt("-threads");
        config = commandLine.getValue("-c");
        start = commandLine.getInt("-s");
        end = commandLine.getInt("-e");
        namespace = commandLine.getValue("-ns");
        repeat = commandLine.getInt("-repeat");
        ip = commandLine.getValue("-ip");
        kp = commandLine.getValue("-kp");
        op = commandLine.getValue("-op");
        vl = commandLine.getInt("-vl");
        if ("true".equalsIgnoreCase(commandLine.getValue("-ssf"))) {
            useSingleStoreFactory = true;
        }

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

    private class GetThread implements Runnable {

        public void run() {
            DataStoreFactory dsf = null;
            if (useSingleStoreFactory) {
                dsf = dataStoreFactory;
            } else {
                dsf = new DataStoreFactoryImpl(config);
            }

            DataStore dataStore = dsf.getDataStore(namespace);
            Object value = null;
            long start = 0;
            int loops = repeat;

            try {
                startGate.await();
                while (loops-- > 0) {
                    start = System.currentTimeMillis();
                    value = dataStore.get(kp + randomPosition());
                    timeCounter.addAndGet(System.currentTimeMillis() - start);
                    countCounter.incrementAndGet();
                    if (value == null) {
                        errorCounter.incrementAndGet();
                    }
                }
                endGate.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private class SimpleGetThread implements Runnable {

        public void run() {
            ConnectionFactory factory = ConnectionFactory.getInstance();
            String[] ips = ip.split(":");

            InetSocketAddress address = new InetSocketAddress(ips[0], Integer.valueOf(ips[1]));
            Connection connection = factory.getConnection(address);
            connection.open();
            Object value = null;
            long start = 0;
            int loops = repeat;

            try {
                startGate.await();
                while (loops-- > 0) {
                    start = System.currentTimeMillis();
                    value = connection.get(getKey(kp + String.valueOf(randomPosition()))).get();
                    timeCounter.addAndGet(System.currentTimeMillis() - start);
                    countCounter.incrementAndGet();
                    if (value == null) {
                        errorCounter.incrementAndGet();
                    }
                }
                endGate.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.close();
            }

        }

        private Key getKey(String key) {
            return KeyFactory.createKey(Integer.valueOf(namespace), key, algorithm.getVirtualByKey(namespace + ":"
                                                                                                   + key));
        }
    }

    private class PutThread implements Runnable {

        public void run() {
            DataStoreFactory dsf = null;
            if (useSingleStoreFactory) {
                dsf = dataStoreFactory;
            } else {
                dsf = new DataStoreFactoryImpl(config);
            }

            DataStore dataStore = dsf.getDataStore(namespace);
            long start = 0;
            int loops = repeat;

            try {
                startGate.await();
                while (loops-- > 0) {
                    start = System.currentTimeMillis();
                    dataStore.put(kp + randomPosition(), getValue());
                    timeCounter.addAndGet(System.currentTimeMillis() - start);
                    countCounter.incrementAndGet();
                }
                endGate.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String getValue() {
            if (null == value) {
                StringBuilder sb = new StringBuilder(vl);
                for (int i = 0; i < vl; i++) {
                    sb.append("V");
                }
                value = sb.toString();
            }
            return value;
        }

        private String value;
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

    private int randomPosition() {
        return start + RandomUtils.nextInt(length);
    }

    VpmRouterAlgorithm       algorithm             = new VpmRouterAlgorithm(1, 10000);
    private int              threads;
    private int              start;
    private int              end;
    private int              length;
    private String           namespace;
    private int              repeat;
    private AtomicLong       timeCounter           = new AtomicLong();
    private AtomicLong       countCounter          = new AtomicLong();
    private AtomicInteger    errorCounter          = new AtomicInteger();
    private CountDownLatch   startGate;
    private CountDownLatch   endGate;
    private String           config;
    private String           ip;
    private String           kp;
    private int              vl;
    private String           op;
    private boolean          useSingleStoreFactory = false;
    private DataStoreFactory dataStoreFactory;
}

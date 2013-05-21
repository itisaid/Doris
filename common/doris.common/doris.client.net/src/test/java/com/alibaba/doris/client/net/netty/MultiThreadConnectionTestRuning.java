package com.alibaba.doris.client.net.netty;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.ConnectionFactory;
import com.alibaba.doris.client.net.NetException;
import com.alibaba.doris.client.net.OperationFuture;
import com.alibaba.doris.client.net.command.CheckCommand.CheckType;
import com.alibaba.doris.client.net.command.result.CheckResult;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.KeyFactory;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.ValueFactory;
import com.alibaba.doris.common.data.impl.NullValueImpl;
import com.alibaba.doris.common.data.util.ByteUtils;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class MultiThreadConnectionTestRuning {

    public static void main(String[] args) {
        ConnectionFactory factory = ConnectionFactory.getInstance();
        InetSocketAddress remoteAddress = new InetSocketAddress("127.0.0.1", 9000);
        Connection con = factory.getConnection(remoteAddress);
        try {
            con.open();
            runTask(con);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            con.close();
            factory.releaseResources();
        }
    }

    private static void runTask(final Connection client) throws InterruptedException {
        final int len = 10000;
        int nThreads = 100;
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < 512; i++) {
            value.append("1");
        }

        ExecutorService executor = Executors.newFixedThreadPool(nThreads);

        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(nThreads);

        for (int i = 0; i < nThreads; i++) {
            executor.execute(new Runnable() {

                public void run() {
                    Connection con = null;
                    try {
                        ConnectionFactory factory = ConnectionFactory.getInstance();
                        InetSocketAddress remoteAddress = new InetSocketAddress("127.0.0.1", 9000);
                        con = factory.getConnection(remoteAddress);
                        con.open();

                        Random random = new Random();
                        String key = Thread.currentThread().getName() + "_KEY_";
                        StringBuilder value = new StringBuilder();
                        int strLen = random.nextInt(512);
                        for (int j = 0; j < strLen; j++) {
                            value.append("1");
                        }

                        String v = value.toString();
                        int j = 0;
                        startGate.await();
                        for (int i = 0; i < len; i++) {
                            sizeCount.addAndGet(value.length());
                            Key keyObj = KeyFactory.createKey(1000, key + i, 0);
                            keyObj.setVNode(10);
                            Value valueObj = ValueFactory.createValue(ByteUtils.stringToByte(v),
                                                                      System.currentTimeMillis());
                            Boolean bReturn = con.put(keyObj, valueObj).get();
                            if (bReturn == false) {
                                putFailCount.incrementAndGet();
                                System.out.println("Put failed.");
                                continue;
                            }
                            // client.set("key_" + i, v + "1", 0);
                            OperationFuture<Value> vi = con.get(keyObj);
                            Value vnew = vi.get();
                            if (vnew == null) {
                                errCount.incrementAndGet();
                            }

                            if ((vnew instanceof NullValueImpl)) {
                                System.out.println("Null value:" + keyObj.getPhysicalKey());
                                nullCount.incrementAndGet();
                            }

                            if (j % 100 == 0) {
                                System.out.println("operation .nullCount:" + nullCount.get() + " Error Count:"
                                                   + errCount.get());
                            }
                            j++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        endGate.countDown();
                        con.close();
                    }

                }
            });
        }

        Thread.sleep(1000);
        long start = System.currentTimeMillis();
        startGate.countDown();
        endGate.await();
        long end = System.currentTimeMillis();

        executor.shutdown();

        long size = (sizeCount.get() * 2) / 1024;
        long total = end - start;
        Thread.sleep(100);
        System.out.println("-------------------" + client.getClass().getName() + "---------------");
        System.out.println("total time:" + total + " error count: (null)" + errCount.get() + " NullValue:"
                           + nullCount.get() + " put fail:" + putFailCount.get());
        System.out.println("size:" + size + "KB");
        System.out.println("len=" + len + " avg:" + total / len + "  size:" + size + "KB  " + (size) / ((total) / 1000)
                           + "K/S  total:" + total);
        System.out.println("-------------------end---------------");
    }

    public static void postGetAndSet(Connection connection, String key, String value) throws InterruptedException,
                                                                                     ExecutionException {
        sizeCount.addAndGet(value.length());

        Key keyObj = KeyFactory.createKey(100, key, 0);
        Value valueObj = ValueFactory.createValue(ByteUtils.stringToByte(value), System.currentTimeMillis());
        Boolean bReturn = connection.put(keyObj, valueObj).get();
        if (bReturn == false) {
            putFailCount.incrementAndGet();
            System.out.println("Put failed.");
            return;
        }
        // client.set("key_" + i, v + "1", 0);
        OperationFuture<Value> vi = connection.get(keyObj);
        Value vnew = vi.get();
        if (vnew == null) {
            errCount.incrementAndGet();
        }

        if ((vnew instanceof NullValueImpl)) {
            System.out.println("Null value:" + keyObj.getPhysicalKey());
            nullCount.incrementAndGet();
        }
    }

    private static void runTask0(final Connection client) throws InterruptedException {
        final int len = 100000;
        int nThreads = 10;
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < 512; i++) {
            value.append("1");
        }

        ExecutorService executor = Executors.newFixedThreadPool(nThreads);

        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(nThreads);

        for (int i = 0; i < nThreads; i++) {
            executor.execute(new Runnable() {

                public void run() {
                    try {
                        startGate.await();
                        for (int i = 0; i < len; i++) {
                            ConnectionFactory factory = ConnectionFactory.getInstance();
                            InetSocketAddress remoteAddress = new InetSocketAddress("127.0.0.1", 9000);
                            Connection con = factory.getConnection(remoteAddress);
                            try {
                                con.open();
                                if (con.isConnected() == false) {
                                    errCount.incrementAndGet();
                                }
                                CheckResult result = con.check(CheckType.CHECK_NORMAL_NODE).get(1, TimeUnit.SECONDS);
                                if (null == result) {
                                    nullCount.incrementAndGet();
                                }
                                putFailCount.incrementAndGet();
                            } catch (NetException net) {
                                errCount.incrementAndGet();
                                net.printStackTrace();
                                Thread.sleep(1000);
                            } finally {
                                con.close();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        endGate.countDown();
                    }

                }
            });
        }

        Thread.sleep(1000);
        long start = System.currentTimeMillis();
        startGate.countDown();
        endGate.await();
        long end = System.currentTimeMillis();

        executor.shutdown();

        long size = (sizeCount.get() * 2) / 1024;
        long total = end - start;
        Thread.sleep(100);
        System.out.println("-------------------" + client.getClass().getName() + "---------------");
        System.out.println("total time:" + total + " error count: (null)" + errCount.get() + " NullValue:"
                           + nullCount.get() + " put fail:" + putFailCount.get());
        System.out.println("size:" + size + "KB");
        System.out.println("len=" + len + " avg:" + total / len + "  size:" + size + "KB  " + (size) / ((total) / 1000)
                           + "K/S  total:" + total);
        System.out.println("-------------------end---------------");
    }

    private static AtomicInteger errCount     = new AtomicInteger();
    private static AtomicInteger nullCount    = new AtomicInteger();
    private static AtomicInteger putFailCount = new AtomicInteger();
    private static AtomicLong    sizeCount    = new AtomicLong();
}

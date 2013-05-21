package com.alibaba.doris.dataserver.store.log.db;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

import org.junit.Test;

import com.alibaba.doris.common.data.ActionPair;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.KeyImpl;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.common.router.virtual.VirtualRouterImpl;
import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.StorageConfig;
import com.alibaba.doris.dataserver.store.log.LogStorageDriver;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class LogStoragePressureTest extends TestCase {

    @Test
    public void testlogSroage() throws InterruptedException {
        LogStorageDriver driver = new LogStorageDriver();
        StorageConfig config = new StorageConfig();
        config.setPropertiesFile("log_storage_presure_test.properties");
        driver.init(config);
        ClumpConfigure clumpConfig = driver.getClumpConfigure();
        clumpConfig.setMaxFileSize(1024 * 1024 * 100);

        Storage storage = driver.createStorage();
        try {
            storage.open();
            runTask(storage);
            storage.close();
            storage.open();
            testRead(storage);
            storage.close();
            storage.open();
            testDelete(storage);
            storage.close();
        } finally {
            storage.close();
        }
        assertTrue("断言压力测试写入读取数据校验失败", errCount.get() == 0);
    }

    public static void testRead(Storage storage) {
        int count = 0, errorCount = 0;
        long start = System.currentTimeMillis();
        try {
            Iterator<Pair> iterator = storage.iterator();
            while (iterator.hasNext()) {
                Pair p = iterator.next();
                String key = p.getKey().getKey();
                if (p != null && p.getKey() != null) {
                    if (p instanceof ActionPair) {
                        if (((ActionPair) p).getActionType() == ActionPair.Type.SET) {
                            Value value = p.getValue();
                            String v = ByteUtils.byteToString(value.getValueBytes());
                            if (v.startsWith(key)) {
                                count++;
                                continue;
                            } else {
                                errorCount++;
                                System.out.println("count=" + count + "key=" + key + " error value:" + v);
                                continue;
                            }
                        }
                    }
                    count++;
                } else {
                    errorCount++;
                }
            }
        } catch (Throwable e) {
            System.out.println("  c:" + count + " error:" + errorCount);
            e.printStackTrace();
        }

        long time = System.currentTimeMillis() - start;
        if (time <= 0) {
            time = 1;
        }
        System.out.println("time(ms):" + time + "  c:" + count + " error:" + errorCount + " avg:" + (time)
                           / (count + errorCount));
    }

    private static void testDelete(Storage storage) {
        int[] vnodeList = ((VirtualRouterImpl) VirtualRouterImpl.getInstance()).getVirtualNode();
        ArrayList<Integer> list = new ArrayList<Integer>(vnodeList.length);
        for (int i = 0; i < vnodeList.length; i++) {
            list.add(vnodeList[i]);
        }

        assertTrue(storage.delete(list));

        try {
            Iterator<Pair> iterator = storage.iterator();
            assertFalse(iterator.hasNext());
        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        }
    }

    private static void runTask(final Storage storage) throws InterruptedException {
        final int len = 10000;
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
                        Random random = new Random();
                        String key = Thread.currentThread().getName() + "_KEY_";
                        startGate.await();
                        for (int i = 0; i < len; i++) {
                            StringBuilder value = new StringBuilder();
                            int strLen = random.nextInt(10240);
                            for (int j = 0; j < strLen; j++) {
                                value.append("1");
                            }

                            String k_ = key + i;
                            String v = k_ + value.toString();
                            sizeCount.addAndGet(value.length());
                            Key keyObj = new KeyImpl(101, k_, 0);
                            Value valueObj = new ValueImpl(ByteUtils.stringToByte(v), System.currentTimeMillis());
                            storage.set(keyObj, valueObj);
                            storage.delete(keyObj);
                        }
                    } catch (Exception e) {
                        fail(e.getLocalizedMessage());
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
        if (size <= 0) size = 1;
        long total = end - start;
        if (total <= 0) total = 1;

        Thread.sleep(100);
        System.out.println("-------------------" + storage.getClass().getName() + "---------------");
        System.out.println("total time:" + total + " error count: " + errCount.get());
        System.out.println("size:" + size + "KB" + " total:" + total);
        System.out.println("len=" + len * nThreads * 2 + " avg:" /* + total / len */+ "  size:" + size + "KB  " /*
                                                                                                                 * +
                                                                                                                 * (size
                                                                                                                 * ) /
                                                                                                                 * ((
                                                                                                                 * total
                                                                                                                 * ) /
                                                                                                                 * 1000)
                                                                                                                 */
                           + "K/S  total:" + total);
        System.out.println("-------------------end---------------");
    }

    protected static ClumpConfigure getClumpConfigure() {
        ClumpConfigure config = new ClumpConfigure();
        config.setPath(LogStoragePressureTest.class.getClassLoader().getResource("").getPath() + "test_data"
                       + File.separatorChar);
        config.setReadBufferSize(1024 * 512);
        config.setWriteBufferSize(1024 * 512);
        config.setWriteDirect(true);// 不缓存数据直接写入磁盘
        return config;
    }

    private static AtomicLong    sizeCount = new AtomicLong();
    private static AtomicInteger errCount  = new AtomicInteger();
}

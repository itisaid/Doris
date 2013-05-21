package com.alibaba.doris.admin.seb.configer;

import java.net.InetSocketAddress;
import java.util.Random;

import junit.framework.TestCase;

import org.junit.Test;

import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.ConnectionFactory;
import com.alibaba.doris.common.data.impl.KeyImpl;

/**
 * 为性能监控做数据准备
 */
public class TestClient extends TestCase{
    private static Random RANDOM = new Random();

    @Test
    public void test() {
        new Thread(new TestTask()).start();
        new Thread(new TestTask()).start();
        new Thread(new TestTask()).start();
        System.out.println("OK");
    }

    private void doExcute() {
        InetSocketAddress remoteAddress = new InetSocketAddress("127.0.0.1", 9000);

        Connection connection = ConnectionFactory.getInstance().getConnection(remoteAddress);

        try {
            connection.open();
            while (true) {
                Thread.sleep(10);
                connection.get(new KeyImpl(RANDOM.nextInt(100), "test" + RANDOM.nextInt(100),
                        RANDOM.nextInt(10)));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class TestTask implements Runnable {
        public void run() {
            doExcute();
        }
    }
}

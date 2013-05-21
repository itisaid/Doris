package com.alibaba.doris.dataserver.migrator.connection;

import static junit.framework.Assert.assertEquals;

import java.net.InetSocketAddress;

import org.junit.Assume;
import org.junit.Test;

import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.KeyImpl;
import com.alibaba.doris.common.data.impl.ValueImpl;

public class ThreadLocalConnectionTest {

    private String ip   = "10.20.157.187";
    private int    port = 9000;

    @Test
    public void testPut2() {

    }

    @Test
    public void tes1tPut() {
        Connection c = null;
        try {
            c = new ThreadLocalConnection(new InetSocketAddress(ip, port));
            c.open();
            KeyImpl key = new KeyImpl(0, "key1", 0);
            Value value = new ValueImpl("value1".getBytes());

            assertEquals(Boolean.TRUE, c.put(key, value).get());

            for (int i = 0; i < 10000; i++) {
                key = new KeyImpl(0, "key" + i, i);

                assertEquals(Boolean.TRUE, c.put(key, value).get());
            }
        } catch (Exception e) {
            System.err.println(String.format("ERROR: Can't connect to %s:%s", ip, port));
            Assume.assumeTrue(false);
        }
    }

}

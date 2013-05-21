package com.alibaba.doris.client.net;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.alibaba.doris.common.data.KeyFactory;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ConnectionFactoryTest extends TestCase {

    public void testGetInstence() {
        ConnectionFactory factory = ConnectionFactory.getInstance();
        assertNotNull(factory);
    }

    public void testGetConnection() {
        ConnectionFactory factory = ConnectionFactory.getInstance();
        InetSocketAddress address = new InetSocketAddress("10.20.153.84", 6004);
        Connection connection = factory.getConnection(address);
        assertNotNull(connection);
        connection.open();
        assertTrue(connection.isConnected());
        connection.close();
    }

    public void testBatchConnections() {
        ConnectionFactory factory = ConnectionFactory.getInstance();
        List<Connection> conList = new ArrayList<Connection>();
        for (int i = 0; i < 100; i++) {
            InetSocketAddress address = new InetSocketAddress("10.20.153.84", 6004);
            Connection connection = factory.getConnection(address);
            assertNotNull(connection);
            connection.open();
            conList.add(connection);
        }

        for (Connection con : conList) {
            con.close();
        }
    }

    public void testWrite() {
        ConnectionFactory factory = ConnectionFactory.getInstance();
        InetSocketAddress address = new InetSocketAddress("10.20.153.84", 6004);
        Connection connection = factory.getConnection(address);
        connection.open();
        connection.get(KeyFactory.createKey(1, "abc"));
        connection.close();
    }

    public void testBatchOpenAndCloseConnections() {
        ConnectionFactory factory = ConnectionFactory.getInstance();
        Connection before = null;
        for (int i = 0; i < 1000000; i++) {
            InetSocketAddress address = new InetSocketAddress("10.20.153.84", 6004);
            Connection connection = factory.getConnection(address);
            assertNotNull(connection);
            connection.open();

            if (null != before) {
                before.close();
            }
            before = connection;
        }
    }
}

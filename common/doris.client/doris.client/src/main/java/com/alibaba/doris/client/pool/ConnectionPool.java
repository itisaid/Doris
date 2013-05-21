package com.alibaba.doris.client.pool;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.ConnectionFactory;
import com.alibaba.doris.client.net.exception.ClientConnectionException;

public class ConnectionPool {

    private List<Connection> connectionHold;

    private AtomicInteger    counter = new AtomicInteger(0);

    private String           ip;
    private int              port;

    public ConnectionPool(String ip, int port, int poolSize) {
        this.ip = ip;
        this.port = port;

        connectionHold = new ArrayList<Connection>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            connectionHold.add(makeConnection(ip, port));
        }
    }

    public Connection getConnection() {
        int index = Math.abs(counter.incrementAndGet());
        // to skip Integer.MIN_VALUE; -2147483648 abs=-2147483648
        if (index < 0) {
            index = 0;
        }

        index = (int) ((index) % connectionHold.size());
        Connection c = connectionHold.get(index);

        if (false == checkConnection(c)) {
            synchronized (this) {
                c = connectionHold.get(index);
                if (!c.isConnected()) {
                    // 如果老的connection无法重新建立连接，则关闭老连接
                    closeConnection(c);
                    // 新建一个connection
                    c = makeConnection(ip, port);

                    if (checkConnection(c)) {
                        connectionHold.set(index, c);
                    } else {
                        throw new ClientConnectionException(" Connecting remote server failed. The remote data server:"
                                                            + ip + ":" + port);
                    }
                }
            }
        }

        return c;
    }

    /**
     * 延迟建立连接，只有当连接真正使用到的时候才开始建立；
     * 
     * @param connection
     * @return
     */
    private boolean checkConnection(Connection connection) {
        synchronized (connection) {
            if (!connection.isConnected()) {
                return openConnection(connection);
                // if (!bResult) {
                // closeConnection(connection);
                // }
            }
        }

        return true;
    }

    private boolean openConnection(Connection connection) {
        try {
            connection.open();
        } catch (Exception ignore) {
            logger.error("Open connection failed. ", ignore);
            return false;
        }

        return true;
    }

    private boolean closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (Exception ignore) {
            logger.error("Close connection failed. ", ignore);
            return false;
        }

        return true;
    }

    public void closeAll() {
        for (Connection c : connectionHold) {
            closeConnection(c);
        }
    }

    private Connection makeConnection(String ip, int port) {
        ConnectionFactory factory = ConnectionFactory.getInstance();
        InetSocketAddress remoteAddress = new InetSocketAddress(ip, port);
        Connection connection = factory.getConnection(remoteAddress);
        return connection;
    }

    private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);
}

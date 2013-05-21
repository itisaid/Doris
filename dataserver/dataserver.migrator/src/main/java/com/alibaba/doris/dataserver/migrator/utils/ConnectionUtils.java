package com.alibaba.doris.dataserver.migrator.utils;

import java.net.InetSocketAddress;

import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.ConnectionFactory;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public final class ConnectionUtils {

    private ConnectionUtils() {

    }

    public static Connection getConnection(String machineName) {
        String[] pId = machineName.split(":");
        String ip = pId[0];
        int port = Integer.valueOf(pId[1]).intValue();

        // 和目标机器建立连接
        InetSocketAddress address = new InetSocketAddress(ip, port);
        Connection con = factory.getConnection(address);
        con.open();

        return con;
    }

    public static void releaseConnection(Connection connection) {
        connection.close();
    }

    private static final ConnectionFactory factory = ConnectionFactory.getInstance();
}

package com.alibaba.doris.client;

import java.util.Properties;

import org.apache.commons.lang.math.NumberUtils;

import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.DataSource;
import com.alibaba.doris.client.pool.ConnectionPool;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class DefaultDataSourceImpl implements DataSource {

    public Connection getConnection() {
        return pool.getConnection();
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getNo() {
        return this.no;
    }

    public void setConfigProperties(Properties properties) {
        this.properties = properties;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void initConfig() {
        int connectionPerNode = 8;
        if (properties != null) {
            connectionPerNode = NumberUtils.toInt(
                    properties.getProperty("doris.config.client.connectionpernode"), 8);
        }

        pool = new ConnectionPool(ip, port, connectionPerNode);
    }

    public void close() {
        pool.closeAll();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        
        if (this == obj) {
            return true;
        }

        if (obj instanceof DefaultDataSourceImpl) {
            DefaultDataSourceImpl v = (DefaultDataSourceImpl) obj;
            if (this.ip.equals(v.ip) && (this.port == v.port) && (this.no == v.no) && (this.sequence == v.sequence)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + ip.hashCode();
        result = 31 * result + port;
        result = 31 * result + no;
        result = 31 * result + sequence;
        return result;
    }
    
    private String         ip;
    private int            port;
    private int            sequence;
    private int            no;
    private Properties     properties;

    private ConnectionPool pool;

    @Override
    public String toString() {
        return String.format("[DataSource :  %d.%d, ip=%s, port=%d]", sequence, no, ip, port);
    }

}

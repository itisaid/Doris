package com.alibaba.doris.client.tools;

import java.net.InetSocketAddress;

import org.apache.commons.lang.math.RandomUtils;

import com.alibaba.doris.algorithm.vpm.VpmRouterAlgorithm;
import com.alibaba.doris.cli.Option;
import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.ConnectionFactory;
import com.alibaba.doris.client.net.OperationFuture;
import com.alibaba.doris.common.data.Value;

/*
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class ConnectionFactoryTool extends PressureTestingBase {

    public static void main(String[] args) {
        ConnectionFactoryTool pressureTestingTool = new ConnectionFactoryTool();
        pressureTestingTool.handle(args);
        System.exit(0);
    }

    @Override
    protected void release() {
        if (null != factory) {
            factory.releaseResources();
        }
    }

    @Override
    public Runnable createRunnableTask(int index, int count) {
        if (op != null) {
            if ("get".equalsIgnoreCase(op)) {
                return new SimpleConnectionGet();
            }
        }
        return new SimpleConnectionPut();
    }

    protected class SimpleConnectionGet extends SimpleConnectionPut {

        @Override
        public boolean execute(int index, int length) {
            OperationFuture<Value> result = connection.get(getKey(kp + RandomUtils.nextInt(len())));
            try {
                return result.get().getValueBytes() != null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    protected class SimpleConnectionPut extends PressureTestingBase.BaseRunnableTask {

        @Override
        public void prepare() {
            factory = ConnectionFactory.getInstance();
            String[] ips = ip.split(":");

            InetSocketAddress address = new InetSocketAddress(ips[0], Integer.valueOf(ips[1]));
            connection = factory.getConnection(address);
            connection.open();
            value = getValueObject(getValue());
        }

        @Override
        public void destory() {
            connection.close();
        }

        @Override
        public boolean execute(int index, int length) {
            OperationFuture<Boolean> result = connection.put(getKey(kp + index), value);
            try {
                return result.get().booleanValue();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        Connection connection;
        Value      value;
    }

    ConnectionFactory factory;

    public ConnectionFactoryTool() {
        options.add(new Option("-ip", "ip", "Setting the ipaddress for remote dataserver.", false, true));
        options.add(new Option("-kp", "ip", "Setting the key prefix."));
        options.add(new Option("-op", "op", "Setting the operation which we want to execute. 'get' 'put'", false, true));
    }

    @Override
    public void prepareParameters() {
        super.prepareParameters();
        ip = commandLine.getValue("-ip");
        kp = commandLine.getValue("-kp");
        op = commandLine.getValue("-op");
    }

    VpmRouterAlgorithm algorithm = new VpmRouterAlgorithm(1, 10000);
    private String     ip;
    private String     kp;
    private String     op;
}

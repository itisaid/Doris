/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client.tools;

import java.net.InetSocketAddress;

import com.alibaba.doris.cli.CommandExecutionException;
import com.alibaba.doris.cli.CommandLineHandler;
import com.alibaba.doris.cli.Option;
import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.ConnectionFactory;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.KeyImpl;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.common.route.MockVirtualRouter;
import com.alibaba.doris.common.route.VirtualRouter;

/**
 * NodeCheckTool
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-7-27
 */
public class NodeCheckTool extends CommandLineHandler {

    private String ip;
    private int    port;
    private int    vn;
    private String o;
    private String key;
    private String value;
    private String verbose;

    public NodeCheckTool() {
        options.add(new Option("-ip", "IP", "DataServer IP"));
        options.add(new Option("-port", "Port", "DataServer Port"));
        options.add(new Option("-vn", "VirtualNumber", "Virtual Number to routing.", false, true));
        options.add(new Option("-o", "KV Operation", "put, get, or delete"));
        options.add(new Option("-k", "Key", "Format ns:logicKey, e.g. 1:aaa, 101:XYZ001 "));
        options.add(new Option("-v", "Value ", "e.g. A0012", false, true));
        options.add(new Option("-verbose", "Verbose", "true/false, Print args and values ", false,
                true));
        options.add(new Option("-h", "Help", "Print command usage", false, false));
    }

    public void prepareParameters() {
        ip = commandLine.getValue("-ip");
        port = commandLine.getInt("-port");
        vn = commandLine.getInt("-vn");
        o = commandLine.getValue("-o");
        key = commandLine.getValue("-k");
        value = commandLine.getValue("-v");

        if ("true".equals(verbose)) {
            System.out.println(" ip: " + ip);
            System.out.println(" port: " + port);
            System.out.println(" v: " + vn);
            System.out.println(" o: " + o);
            System.out.println(" k: " + key);
            System.out.println(" v: " + value);
        }
    }

    public void handleCommand() {
        int vnode = -1;
        if (vn != 0) {
            VirtualRouter virtualRouter = new MockVirtualRouter(vn);
            vnode = virtualRouter.findVirtualNode(key);
        }
        Connection connection = null;
        ConnectionFactory factory = ConnectionFactory.getInstance();
        InetSocketAddress remoteAddress = new InetSocketAddress(ip, port);
        connection = factory.getConnection(remoteAddress);
        connection.open();

        int commaIndex = key.indexOf(":");
        String ns = key.substring(0, commaIndex);
        String logicKey = key.substring(commaIndex + 1);
        int namespaceId = Integer.valueOf(ns);
        Key key1 = new KeyImpl(namespaceId, logicKey, vnode);

        Value valueObj;
        try {
            if ("put".equals(o)) {

                Value value1 = new ValueImpl(value.getBytes(), System.currentTimeMillis());
                boolean result = connection.put(key1, value1).get();

                System.out.println(String.format("Put succeed? %s, key=%s, value=%s ", result, key,
                        value));

            } else if ("get".equals(o)) {

                valueObj = connection.get(key1).get();

                if (valueObj == null || valueObj.getValueBytes() == null) {
                    System.out.println(String.format("Not Found: key=%s", key));
                } else {
                    String svalue = new String(valueObj.getValueBytes());
                    System.out.println(String.format("Found: key=%s, value=%s ", key, svalue));
                }
            } else if ("delete".equals(o)) {
                boolean result = connection.delete(key1).get();
                System.out.println(String.format("Delete succeed? %s, key=%s ", result, key));
            }

        } catch (Exception e) {
            throw new CommandExecutionException("Fail to execute command: " + e.getMessage(), e);
        } finally {

            if (connection != null) {
                connection.close();
            }
            factory.releaseResources();
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getVn() {
        return vn;
    }

    public void setVn(int vn) {
        this.vn = vn;
    }

    public String getO() {
        return o;
    }

    public void setO(String o) {
        this.o = o;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static void main(String[] args) {
        //		String[] demoArgs = new String[]{"-ip","10.20.144.91","-port","9000","-vn","10000","-o","put","-k","24:XYZ001","-v","A0012"};
        NodeCheckTool checkTool = new NodeCheckTool();
        checkTool.handle(args);

        //		demoArgs = new String[]{"-ip","10.20.144.91","-port","9000","-vn","10000","-o","get","-k","24:XYZ001"};
        //		NodeCheckTool checkTool2 = new NodeCheckTool();
        //		checkTool2.handle( demoArgs );

        System.exit(0);
    }
}

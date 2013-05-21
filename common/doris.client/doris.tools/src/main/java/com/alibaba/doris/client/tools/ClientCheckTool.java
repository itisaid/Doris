/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client.tools;

import com.alibaba.doris.cli.CommandExecutionException;
import com.alibaba.doris.cli.CommandLineHandler;
import com.alibaba.doris.cli.Option;
import com.alibaba.doris.client.DataStore;
import com.alibaba.doris.client.DataStoreFactory;
import com.alibaba.doris.client.DataStoreFactoryImpl;

/**
 * NodeCheckTool
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-7-27
 */
public class ClientCheckTool extends CommandLineHandler {

    protected String config;
    protected String o;
    protected String namespace;
    protected String logicKey;
    protected String value;
    protected String verbose;

    public ClientCheckTool() {
        options.add(new Option("-c", "config", "Location of the config file."));
        options.add(new Option("-o", "KV Operation", "put, get, or delete", false, true));
        options.add(new Option("-ns", "Namespace", "name of namespace "));
        options.add(new Option("-k", "Key", "LogicKey without namespace ,e.g. XYZ001"));
        options.add(new Option("-v", "Value ", "e.g. A0012", false, true));
        options.add(new Option("-h", "Help", "Print command usage", false, false));
    }

    public void prepareParameters() {
        config = commandLine.getValue("-c");
        o = commandLine.getValue("-o");
        namespace = commandLine.getValue("-ns");
        logicKey = commandLine.getValue("-k");
        value = commandLine.getValue("-v");

        if ("true".equals(verbose)) {
            System.out.println(" v: " + config);
            System.out.println(" o: " + o);
            System.out.println(" ns: " + namespace);
            System.out.println(" k: " + logicKey);
            System.out.println(" v: " + value);
        }
    }

    public static void main(String[] args) {
        ClientCheckTool checkTool = new ClientCheckTool();
        checkTool.handle(args);
        System.exit(0);
    }

    public void handleCommand() {
        DataStoreFactory dataStoreFactory = new DataStoreFactoryImpl(config);
        DataStore dataStore = dataStoreFactory.getDataStore(namespace);
        Object valueObj;
        try {
            if ("put".equals(o)) {
                boolean result = dataStore.put(logicKey, value);
                System.out.println(String.format("Put succeed? %s, key=%s, value=%s ", result,
                        logicKey, value));

            } else if ("get".equals(o)) {
                valueObj = dataStore.get(logicKey);
                if (valueObj == null) {
                    System.out.println(String.format("Null value: key=%s", logicKey));
                } else {
                    System.out.println(String
                            .format("Found: key=%s, value=%s ", logicKey, valueObj));
                }
            } else if ("delete".equals(o)) {
                boolean result = dataStore.delete(logicKey);
                System.out.println(String.format("Delete succeed? %s, key=%s ", result, logicKey));
            }

        } catch (Exception e) {
            throw new CommandExecutionException("Fail to execute command: " + e.getMessage(), e);
        }
    }

    public String getO() {
        return o;
    }

    public void setO(String o) {
        this.o = o;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the config
     */
    public String getConfig() {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(String config) {
        this.config = config;
    }

    /**
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @param namespace the namespace to set
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * @return the logicKey
     */
    public String getLogicKey() {
        return logicKey;
    }

    /**
     * @param logicKey the logicKey to set
     */
    public void setLogicKey(String logicKey) {
        this.logicKey = logicKey;
    }

    /**
     * @return the verbose
     */
    public String getVerbose() {
        return verbose;
    }

    /**
     * @param verbose the verbose to set
     */
    public void setVerbose(String verbose) {
        this.verbose = verbose;
    }

}

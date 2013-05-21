/**
 * Project: doris.tools-0.1.0-SNAPSHOT File Created at 2011-8-1 $Id$ Copyright 1999-2100 Alibaba.com Corporation
 * Limited. All rights reserved. This software is the confidential and proprietary information of Alibaba Company.
 * ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.doris.client.tools;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.alibaba.doris.client.DataSourceRouter;
import com.alibaba.doris.client.DataStoreFactory;
import com.alibaba.doris.client.DataStoreFactoryImpl;
import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.DataSource;
import com.alibaba.doris.client.net.exception.ClientConnectionException;
import com.alibaba.doris.common.StoreNodeSequenceEnum;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.KeyFactory;
import com.alibaba.doris.common.data.Value;

/**
 * TODO Comment of DataCheckTool
 * 
 * @author luyi.huangly
 */
public class DataCheckTool extends ClientCheckTool {

    public static void main(String[] args) {
        DataCheckTool dataCheckTool = new DataCheckTool();
        dataCheckTool.handle(args);
        System.exit(0);
    }

    @Override
    public void handleCommand() {
        DataStoreFactory dataStoreFactory = new DataStoreFactoryImpl(config);
        DataSourceRouter dataSourceRouter = dataStoreFactory.getDataSourceManager().getDataSourceRouter();
        int namespaceId = dataStoreFactory.getNamespaceManager().getNamespace(namespace).getId();

        Map<String, List<DataSource>> allDataSources = dataSourceRouter.getAllDataSources();

        Set<String> seqList = allDataSources.keySet();
        Value valueObj = null;
        String phId = null;
        Connection connection = null;
        for (String seq : seqList) {
            if (seq.equals((Integer.toString(StoreNodeSequenceEnum.TEMP_SEQUENCE.getValue())))) {
                continue;
            }
            List<DataSource> seqDataSources = allDataSources.get(seq);
            for (DataSource dataSource : seqDataSources) {
                try {
                    connection = (Connection) dataSource.getConnection();
                } catch (ClientConnectionException e) {
                    System.out.println(String.format("Connect Exception in seq %s : PhId %s ", seq, phId));
                    continue;
                }
                phId = dataSourceRouter.getStoreNodeOf(dataSource).getPhId();
                Key key1 = KeyFactory.createKey(namespaceId, logicKey, -1);
                if (connection != null) {
                    try {
                        valueObj = connection.get(key1).get();
                    } catch (InterruptedException e) {
                        System.out.println(String.format("Connect Exception in seq %s : PhId %s ", seq, phId));
                    } catch (ExecutionException e) {
                        System.out.println(String.format("Connect Exception in seq %s : PhId %s ", seq, phId));
                    }
                    if (valueObj == null || valueObj.getValueBytes() == null) {
                        System.out.println(String.format("Not Found: key=%s in seq %s : PhId %s ", logicKey, seq, phId));
                    } else {
                        String svalue = new String(valueObj.getValueBytes());
                        System.out.println(String.format("Found: key=%s, value=%s in seq %s : PhId %s", logicKey,
                                                         svalue, seq, phId));
                    }

                }
                dataSource.close();
            }
        }
    }
}

/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;

import com.alibaba.doris.client.mock.DorisClientMockKvConnection;
import com.alibaba.doris.client.mock.MockDataStoreFactoryImpl;
import com.alibaba.doris.client.net.DataSource;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.KeyFactory;
import com.alibaba.doris.common.data.Value;

/**
 * DataStoreTest
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-6
 */
public class DataStoreCompressTest extends TestCase {

    protected String           configUrl;
    protected DataStoreFactory dataStoreFactory;
    protected DataStore        dataStore;

    protected int              logId = 3;

    public DataStoreCompressTest() {
        configUrl = "mock-doris-client.properties";
        dataStoreFactory = new MockDataStoreFactoryImpl(configUrl);
        dataStore = dataStoreFactory.getDataStore("User");
    }

    public byte[] buildTestData(int n) {

        byte[] bigData = new byte[n];

        for (int i = 0; i < n; i++) {
            if (i % 5 == 0) bigData[i] = 'a';
            else if (i % 5 == 1) bigData[i] = 'b';
            else if (i % 5 == 2) bigData[i] = 'c';
            else if (i % 5 == 3) bigData[i] = 'd';
            else {
                bigData[i] = 'e';
            }

        }
        return bigData;
    }

    public byte[] buildComplexData(int n) {
        byte[] bigData = new byte[n];
        for (int i = 0; i < n; i++) {
            bigData[i] = getRandomASC();
        }
        return bigData;
    }

    public byte getRandomASC() {
        for (;;) {
            Integer k = Integer.valueOf((int) (Math.random() * 126));
            if (k > 32) {
                return k.byteValue();
            }
        }
    }

    /**
     * 验证value大小超限
     */
    public void testPutValueValidate() {
        String key = "U001";
        byte[] bigValue = buildComplexData(2000000);
        String bigValueStr = new String(bigValue);

        try {
            dataStore.put(key, bigValueStr);
            fail("illegal argument.");
        } catch (IllegalArgumentException e) {

        }
    }

    /**
     * Test Name: value无压缩大小为n+1. Expected Result: bdb中存储的是压缩后的.取出数据正确
     */
    public void testPut() throws InterruptedException, ExecutionException {
        String key = "U001";

        byte[] bigValue = buildTestData(1990);
        String bigValueStr = new String(bigValue);
        dataStore.put(key, bigValueStr);

        Map<String, List<DataSource>> allDataSources = dataStoreFactory.getDataSourceManager().getDataSourceRouter().getAllDataSources();

        if (allDataSources.size() > 0) {
            List<DataSource> seqDataSources = allDataSources.get("1");

            DataSource dataSource = seqDataSources.get(logId);

            DorisClientMockKvConnection mockKvConnection = (DorisClientMockKvConnection) dataSource.getConnection();

            Key pKey = KeyFactory.createKey(101, key, 0);
            Value pValue = mockKvConnection.get(pKey).get();

            int storedBytes = pValue.getValueBytes().length;
            assertTrue("Compressed should be less than orignal.", storedBytes < bigValueStr.length());
        }
    }

    public void testPut2() throws InterruptedException, ExecutionException {
        String key = "U001";

        byte[] bigValue = buildTestData(1990);
        String bigValueStr = new String(bigValue);
        dataStore.put(key, bigValueStr);

        String result = (String) dataStore.get(key);

        assertEquals("Decompressed Value.", bigValueStr, result);
    }

    /**
     * Test Name: value无压缩大小为n-1. Expected Result: bdb中存储的是无压缩的.取出数据正确
     */
    public void testPut1023() throws InterruptedException, ExecutionException {
        String key = "U001";

        byte[] bigValue = buildTestData(1023);
        String bigValueStr = new String(bigValue);
        dataStore.put(key, bigValueStr);

        Map<String, List<DataSource>> allDataSources = dataStoreFactory.getDataSourceManager().getDataSourceRouter().getAllDataSources();

        if (allDataSources.size() > 0) {
            List<DataSource> seqDataSources = allDataSources.get("1");

            DataSource dataSource = seqDataSources.get(logId);

            DorisClientMockKvConnection mockKvConnection = (DorisClientMockKvConnection) dataSource.getConnection();

            Key pKey = KeyFactory.createKey(101, key, 0);
            Value pValue = mockKvConnection.get(pKey).get();

            int storedBytes = pValue.getValueBytes().length;
            assertTrue("no compresse.", storedBytes == bigValueStr.length());
        }
    }

    /**
     * Test Name: value无压缩大小为n. Expected Result: bdb中存储的是无压缩的.取出数据正确
     */
    public void testPut1024() throws InterruptedException, ExecutionException {
        String key = "U001";

        byte[] bigValue = buildTestData(1024);
        String bigValueStr = new String(bigValue);
        dataStore.put(key, bigValueStr);

        Map<String, List<DataSource>> allDataSources = dataStoreFactory.getDataSourceManager().getDataSourceRouter().getAllDataSources();

        if (allDataSources.size() > 0) {
            List<DataSource> seqDataSources = allDataSources.get("1");

            DataSource dataSource = seqDataSources.get(logId);

            DorisClientMockKvConnection mockKvConnection = (DorisClientMockKvConnection) dataSource.getConnection();

            Key pKey = KeyFactory.createKey(101, key, 0);
            Value pValue = mockKvConnection.get(pKey).get();

            int storedBytes = pValue.getValueBytes().length;
            assertTrue("no compresse.", storedBytes == bigValueStr.length());

            String result = (String) dataStore.get(key);
            assertEquals("Decompressed Value.", bigValueStr, result);
        }
    }

    /**
     * Test Name: value无压缩大小为n+1. Expected Result: bdb中存储的是压缩后的.取出数据正确
     */
    public void testPut1025() throws InterruptedException, ExecutionException {
        String key = "U001";

        byte[] bigValue = buildTestData(1025);
        String bigValueStr = new String(bigValue);
        dataStore.put(key, bigValueStr);

        Map<String, List<DataSource>> allDataSources = dataStoreFactory.getDataSourceManager().getDataSourceRouter().getAllDataSources();

        if (allDataSources.size() > 0) {
            List<DataSource> seqDataSources = allDataSources.get("1");

            DataSource dataSource = seqDataSources.get(logId);

            DorisClientMockKvConnection mockKvConnection = (DorisClientMockKvConnection) dataSource.getConnection();

            Key pKey = KeyFactory.createKey(101, key, 0);
            Value pValue = mockKvConnection.get(pKey).get();

            int storedBytes = pValue.getValueBytes().length;
            assertTrue("Compressed should be less than orignal.", storedBytes < bigValueStr.length());

            String result = (String) dataStore.get(key);

            assertEquals("Decompressed Value.", bigValueStr, result);
        }
    }

    /**
     * Test puts n>1024 压缩正确
     * 
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public void testPuts2000() throws InterruptedException, ExecutionException {
        String key = "U001";
        int size = 4;
        byte[] bigValue = buildTestData(2000);
        String bigValueStr = new String(bigValue);
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < size; i++) {
            map.put(key + i, bigValueStr);
        }
        dataStore.puts(map);

        Map<String, List<DataSource>> allDataSources = dataStoreFactory.getDataSourceManager().getDataSourceRouter().getAllDataSources();

        int count = 0;
        if (allDataSources.size() > 0) {
            List<DataSource> seqDataSources = allDataSources.get("1");

            for (int i = 0; i < size; i++) {
                for (DataSource dataSource : seqDataSources) {
                    DorisClientMockKvConnection mockKvConnection = (DorisClientMockKvConnection) dataSource.getConnection();
                    Map<Key, Value> storage = mockKvConnection.getStorage();

                    for (Entry<Key, Value> e : storage.entrySet()) {

                        Key pk = e.getKey();
                        if (pk.getKey().equals(key + i)) {
                            int storedBytes = e.getValue().getValueBytes().length;
                            assertTrue("Compressed should be less than orignal.", storedBytes < bigValueStr.length());
                            count++;
                        }
                    }

                }

                String result = (String) dataStore.get(key + i);

                assertEquals("Decompressed Value.", bigValueStr, result);
            }
        }
        assertEquals(size + " data were got.", count, size);
    }

    /**
     * 验证value超限
     * 
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public void testPuts2000000() throws InterruptedException, ExecutionException {
        String key = "U001";
        int size = 4;
        byte[] bigValue = buildComplexData(2000000);
        String bigValueStr = new String(bigValue);
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < size; i++) {
            map.put(key + i, bigValueStr);
        }
        try {
            dataStore.puts(map);
            fail("illegal argument.");
        } catch (IllegalArgumentException e) {

        }
    }

}

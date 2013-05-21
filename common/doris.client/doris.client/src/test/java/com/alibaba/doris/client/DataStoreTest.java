/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.doris.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;

import com.alibaba.doris.client.mock.MockDataStoreFactoryImpl;

/**
 * DataStoreTest
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-6
 */
public class DataStoreTest extends TestCase {

    protected String           configUrl;
    protected DataStoreFactory dataStoreFactory;
    protected DataStore        dataStore;

    public DataStoreTest() {
        configUrl = "mock-doris-client.properties";
        dataStoreFactory = new MockDataStoreFactoryImpl(configUrl);
        dataStore = dataStoreFactory.getDataStore("User");
    }

    /**
     * Test Name: put，get逻辑校验. Expected Result: 取出数据正确
     */
    public void testPut() {
        String key = "key001";
        String value = "value001";
        dataStore.put(key, value);
        String strValue = (String) dataStore.get(key);
        assertEquals(value, strValue);
    }

    /**
     * Test Name: key值为空. Expected Result: Client端报错
     */
    public void testPut2() {

        String key = null;
        String value = "value001";
        try {
            dataStore.put(key, value);
            fail("Null key!");
        } catch (IllegalArgumentException e) {

        }
    }
    
    public void testSinglePuts(){

        String key1 = "key1";
        String value1 = "dddes";


        Map<String, String> map = new HashMap<String, String>();
        map.put(key1, value1);

        dataStore.puts(map);

        String strValue1 = (String) dataStore.get(key1);
        assertEquals(value1, strValue1);

    
    }

    public void testPuts() {
        String key1 = "key1";
        String value1 = "value1";
        String key2 = "key2";
        String value2 = "value2";

        Map<String, String> map = new HashMap<String, String>();
        map.put(key1, value1);
        map.put(key2, value2);

        dataStore.puts(map);

        String strValue1 = (String) dataStore.get(key1);
        assertEquals(value1, strValue1);

        String strValue2 = (String) dataStore.get(key2);
        assertEquals(value2, strValue2);

        map = new HashMap<String, String>();
        map.put(key1, value1 + "1");
        dataStore.puts(map);
        strValue1 = (String) dataStore.get(key1);
        assertEquals(value1 + "1", strValue1);
    }

    public void testPutsValuenull() {
        String key1 = "key1";
        String value1 = "value1";
        String key2 = "key2";
        String value2 = null;

        Map<String, String> map = new HashMap<String, String>();
        map.put(key1, value1);
        map.put(key2, value2);

        dataStore.puts(map);

        String strValue1 = (String) dataStore.get(key1);
        assertEquals(value1, strValue1);

        String strValue2 = (String) dataStore.get(key2);
        assertNull(strValue2);
    }

    public void testPutsKeynull() {
        String key1 = "key1";
        String value1 = "value1";
        String key2 = null;
        String value2 = "value2";

        Map<String, String> map = new HashMap<String, String>();
        map.put(key1, value1);
        map.put(key2, value2);
        try {
            dataStore.puts(map);
            fail("null");
        } catch (Exception e) {

        }

    }

    public void testGets() {
        String key1 = "key1";
        String value1 = "value1";
        String key2 = "key2";
        String value2 = "value2";
        dataStore.put(key1, value1);
        dataStore.put(key2, value2);

        List<String> keys = new ArrayList<String>();
        keys.add(key1);
        keys.add(key2);

        List<Object> results = dataStore.gets(keys);
        for (int i = 0; i < 2; i++) {
            String s = (String) results.get(i);
            assertEquals(s, "value" + ++i);
        }

        keys = new ArrayList<String>();
        keys.add(key1);

        // test gets single key
        results = dataStore.gets(keys);
        for (int i = 0; i < 2; i++) {
            String s = (String) results.get(i);
            assertEquals(s, "value" + ++i);
        }
    }
    
    public void testSingleGets(){
        String key1 = "key1";
        String value1 = "value1";

        dataStore.put(key1, value1);

        List<String> keys = new ArrayList<String>();
        keys.add(key1);

        List<Object> results = dataStore.gets(keys);
        for (int i = 0; i < 1; i++) {
            String s = (String) results.get(i);
            assertEquals(s, "value" + ++i);
        }
    }

    public void testGetMap() {
        String key1 = "key1";
        String value1 = "value1";
        String key2 = "key2";
        String value2 = "value2";
        dataStore.put(key1, value1);
        dataStore.put(key2, value2);

        List<String> keys = new ArrayList<String>();
        keys.add(key1);
        keys.add(key2);

        Map<Object, Object> map = dataStore.getMap(keys);
        int i = 0;
        for (Entry<Object, Object> e : map.entrySet()) {
            if (e.getKey().equals("key" + ++i)) {
                assertEquals(e.getValue(), "value" + i);
            }
        }
        assertEquals(2, i);
    }

    /**
     * Test Name: value值为空. Expected Result: 取出数据正确
     */
    public void testPut3() {
        String key = "key001";
        String value = null;
        try {
            dataStore.put(key, value);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            fail("Null value!");
        }
    }

    /*
     * public void testPutObject() { String key = "key999"; Product product = new Product(); product.setId("P999");
     * product.setName("Apple4"); product.setPrice(199.99); try { dataStore.put(key, product); Product product2 =
     * (Product) dataStore.get(key); Assert.assertEquals(product.getId(), product2.getId());
     * Assert.assertEquals(product.getName(), product2.getName()); Assert.assertEquals(product.getPrice(),
     * product2.getPrice()); } catch (IllegalArgumentException e) { fail(e.toString()); } }
     */

    // public void testGet() {
    // String key = "key002";
    // String value = "value002";
    // dataStore.put( key , value);
    //
    // String retValue = (String) dataStore.get( key );
    //
    // assertEquals( "Get putted value", value , retValue);
    // }
    public void testDelete() {
        String key = "key003";
        String value = "value003";
        dataStore.put(key, value);
        dataStore.delete(key);
        String retValue = (String) dataStore.get(key);
        assertNull("Get deleted value", retValue);
    }

    /**
     * Test Name: put新的value给已存在的key，get出新的value, Expected Result: 取出数据正确
     */
    public void testKeyExisted() {
        String key = "key003";
        String value = "value003";
        dataStore.put(key, value);
        String value2 = "value004";
        dataStore.put(key, value2);
        String retValue2 = (String) dataStore.get(key);
        assertEquals(value2, retValue2);

    }

    /**
     * Test Name: key为int类型, Expected Result: 取出数据正确
     */
    public void testPutKeyInt() {
        String key = "3";
        String value = "value003";
        dataStore.put(key, value);
        Integer key2 = 3;
        String value2 = "value004";
        dataStore.put(key2, value2);
        String strValue1 = (String) dataStore.get(key);
        String strValue2 = (String) dataStore.get(key2);
        assertEquals(value2, strValue1);
        assertEquals(value2, strValue2);

    }

    /**
     * Test Name: key长度为0, Expected Result: client抛异常
     */
    public void testPutKeyLngth0() {
        String key = "";
        String value = "value003";
        try {
            dataStore.put(key, value);
            fail("Emtpy Key!");
        } catch (IllegalArgumentException e) {

        }
    }

    /**
     * Test Name: key长度为255, Expected Result: 插入正常
     */
    public void testPutKeyLngth255() {
        String key = new String(new byte[255]);
        String value = "value003";
        try {
            dataStore.put(key, value);
        } catch (IllegalArgumentException e) {

        }
    }

    /**
     * Test Name: key长度为256, Expected Result: client抛异常
     */
    public void testPutKeyLngth256() {
        String key = new String(new byte[256]);
        System.out.println(key.length());
        String value = "value003";
        try {
            dataStore.put(key, value);
            fail("Out of Length Key!");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Test Name: key长度为256, Expected Result: client抛异常
     */
    public void testPutKeyLngth500() {
        String key = StringUtils.repeat("A", 300);
        System.out.println(key.length());
        String value = "value003";
        try {
            dataStore.put(key, value);
            fail("Out of Length Key!");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Test Name: key长度为256, Expected Result: client抛异常
     */
    public void testPutValueMoreThan1M() {
        String key = "ABC";
        String value = StringUtils.repeat("A", 1024 * 1024 + 1);
        System.out.println(key.length());
        try {
            DataStore dataStore0 = dataStoreFactory.getDataStore("NotcompressUser");
            dataStore0.put(key, value);
            fail("Out of Length Value, 1M!");
        } catch (IllegalArgumentException e) {

        }
    }

    /**
     * Test Name: value为特殊字符 , Expected Result: 插入获取正常
     */
    public void testPutValueSpecial() {
        String key = new String(new byte[256]);
        System.out.println(key.length());
        String value = "!@#$%^&amp;*:-/a\u0001b,\n\t";
        try {
            dataStore.put(key, value);
            String retValue = (String) dataStore.get(key);
            assertEquals(value, retValue);
        } catch (IllegalArgumentException e) {
        }

    }

}

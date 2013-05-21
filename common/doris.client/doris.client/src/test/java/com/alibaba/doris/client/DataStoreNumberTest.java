/*
Copyright(C) 2010 Alibaba Group Holding Limited
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.alibaba.doris.client;

import java.math.BigDecimal;
import java.math.BigInteger;

import junit.framework.Assert;

import org.junit.Test;

import com.alibaba.doris.client.mock.MockDataStoreFactoryImpl;

/**
 * @author Raymond He ( He Kun), raymond.he.kk@gmail.com
 * @since 1.0 2011-7-5
 */
public class DataStoreNumberTest {

    protected String           configUrl;
    protected DataStoreFactory dataStoreFactory;
    protected DataStore        dataStore;

    public DataStoreNumberTest() {
        configUrl = "mock-doris-client.properties";
        dataStoreFactory = new MockDataStoreFactoryImpl(configUrl);
        dataStore = dataStoreFactory.getDataStore("HitCount");
    }

    @Test
    public void testNullValue() {
        dataStore.put(101, null);
        Object result = dataStore.get(101);
        Assert.assertEquals("null value", null, result);
    }

    /**
     * 测试插入int数据类型
     */
    @Test
    public void testIntValue() {
        dataStore.put(199, 199);

        Assert.assertTrue("Integer value", dataStore.get(199) instanceof Integer);
        int value = ((Integer) dataStore.get(199)).intValue();

        Assert.assertEquals("int value", 199, value);
    }

    /**
     * 测试插入Integer数据类型
     */
    @Test
    public void testIntegerValue() {
        Integer integer = new Integer(199);
        dataStore.put(199, integer);

        Assert.assertTrue("Integer value", dataStore.get(199) instanceof Integer);

        Integer value = ((Integer) dataStore.get(199));

        Assert.assertEquals("integer value", integer, value);
    }

    /**
     * 测试插入long数据类型
     */
    @Test
    public void testlongValue() {
        DataStore dataStore = dataStoreFactory.getDataStore("HitCountLong");
        String key = "keyLong";
        long v = 100000l;
        dataStore.put("keyLong", v);

        Assert.assertTrue("Integer value", dataStore.get(key) instanceof Long);
        long value = ((Long) dataStore.get(key)).longValue();

        Assert.assertEquals("long value", v, value);
    }

    /**
     * 测试插入Long数据类型
     */
    @Test
    public void testLongValue() {
        DataStore dataStore = dataStoreFactory.getDataStore("HitCountLong");
        String key = "keyInteger";
        Long v = new Long(100000l);
        dataStore.put("keyInteger", v);

        Assert.assertTrue("Integer value", dataStore.get(key) instanceof Long);
        Long value = ((Long) dataStore.get(key));

        Assert.assertEquals("long value", v, value);
    }

    /**
     * 测试插入double数据类型
     */
    @Test
    public void testdoubleValue() {
        DataStore dataStore = dataStoreFactory.getDataStore("double");
        String key = "keyLong";
        double v = 100002342340d;
        dataStore.put("keyLong", v);

        Assert.assertTrue("double value", dataStore.get(key) instanceof Double);
        double value = ((Double) dataStore.get(key)).doubleValue();

        Assert.assertEquals("double value", v, value);
    }

    /**
     * 测试插入Double数据类型
     */
    @Test
    public void testDoubleValue() {
        DataStore dataStore = dataStoreFactory.getDataStore("Double");
        String key = "keyDouble";
        Double v = 10000.1;
        dataStore.put(key, v);

        Assert.assertTrue("Double value", dataStore.get(key) instanceof Double);
        Double value = ((Double) dataStore.get(key)).doubleValue();

        Assert.assertEquals("Double value", v, value);
    }

    /**
     * 测试插入short数据类型
     */
    @Test
    public void testshortValue() {
        DataStore dataStore = dataStoreFactory.getDataStore("short");
        String key = "keyshort";
        short v = 10000;
        dataStore.put(key, v);

        Assert.assertTrue("short value", dataStore.get(key) instanceof Short);
        short value = ((Short) dataStore.get(key)).shortValue();

        Assert.assertEquals("short value", v, value);
    }

    /**
     * 测试插入Short数据类型
     */
    @Test
    public void testShortValue() {
        DataStore dataStore = dataStoreFactory.getDataStore("Short");
        String key = "keyShort";
        Short v = 10000;
        dataStore.put(key, v);

        Assert.assertTrue("Short value", dataStore.get(key) instanceof Short);
        Short value = ((Short) dataStore.get(key)).shortValue();

        Assert.assertEquals("Short value", v, value);
    }

    /**
     * 测试插入float数据类型
     */
    @Test
    public void testfloatValue() {
        DataStore dataStore = dataStoreFactory.getDataStore("float");
        String key = "keyfloat";
        float v = 10000.1f;
        dataStore.put(key, v);

        Assert.assertTrue("float value", dataStore.get(key) instanceof Float);
        float value = ((Float) dataStore.get(key)).floatValue();

        Assert.assertEquals("float value", v, value);
    }

    /**
     * 测试插入Float数据类型
     */
    @Test
    public void testFloatValue() {
        DataStore dataStore = dataStoreFactory.getDataStore("Float");
        String key = "keyFloat";
        Float v = new Float(10000.1f);
        dataStore.put(key, v);

        Assert.assertTrue("Float value", dataStore.get(key) instanceof Float);
        Float value = ((Float) dataStore.get(key));

        Assert.assertEquals("Float value", v, value);
    }

    /**
     * 测试插入byte数据类型
     */
    @Test
    public void testbyteValue() {
        DataStore dataStore = dataStoreFactory.getDataStore("byte");
        String key = "keybyte";
        byte v = 123;
        dataStore.put(key, v);
        Object objectValue = dataStore.get(key);
        Assert.assertTrue("byte value", objectValue instanceof Byte);
        byte value = ((Byte) objectValue).byteValue();

        Assert.assertEquals("byte value", v, value);
    }

    /**
     * 测试插入Byte数据类型
     */
    @Test
    public void testByteValue() {
        DataStore dataStore = dataStoreFactory.getDataStore("Byte");
        String key = "keyByte";
        Byte v = 123;
        dataStore.put(key, v);
        Object objectValue = dataStore.get(key);
        Assert.assertTrue("Byte value", objectValue instanceof Byte);
        Byte value = ((Byte) objectValue);

        Assert.assertEquals("Byte value", v, value);
    }

    /**
     * 测试插入BigInteger数据类型
     */
    @Test
    public void testBigIntegerValue() {
        DataStore dataStore = dataStoreFactory.getDataStore("BigInteger");
        String key = "keyBigInteger";
        BigInteger v = new BigInteger("12300000000000");
        dataStore.put(key, v);
        Object objectValue = dataStore.get(key);
        Assert.assertTrue("BigInteger value", objectValue instanceof BigInteger);
        BigInteger value = ((BigInteger) objectValue);

        Assert.assertEquals("BigInteger value", v, value);
    }

    /**
     * 测试插入BigDecimal数据类型
     */
    @Test
    public void testBigDecimalValue() {
        DataStore dataStore = dataStoreFactory.getDataStore("BigDecimal");
        String key = "keyBigDecimal";
        BigDecimal v = new BigDecimal("123.2349");
        dataStore.put(key, v);
        Object objectValue = dataStore.get(key);
        Assert.assertTrue("BigDecimal value", objectValue instanceof BigDecimal);
        BigDecimal value = ((BigDecimal) objectValue);

        Assert.assertEquals("BigDecimal value", v, value);
    }

    @Test
    public void testWrongType1() {
        DataStore dataStore = dataStoreFactory.getDataStore("Long");
        String key = "keyLong";
        Double v = new Double(1000.1);
        try {
            dataStore.put(key, v);
            Assert.fail("wrong type put");
        } catch (Exception e) {
        }
        /*
         * Object objectValue = dataStore.get(key);
         * Assert.assertFalse("BigDecimal value", objectValue instanceof
         * Integer); Assert.assertNotSame("Wrong value", v, objectValue);
         */
    }

    /**
     * 测试插入数据类型
     */
    @Test
    public void testWrongType2() {
        DataStore dataStore = dataStoreFactory.getDataStore("Long");
        String key = "keyLong";
        byte v = (byte) 126;
        try {
            dataStore.put(key, v);
            Assert.fail("wrong type put");
        } catch (Exception e) {
        }
    }

}

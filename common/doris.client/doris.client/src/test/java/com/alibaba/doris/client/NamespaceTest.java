package com.alibaba.doris.client;

import com.alibaba.doris.client.mock.MockDataStoreFactoryImpl;
import com.alibaba.doris.client.testdata.DorisClientProduct;

import junit.framework.TestCase;

public class NamespaceTest extends TestCase {

    protected String           configUrl;
    protected DataStoreFactory dataStoreFactory;

    public NamespaceTest() {
        configUrl = "mock-doris-client.properties";
        dataStoreFactory = new MockDataStoreFactoryImpl(configUrl);
    }

    /**
     * Test Name: 不同namespace下面的key可以重复. Expected Result: 在不同的namespace里面取出数据正确
     */
    public void testDiffNamespace() {
        DataStore dataStoreUser = dataStoreFactory.getDataStore("User");
        DataStore dataStorePro = dataStoreFactory.getDataStore("Product");

        String key = "key001";
        String value1 = "value001";

        DorisClientProduct pro2 = new DorisClientProduct();
        pro2.setName("b");
        pro2.setPrice(10);
        pro2.setQuantity(10);
        pro2.setId("b");

        dataStoreUser.put(key, value1);
        dataStorePro.put(key, pro2);

        Object retValue = dataStoreUser.get(key);

        assertNotNull("get value result ", retValue);
        assertTrue("Result is String ", retValue instanceof String);

        Object retValue2 = dataStorePro.get(key);

        assertNotNull("get value result ", retValue2);
        assertTrue("Result is Product ", retValue2 instanceof DorisClientProduct);

    }

    /**
     * Test Name: client输入不存在的namespace. Expected Result: client抛异常
     */
    public void testNotExistNamespace() {
        try {
            DataStore dataStoreUser = dataStoreFactory.getDataStore("User1");

            String key = "key001";
            String value1 = "value001";

            dataStoreUser.put(key, value1);
            fail("Not Exist Namespace");
        } catch (NullPointerException e) {
        } catch(DorisClientException e) {
        	
        }
    }

}

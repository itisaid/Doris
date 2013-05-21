package com.alibaba.doris.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import com.alibaba.doris.client.mock.MockDataStoreFactoryImpl;
import com.alibaba.doris.client.testdata.DorisClientProduct;

public class ObjectStoreTest extends TestCase {

   protected String           configUrl;
   protected DataStoreFactory dataStoreFactory;
   protected DataStore        dataStore;

    public ObjectStoreTest() {
        configUrl = "mock-doris-client.properties";
        dataStoreFactory = new MockDataStoreFactoryImpl(configUrl);
        dataStore = dataStoreFactory.getDataStore("Product");
    }

    /**
     * Test Name: put简单对象. Expected Result: 取出数据正确
     */
    public void testPutObject() {
        String key = "key001";
        DorisClientProduct pro = new DorisClientProduct();
        pro.setName("a");
        pro.setPrice(10);
        pro.setQuantity(10);
        pro.setId("a");
        dataStore.put(key, pro);
        DorisClientProduct product2 = (DorisClientProduct) dataStore.get(key);
        assertEquals(pro.getId(), product2.getId());
        assertEquals(pro.getName(), product2.getName());
        assertEquals(pro.getPrice(), product2.getPrice());

    }

    /**
     * Test Name: put List对象. Expected Result: 取出数据正确
     */
    @SuppressWarnings("unchecked")
    public void testPutList() {
        String key = "key001";
        DorisClientProduct pro = new DorisClientProduct();
        pro.setName("a");
        pro.setPrice(10);
        pro.setQuantity(10);
        pro.setId("a");

        DorisClientProduct pro2 = new DorisClientProduct();
        pro2.setName("b");
        pro2.setPrice(10);
        pro2.setQuantity(10);
        pro2.setId("b");

        List<DorisClientProduct> proList = new ArrayList<DorisClientProduct>();
        proList.add(pro);
        proList.add(pro2);
        dataStore.put(key, proList);
        try {
            List<DorisClientProduct> productList = (List<DorisClientProduct>) dataStore.get(key);

            assertNotNull("get value result ", productList.get(0));
            assertTrue("Result is Product ", productList.get(0) instanceof DorisClientProduct);

            DorisClientProduct p1 = (DorisClientProduct) productList.get(0);
            assertEquals(pro.getId(), p1.getId());
            assertEquals(pro.getName(), p1.getName());
            assertEquals(pro.getPrice(), p1.getPrice());

            assertNotNull("get value result ", productList.get(1));
            assertTrue("Result is Product ", productList.get(1) instanceof DorisClientProduct);

            DorisClientProduct p2 = (DorisClientProduct) productList.get(1);
            assertEquals(pro2.getId(), p2.getId());
            assertEquals(pro2.getName(), p2.getName());
            assertEquals(pro2.getPrice(), p2.getPrice());

        } catch (Exception e) {
            e.printStackTrace();
            fail("List value fail:" + e);
        }
    }
}

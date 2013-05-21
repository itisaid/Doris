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
import java.util.Map;

import junit.framework.TestCase;

import com.alibaba.doris.client.mock.MockDataStoreFactoryImpl;
import com.alibaba.doris.client.testdata.DorisClientProduct;

/**
 * DataStoreTest
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-5-6
 */
public class DataStoreSerializeTest extends TestCase {

    protected String           configUrl;
    protected DataStoreFactory dataStoreFactory;
    protected DataStore        dataStore;

    public DataStoreSerializeTest() {
        configUrl = "mock-doris-client.properties";
        dataStoreFactory = new MockDataStoreFactoryImpl(configUrl);
        dataStore = dataStoreFactory.getDataStore("Product");
    }

    public void testPut() {
        String key = "key001";

        DorisClientProduct product = new DorisClientProduct();
        product.setId(key);
        product.setName("IPhone4");
        product.setPrice(199.99);
        product.setQuantity(1000);

        dataStore.put(key, product);

        Object result = dataStore.get(key);

        assertNotNull("get value result ", result);
        assertTrue("Result is Product ", result instanceof DorisClientProduct);

        DorisClientProduct retProduct = (DorisClientProduct) dataStore.get(key);

        assertEquals(retProduct.getId(), product.getId());
        assertEquals(retProduct.getName(), product.getName());
        assertEquals(retProduct.getPrice(), product.getPrice());
        assertEquals(retProduct.getQuantity(), product.getQuantity());
    }

    public void testPutSpecial() {
        String key = "key001";

        DorisClientProduct product = new DorisClientProduct();
        product.setId(key);
        product.setName("IPhone4},a\"bc");
        product.setPrice(199.99);
        product.setQuantity(1000);

        dataStore.put(key, product);

        Object result = dataStore.get(key);

        assertNotNull("get value result ", result);
        assertTrue("Result is Product ", result instanceof DorisClientProduct);

        DorisClientProduct retProduct = (DorisClientProduct) dataStore.get(key);

        assertEquals(retProduct.getId(), product.getId());
        assertEquals(retProduct.getName(), product.getName());
        assertEquals(retProduct.getPrice(), product.getPrice());
        assertEquals(retProduct.getQuantity(), product.getQuantity());
    }

    public void testPuts() {
        String key1 = "key001";

        DorisClientProduct product1 = new DorisClientProduct();
        product1.setId(key1);
        product1.setName("IPhone4");
        product1.setPrice(199.99);
        product1.setQuantity(1000);

        String key2 = "key002";

        DorisClientProduct product2 = new DorisClientProduct();
        product2.setId(key2);
        product2.setName("IPhone4},a\"bc");
        product2.setPrice(199.99);
        product2.setQuantity(1000);

        Map<String, DorisClientProduct> map = new HashMap<String, DorisClientProduct>();
        map.put(key1, product1);
        map.put(key2, product2);

        dataStore.puts(map);

        Object result1 = dataStore.get(key1);

        assertNotNull("get value result ", result1);
        assertTrue("Result is Product ", result1 instanceof DorisClientProduct);

        DorisClientProduct retProduct = (DorisClientProduct) dataStore.get(key1);

        assertEquals(retProduct.getId(), product1.getId());
        assertEquals(retProduct.getName(), product1.getName());
        assertEquals(retProduct.getPrice(), product1.getPrice());
        assertEquals(retProduct.getQuantity(), product1.getQuantity());

        Object result2 = dataStore.get(key2);

        assertNotNull("get value result ", result2);
        assertTrue("Result is Product ", result2 instanceof DorisClientProduct);

        DorisClientProduct retProduct2 = (DorisClientProduct) dataStore.get(key2);

        assertEquals(retProduct2.getId(), product2.getId());
        assertEquals(retProduct2.getName(), product2.getName());
        assertEquals(retProduct2.getPrice(), product2.getPrice());
        assertEquals(retProduct2.getQuantity(), product2.getQuantity());

    }

}

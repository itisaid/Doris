package com.alibaba.doris.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainTestPutsGets {

    public static void main(String[] args) {
        String configUrl = "t.properties";
        DataStoreFactory dataStoreFactory = new DataStoreFactoryImpl(configUrl);
        int num = 30;
        DataStore dataStore = dataStoreFactory.getDataStore("frProduct");
        dataStore.put("key1", "hello1");// init connection
        System.out.println(dataStore.get("key1"));
        dataStore.put("key1", "hello1");
        System.out.println(dataStore.get("key1"));
        System.out.println(dataStore.get("key1"));
        String key = "kfsse";
        String value = "v2w";
        Map<String, String> map = new HashMap<String, String>();

        long bp2 = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            dataStore.put(key + i, value + i);
        }
        long ep2 = System.currentTimeMillis();

        for (int i = 0; i < num; i++) {
            map.put(key + i, value + i);
        }
        long bp1 = System.currentTimeMillis();
        dataStore.puts(map);
        long ep1 = System.currentTimeMillis();

        // System.out.println("======" + dataStore.get("t1") + dataStore.get("t2"));
        long b1 = System.currentTimeMillis();

        for (int i = 0; i < num; i++) {
            dataStore.get(key + i);
        }
        long e1 = System.currentTimeMillis();

        List<Object> keys = new ArrayList<Object>();
        for (int i = 0; i < num; i++) {
            keys.add(key + i);
        }
        long b2 = System.currentTimeMillis();
        List<Object> r = dataStore.gets(keys);
        long e2 = System.currentTimeMillis();
        System.out.println("get:" + (e1 - b1));
        System.out.println("gets:" + (e2 - b2));
        System.out.println("puts:" + (ep1 - bp1));
        System.out.println("put:" + (ep2 - bp2));
        System.out.println("======+++" + r);
        dataStoreFactory.close();
    }
}

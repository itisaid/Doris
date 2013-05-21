/*
Copyright(C) 1999-2010 Alibaba Group Holding Limited
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
package com.alibaba.doris.client.mock;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

import com.alibaba.doris.client.DataStoreFactoryImpl;
import com.alibaba.doris.client.testdata.DorisClientOrder;
import com.alibaba.doris.client.testdata.DorisClientProduct;
import com.alibaba.doris.common.Namespace;
import com.alibaba.doris.common.adminservice.UserAuthService;
import com.alibaba.doris.common.config.ConfigManager;
import com.alibaba.doris.common.config.NamespaceManagerImpl;
import com.alibaba.doris.common.serialize.JsonSerializer;
import com.alibaba.doris.common.serialize.StringSerializer;

/**
 * MockDataStoreFactoryImpl
 * <p/>
 * A demo DataStoreFactoryImpl to load config by API, but not config file.
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-21
 */

public class MockDataStoreFactoryImpl extends DataStoreFactoryImpl {

    public MockDataStoreFactoryImpl(String configLocation) {
        super(configLocation);
    }

    @Override
    protected void initConfigManager() {
        //init config manager.
        ConfigManager configManager = new DorisClientMockConfigManager();
        configManager.setConfigLocation(configLocation);
        configManager.initConfig();
        this.setConfigManager(configManager);
    }

    /**
     * mock
     */
    @Override
    protected void initUserAuthService() {
        userAuthService = new UserAuthService() {

            public int getUserAuth(String userName, String password) {
                return 0;
            }
        };
    }

    @Override
    protected void initNamespace() {
        namespaceManager = new NamespaceManagerImpl() {

            @Override
            public void initConfig() {
                namespaceMap = new HashMap<String, Namespace>();

                Namespace namespace = new Namespace();
                namespace.setId(101);
                namespace.setSerializeMode(StringSerializer.class.getName());
                namespace.setName("User");
                namespace.setCopyCount(2);
                namespace.setCompressThreshold("1024");
                namespace.setCompressMode("gzip");

                namespaceMap.put(namespace.getName(), namespace);
                
                Namespace namespace0 = new Namespace();
                namespace0.setId(101);
                namespace0.setSerializeMode(StringSerializer.class.getName());
                namespace0.setName("NotcompressUser");
                namespace0.setCopyCount(2);
                //dont compress

                namespaceMap.put(namespace0.getName(), namespace0);


                Namespace namespace1 = new Namespace();
                namespace1.setId(102);
                namespace1.setName("Product");
                namespace1.setSerializeMode(JsonSerializer.class.getName());
                namespace1.setClassName(DorisClientProduct.class.getName());
                namespace1.setCopyCount(2);
                namespace1.setCompressThreshold("1024");
                namespaceMap.put(namespace1.getName(), namespace1);

                /* 过长 value 验证， 压缩阀值 1M＋10，保证测试用例中的 1M value 不会被压缩. */
                Namespace namespace2 = new Namespace();
                namespace2.setId(103);
                namespace2.setSerializeMode(JsonSerializer.class.getName());
                namespace2.setName("Order");
                namespace2.setCopyCount(2);
                namespace2.setClassName(DorisClientOrder.class.getName());
                namespace2.setCompressThreshold(String.valueOf(1024 * 1024 + 10));
                namespaceMap.put(namespace2.getName(), namespace2);

                /* 过长的Namespace name, 超过 64，测试验证功能 */
                Namespace namespace4 = new Namespace();
                namespace4.setId(104);
                namespace4.setSerializeMode(JsonSerializer.class.getName());
                namespace4
                        .setName("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                namespace4.setCopyCount(2);
                namespace4.setClassName(DorisClientOrder.class.getName());
                namespace4.setCompressThreshold("1024");

                namespaceMap.put(namespace4.getName(), namespace4);

                Namespace namespace5 = new Namespace();
                namespace5.setId(105);
                namespace5.setSerializeMode(StringSerializer.class.getName());
                namespace5.setClassName(Integer.class.getName());
                namespace5.setName("HitCount");
                namespace5.setCopyCount(2);
                namespace5.setCompressThreshold("1024");

                namespaceMap.put(namespace5.getName(), namespace5);

                Namespace namespace6 = new Namespace();
                namespace6.setId(106);
                namespace6.setSerializeMode(StringSerializer.class.getName());
                namespace6.setClassName(Long.class.getName());
                namespace6.setName("HitCountLong");
                namespace6.setCopyCount(2);
                namespace6.setCompressThreshold("1024");

                namespaceMap.put(namespace6.getName(), namespace6);
                //用于存储int类型
                Namespace namespace7 = new Namespace();
                namespace7.setId(107);
                namespace7.setSerializeMode(StringSerializer.class.getName());
                namespace7.setClassName(int.class.getName());
                namespace7.setName("int");
                namespace7.setCopyCount(2);
                namespace7.setCompressThreshold("1024");

                namespaceMap.put(namespace7.getName(), namespace7);
              //用于存储Integer类型                
                Namespace namespace8 = new Namespace();
                namespace8.setId(108);
                namespace8.setSerializeMode(StringSerializer.class.getName());
                namespace8.setClassName(Integer.class.getName());
                namespace8.setName("Integer");
                namespace8.setCopyCount(2);
                namespace8.setCompressThreshold("1024");
                namespaceMap.put(namespace8.getName(), namespace8);
              //用于存储long类型 
                Namespace namespace9 = new Namespace();
                namespace9.setId(109);
                namespace9.setSerializeMode(StringSerializer.class.getName());
                namespace9.setClassName(long.class.getName());
                namespace9.setName("long");
                namespace9.setCopyCount(2);
                namespace9.setCompressThreshold("1024");
                namespaceMap.put(namespace9.getName(), namespace9);
                //用于存储Long类型 
                Namespace namespace10 = new Namespace();
                namespace10.setId(110);
                namespace10.setSerializeMode(StringSerializer.class.getName());
                namespace10.setClassName(Long.class.getName());
                namespace10.setName("Long");
                namespace10.setCopyCount(2);
                namespace10.setCompressThreshold("1024");
                namespaceMap.put(namespace10.getName(), namespace10);
                //用于存储double类型 
                Namespace namespace11 = new Namespace();
                namespace11.setId(111);
                namespace11.setSerializeMode(StringSerializer.class.getName());
                namespace11.setClassName(double.class.getName());
                namespace11.setName("double");
                namespace11.setCopyCount(2);
                namespace11.setCompressThreshold("1024");
                namespaceMap.put(namespace11.getName(), namespace11);
                //用于存储Double类型 
                Namespace namespace12 = new Namespace();
                namespace12.setId(112);
                namespace12.setSerializeMode(StringSerializer.class.getName());
                namespace12.setClassName(Double.class.getName());
                namespace12.setName("Double");
                namespace12.setCopyCount(2);
                namespace12.setCompressThreshold("1024");
                namespaceMap.put(namespace12.getName(), namespace12);
                //用于存储short类型 
                Namespace namespace13 = new Namespace();
                namespace13.setId(113);
                namespace13.setSerializeMode(StringSerializer.class.getName());
                namespace13.setClassName(short.class.getName());
                namespace13.setName("short");
                namespace13.setCopyCount(2);
                namespace13.setCompressThreshold("1024");
                namespaceMap.put(namespace13.getName(), namespace13);
                //用于存储Short类型 
                Namespace namespace14 = new Namespace();
                namespace14.setId(114);
                namespace14.setSerializeMode(StringSerializer.class.getName());
                namespace14.setClassName(Short.class.getName());
                namespace14.setName("Short");
                namespace14.setCopyCount(2);
                namespace14.setCompressThreshold("1024");
                namespaceMap.put(namespace14.getName(), namespace14);
                //用于存储float类型 
                Namespace namespace15 = new Namespace();
                namespace15.setId(115);
                namespace15.setSerializeMode(StringSerializer.class.getName());
                namespace15.setClassName(float.class.getName());
                namespace15.setName("float");
                namespace15.setCopyCount(2);
                namespace15.setCompressThreshold("1024");
                namespaceMap.put(namespace15.getName(), namespace15);
                //用于存储Float类型 
                Namespace namespace16 = new Namespace();
                namespace16.setId(116);
                namespace16.setSerializeMode(StringSerializer.class.getName());
                namespace16.setClassName(Float.class.getName());
                namespace16.setName("Float");
                namespace16.setCopyCount(2);
                namespace16.setCompressThreshold("1024");
                namespaceMap.put(namespace16.getName(), namespace16);
                //用于存储byte类型 
                Namespace namespace17 = new Namespace();
                namespace17.setId(117);
                namespace17.setSerializeMode(StringSerializer.class.getName());
                namespace17.setClassName(byte.class.getName());
                namespace17.setName("byte");
                namespace17.setCopyCount(2);
                namespace17.setCompressThreshold("1024");
                namespaceMap.put(namespace17.getName(), namespace17);
                //用于存储Byte类型 
                Namespace namespace18 = new Namespace();
                namespace18.setId(118);
                namespace18.setSerializeMode(StringSerializer.class.getName());
                namespace18.setClassName(Byte.class.getName());
                namespace18.setName("Byte");
                namespace18.setCopyCount(2);
                namespace18.setCompressThreshold("1024");
                namespaceMap.put(namespace18.getName(), namespace18);
                //用于存储BigInteger类型 
                Namespace namespace19 = new Namespace();
                namespace19.setId(119);
                namespace19.setSerializeMode(StringSerializer.class.getName());
                namespace19.setClassName(BigInteger.class.getName());
                namespace19.setName("BigInteger");
                namespace19.setCopyCount(2);
                namespace19.setCompressThreshold("1024");
                namespaceMap.put(namespace19.getName(), namespace19);
                //用于存储BigDecimal类型 
                Namespace namespace20 = new Namespace();
                namespace20.setId(120);
                namespace20.setSerializeMode(StringSerializer.class.getName());
                namespace20.setClassName(BigDecimal.class.getName());
                namespace20.setName("BigDecimal");
                namespace20.setCopyCount(2);
                namespace20.setCompressThreshold("1024");
                namespaceMap.put(namespace20.getName(), namespace20);

            }
        };

        namespaceManager.initConfig();
    }

}

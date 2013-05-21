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
package com.alibaba.doris.demo;

import java.util.List;
import java.util.Map;

import com.alibaba.doris.client.DataSourceException;
import com.alibaba.doris.client.DataStore;
import com.alibaba.doris.client.DataStoreFactory;
import com.alibaba.doris.client.mock.MockDataStoreFactoryImpl;
import com.alibaba.doris.client.mock.DorisClientMockKvConnection;
import com.alibaba.doris.client.net.DataSource;

/**
 * DorisDemo
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-21
 */
public class DorisDemo {
	
	public static void main(String[] args) throws DataSourceException {
		
		String configUrl = "mock-doris-client.properties";		
		DataStoreFactory dataStoreFactory = new MockDataStoreFactoryImpl(configUrl);
		
		DataStore dataStore = dataStoreFactory.getDataStore( "User");
		
		String key1 = "0001";
		String value1 = "User001";
		
		//1. put
		dataStore.put(key1 ,value1 );		
			
		System.out.println(String.format("Put key=value: %s, %s." , key1, value1));
		
		//2. get
		String retValue  = (String) dataStore.get(key1);				
		System.out.println("Get Value: " + retValue );
		
		//3. delete, then get
//		int delCount = dataStore.delete(key1);
		retValue  = (String) dataStore.get(key1);
		
		dataStore.put(key1 ,value1 );	
		
		Map<String,List<DataSource>> allDataSources = dataStoreFactory.getDataSourceManager().getDataSourceRouter().getAllDataSources();
		
		System.out.println();
		System.out.println("View storage data: ***************************"); 
		
		if( allDataSources.size() > 0) {
			List<DataSource> seqDataSources = allDataSources.get( "1" );
			for(DataSource dataSource: seqDataSources ) {
				DorisClientMockKvConnection mockKvConnection = (DorisClientMockKvConnection) dataSource.getConnection();
				System.out.println("Datasource: " + dataSource + ", Storage:" + mockKvConnection.getStorage());
			}
		}
		
		if( allDataSources.size() > 1) {
			List<DataSource> seqDataSources = allDataSources.get( "2" );
			for(DataSource dataSource: seqDataSources ) {
				DorisClientMockKvConnection mockKvConnection = (DorisClientMockKvConnection) dataSource.getConnection();
				System.out.println("Datasource: " + dataSource + ",Storage:" + mockKvConnection.getStorage());
			}
		}
		
	}
}
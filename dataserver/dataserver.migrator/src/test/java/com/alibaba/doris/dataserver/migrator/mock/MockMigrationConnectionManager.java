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
package com.alibaba.doris.dataserver.migrator.mock;

import java.net.InetSocketAddress;

import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.local.LocalConnection;
import com.alibaba.doris.dataserver.migrator.connection.MigrationConnectionManager;

/**
 * @author Raymond He ( He Kun), raymond.he.kk@gmail.com
 * @since 1.0
 * 2011-6-28
 */
public class MockMigrationConnectionManager extends MigrationConnectionManager {
	
	@Override
	protected Connection createConnection(InetSocketAddress address) {
		return new LocalConnection(); 
	}
}

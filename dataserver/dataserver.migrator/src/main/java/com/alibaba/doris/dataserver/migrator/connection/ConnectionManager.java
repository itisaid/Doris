/*
Copyright(C) 2010-2011 Alibaba Group Holding Limited
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
package com.alibaba.doris.dataserver.migrator.connection;

import java.util.List;

import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.common.route.MigrationRoutePair;

/**
 * ConnectionManager
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-6-1
 */
public interface ConnectionManager {
	
	/**
	 * @param routePairs
	 */
	public void connect(List<MigrationRoutePair> routePairs);

	/**
	 * 释放所有连接
	 */
	public void close();

	/**
	 * 根据address快速获取缓存中的一个 connection 
	 * @see com.alibaba.doris.dataserver.migrator.connection.ConnectionManager#getConnection(java.net.InetSocketAddress)
	 */
	public Connection getConnection(String address);
	
	/**
	 * 判断所有链接是否开启和联通
	 * @return
	 */
	public boolean isOpen();
}

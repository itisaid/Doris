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
package com.alibaba.doris.client.operation;

import com.alibaba.doris.client.net.DataSource;
import com.alibaba.doris.client.net.OperationFuture;
import com.alibaba.doris.client.operation.failover.PeerCallback;

/**
 * @author Raymond He ( He Kun), raymond.he.kk@gmail.com
 * @since 1.0
 * 2011-7-11
 */
public class DataSourceOpFuture {
	
	private DataSource dataSource;
	private OperationFuture<?>future;
	private PeerCallback peerCallback;
	
	public DataSourceOpFuture( DataSource dataSource,  PeerCallback peerCallback) {
		this.dataSource = dataSource;
		this.peerCallback = peerCallback;
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public PeerCallback getPeerCallback() {
		return peerCallback;
	}
}

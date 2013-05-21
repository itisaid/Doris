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

import java.util.Properties;

import com.alibaba.doris.client.net.Connection;
import com.alibaba.doris.client.net.DataSource;

/**
 * MockKvDataSource
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-22
 */
public class MockKvDataSource implements DataSource {

	private DorisClientMockKvConnection mockKvConnection = new DorisClientMockKvConnection();

	private int sequence;
	public int no;
	private String ip;
	private int port;
	private Properties configProperties;

	public MockKvDataSource() {
	}

	public MockKvDataSource(String url) {
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}
	
	
	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public int getNo() {
		return no;
	}

	public Connection getConnection() {
		return mockKvConnection;
	}

	public void setConfigProperties(Properties configProperties) {
		this.configProperties = configProperties;
	}

	public void initConfig() {
	}

	public String toString() {
		return new StringBuilder().append("[DataSource:").append(sequence).append(".").append(no)
				.append(",").append(ip).append(":").append(port).append("]")
				.toString();
	}
	
	public void close() {
	}
}

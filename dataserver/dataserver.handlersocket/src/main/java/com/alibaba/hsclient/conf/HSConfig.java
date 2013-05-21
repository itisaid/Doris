package com.alibaba.hsclient.conf;

public class HSConfig {
	private String host;
	private int rPort = 9998;
	private int wrPort = 9999;
	private boolean tcpNoDelay = true;
	private boolean reuseAddress = true;
	private boolean readOnly = false;
	private String encoding = "utf-8";
	private int soTimeout = 30 * 1000;
	private int sendBufferSize = 8 * 1024;
	private int receiveBufferSize = 8 * 1024;
	private int executeBufferSize = 8 * 1024;
	private boolean blocking = false;
	private boolean hardClose = false;
	private boolean isAuth = false;
	private String aKey;

	public boolean isAuth() {
		return isAuth;
	}

	public void setAuth(boolean isAuth) {
		this.isAuth = isAuth;
	}

	public String getaKey() {
		return aKey;
	}

	public void setaKey(String aKey) {
		this.aKey = aKey;
	}

	public int getrPort() {
		return rPort;
	}

	public void setrPort(int rPort) {
		this.rPort = rPort;
	}

	public int getSoTimeout() {
		return soTimeout;
	}

	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}

	public int getSendBufferSize() {
		return sendBufferSize;
	}

	public void setSendBufferSize(int sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
	}

	public int getReceiveBufferSize() {
		return receiveBufferSize;
	}

	public void setReceiveBufferSize(int receiveBufferSize) {
		this.receiveBufferSize = receiveBufferSize;
	}

	public int getExecuteBufferSize() {
		return executeBufferSize;
	}

	public void setExecuteBufferSize(int executeBufferSize) {
		this.executeBufferSize = executeBufferSize;
	}

	public boolean isBlocking() {
		return blocking;
	}

	public void setBlocking(boolean blocking) {
		this.blocking = blocking;
	}

	public boolean isHardClose() {
		return hardClose;
	}

	public void setHardClose(boolean hardClose) {
		this.hardClose = hardClose;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getRPort() {
		return rPort;
	}

	public void setRPort(int rPort) {
		this.rPort = rPort;
	}

	public int getWrPort() {
		return wrPort;
	}

	public void setWrPort(int wrPort) {
		this.wrPort = wrPort;
	}

	public boolean isTcpNoDelay() {
		return tcpNoDelay;
	}

	public void setTcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
	}

	public boolean isReuseAddress() {
		return reuseAddress;
	}

	public void setReuseAddress(boolean reuseAddress) {
		this.reuseAddress = reuseAddress;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}

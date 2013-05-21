package com.alibaba.hsclient.bean;

public class LimitInfo {
	private int limit = 1;
	private int offset = 0;

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
}

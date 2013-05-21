package com.alibaba.hsclient.util;

public enum FilterOperator {
	F,
	W;
	public String getValue() {
		switch (this) {
		case F:
			return "F";
		case W:
			return "W";
		default:
			throw new RuntimeException("Unknow filter operator " + this);
		}
	}
}

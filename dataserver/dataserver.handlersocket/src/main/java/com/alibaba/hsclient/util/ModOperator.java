package com.alibaba.hsclient.util;

public enum ModOperator {
	U,
	D,
	INC,
	DER;
	public String getValue() {
		switch (this) {
		case U:
			return "U";
		case D:
			return "D";
		case INC:
			return "+";
		case DER:
			return "-";
		default:
			throw new RuntimeException("Unknow modify operator " + this);
		}
	}
}

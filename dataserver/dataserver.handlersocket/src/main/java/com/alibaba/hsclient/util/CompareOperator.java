package com.alibaba.hsclient.util;

public enum CompareOperator {
	EQ,
	GT,
	GE,
	LE,
	LT;

	public String getValue() {
		switch (this) {
		case EQ:
			return "=";
		case GT:
			return ">";
		case GE:
			return ">=";
		case LE:
			return "<=";
		case LT:
			return "<";
		default:
			throw new RuntimeException("Unknow Compare operator " + this);
		}
	}
}

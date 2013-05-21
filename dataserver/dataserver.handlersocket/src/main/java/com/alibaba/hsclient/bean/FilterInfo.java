package com.alibaba.hsclient.bean;

public class FilterInfo {

	private String fType;
	private String fOp;
	private String fCol;
	
	public String getfCol() {
		return fCol;
	}

	public void setfCol(String fCol) {
		this.fCol = fCol;
	}

	private String fVal;

	public String getfType() {
		return fType;
	}

	public void setfType(String fType) {
		this.fType = fType;
	}

	public String getfOp() {
		return fOp;
	}

	public void setfOp(String fOp) {
		this.fOp = fOp;
	}

	public String getfVal() {
		return fVal;
	}

	public void setfVal(String fVal) {
		this.fVal = fVal;
	}

}

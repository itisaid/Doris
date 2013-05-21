package com.alibaba.hsclient.bean;

import com.alibaba.hsclient.util.Const;

public class InInfo {
	
	private String inOperator = Const.IN_OPERATOR;
	private String iCol;
	private String[] inValues;

	public InInfo(String iCol, String[] inValues)
	{
		this.iCol = iCol;
		this.inValues = inValues;
	}
	
	public String getInOperator()
	{
		return inOperator;
	}
	public String getiCol() {
		return iCol;
	}

	public void setiCol(String iCol) {
		this.iCol = iCol;
	}

	public String[] getInValues() {
		return inValues;
	}

	public void setInValues(String[] inValues) {
		this.inValues = inValues;
	}

}

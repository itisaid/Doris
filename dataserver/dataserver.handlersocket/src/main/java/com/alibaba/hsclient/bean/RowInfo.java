package com.alibaba.hsclient.bean;

public class RowInfo {
	private String[] columns;
	
	public RowInfo(String[] columns)
	{
		this.columns = columns;
	}
	
	public String[] getColumns() {
		return columns;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

	public String getColumn(int index)
	{
		if (index >= columns.length)
		{
			throw new IllegalArgumentException("...");
		}
		else
		{
			return columns[index];
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (String str : columns)
		{
			builder.append(str.toString()+"\t");
		}
		return builder.toString();
	}
}

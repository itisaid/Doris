package com.alibaba.hsclient.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import com.alibaba.hsclient.util.Const;

public class ResultInfo {
	private int errorCode;
	private int numColumns;
	private List<String> messages;
	
	public ResultInfo() {
		messages = new ArrayList<String>();
	}
	public int getRowCount()
	{
		if (this.numColumns == 0)
		{
			return 0;
		}
		if (this.messages == null || this.messages.size() == 0)
		{
			return 0;
		}
		return this.messages.size() / this.numColumns;
	}
	public List<String> getMessages() {
		return messages;
	}

	public void addMessages(String message) {
		this.messages.add(message);
	}

	public ResultInfo(int errorCode, int numColumns, List<String> messages) {
		this();
		this.errorCode = errorCode;
		this.numColumns = numColumns;
		this.messages = messages;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getNumColumns() {
		return numColumns;
	}

	public void setNumColumns(int numColumns) {
		this.numColumns = numColumns;
	}

	public Iterator<RowInfo> iterator()
	{
		if (this.messages == null || this.messages.size() == 0 || this.numColumns == 0)
		{
			return new Iterator<RowInfo>(){
				public boolean hasNext() {
					return false;
				}
				public RowInfo next() {
					return null;
				}
				public void remove() {
				}
				
			};
		}
		
		List<RowInfo> list = new ArrayList<RowInfo>();
		int cursor = 0;
		int rowCount = this.messages.size() / this.numColumns;
		String[] messageArray = this.messages.toArray(new String[this.messages.size()]);
		while(cursor < rowCount)
		{
			list.add(new RowInfo(Arrays.copyOfRange(messageArray, cursor*numColumns, cursor*numColumns+numColumns)));
			cursor++;
		}
		return list.iterator();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(errorCode).append(Const.TOKEN_SEPARATOR);
		builder.append(numColumns).append(Const.TOKEN_SEPARATOR);
		for (String message : messages) {
			builder.append(message).append(Const.TOKEN_SEPARATOR);
		}
		return builder.toString();
	}
}

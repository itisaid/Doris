package com.alibaba.hsclient.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import com.alibaba.hsclient.bean.ResultInfo;

public class HSUtils {

	private static final String DEFAULT_ENCODING = "UTF-8";
	private String encoding = DEFAULT_ENCODING;

	public HSUtils() {
	}
	
	public static final boolean isBlank(String s) {
		if (s == null || s.trim().length() == 0) {
			return true;
		}
		return false;
	}
	
	public HSUtils(String encoding) {
		this.encoding = encoding;
	}
	
	public byte[] readToken(byte[] data, int index)
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int idx = index;
		while(!(data[idx] == 0x09 || data[idx] == 0x0a))
		{
			out.write(data[index]);
			idx = idx +1;
		}
		idx = idx+1;
		out.toByteArray();
		return null;
	}

	public byte[] filterLeft(byte[] data)
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int i = 0;
		while(i<data.length)
		{
			if (data[i] == 0x01)
			{
				out.write(data[i+1]^0x40);
				i = i+2;
			}
			else
			{
				out.write(data[i]);
				i = i+1;
			}
		}
		return out.toByteArray();
	}

	public ResultInfo parse(byte[] data) throws UnsupportedEncodingException {
		ResultInfo result = new ResultInfo();
		for (int i = 0; i < data.length;) {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			int errorCode = data[i] - 0x30;
			i++;
			if (i >= data.length)
				break;
			if (data[i] != Const.TOKEN_SEPARATOR_BYTE)
				throw new RuntimeException(); // TOOD
			i++;
			if (i >= data.length)
				break;// 0x09
			int numColumns = data[i] - 0x30;
			result.setErrorCode(errorCode);
			result.setNumColumns(numColumns);
			i++;
			if (i >= data.length)
				break;

			if (data[i] == Const.COMMAND_TERMINATE_BYTE) {
				result.addMessages("");
				i++;// 0x09 or 0x0a
				continue;
			} else {
				i++;// 0x09 or 0x0a
			}

			while (true) {
				if (data.length <= i)
					break;
				byte b = data[i];
				i++;
				if (b == Const.COMMAND_TERMINATE_BYTE) {
					result.addMessages(new String(buf.toByteArray(), this.encoding));
					break;
				}
				if (b == Const.TOKEN_SEPARATOR_BYTE) {
					result.addMessages(new String(buf.toByteArray(), this.encoding));
					buf = new ByteArrayOutputStream();
					continue;
				}
				buf.write(b);
			}
		}
		return result;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}

package com.alibaba.hsclient.commond;

import java.io.UnsupportedEncodingException;
import com.alibaba.hsclient.Command;
import com.alibaba.hsclient.util.Const;

/**
 * <indexid> + <vlen> <v1> ... <vn>
 * @author yinghui.huangyh
 *
 */
public class InsertCommand implements Command{
	private int indexId;
	private String operator = Const.INSERT_OPERATOR;
	private String[] fieldValues;
	
	public InsertCommand(int indexId, String[] fieldValues) {
		super();
		this.indexId = indexId;
		this.fieldValues = fieldValues;
	}

	public int getIndexId() {
		return indexId;
	}

	public void setIndexId(int indexId) {
		this.indexId = indexId;
	}

	public String[] getFieldValues() {
		return fieldValues;
	}

	public void setFieldValues(String[] fieldValues) {
		this.fieldValues = fieldValues;
	}

	public byte[] toByte(String encoding) throws UnsupportedEncodingException {
		//<indexid> + <vlen> <v1> ... <vn>
		StringBuffer sb = new StringBuffer();
		sb.append(this.getIndexId()).append(Const.TOKEN_SEPARATOR)
		  .append(this.operator).append(Const.TOKEN_SEPARATOR)
		  .append(this.fieldValues.length).append(Const.TOKEN_SEPARATOR);
		
		for (int i=0; i< fieldValues.length; i++) {
			if (i == fieldValues.length -1)
			{
				sb.append(fieldValues[i]);
			}
			else
			{
				sb.append(fieldValues[i]).append(Const.TOKEN_SEPARATOR);
			}
		}
		
		sb.append(Const.COMMAND_TERMINATE);
		return sb.toString().getBytes(encoding);
	}
	
}

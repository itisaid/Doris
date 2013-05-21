package com.alibaba.hsclient.commond;

import java.io.UnsupportedEncodingException;

import com.alibaba.hsclient.Command;
import com.alibaba.hsclient.bean.FilterInfo;
import com.alibaba.hsclient.bean.InInfo;
import com.alibaba.hsclient.bean.LimitInfo;
import com.alibaba.hsclient.bean.ModInfo;
import com.alibaba.hsclient.util.CompareOperator;
import com.alibaba.hsclient.util.Const;
import com.alibaba.hsclient.util.ModOperator;

public class IncrementCommand implements Command{
	private int indexId;
	private CompareOperator operator;
	private String[] fieldValues;
	private LimitInfo limitInfo;
	private InInfo inInfo;
	private FilterInfo[] filterInfos;
	private ModInfo modInfo;

	public IncrementCommand(int indexId, CompareOperator operator, String[] fieldValues,
			LimitInfo limitInfo, InInfo inInfo, FilterInfo[] filterInfos,
			ModInfo modInfo) {
		super();
		this.indexId = indexId;
		this.operator = operator;
		this.fieldValues = fieldValues;
		this.limitInfo = limitInfo;
		this.inInfo = inInfo;
		this.filterInfos = filterInfos;
		this.modInfo = modInfo;
	}

	public int getIndexId() {
		return indexId;
	}

	public void setIndexId(int indexId) {
		this.indexId = indexId;
	}

	public CompareOperator getOperator() {
		return operator;
	}
	
	public void setOperator(CompareOperator operator)
	{
		this.operator = operator;
	}

	public String[] getFieldValues() {
		return fieldValues;
	}

	public void setFieldValues(String[] fieldValues) {
		this.fieldValues = fieldValues;
	}

	public LimitInfo getLimitInfo() {
		return limitInfo;
	}

	public void setLimitInfo(LimitInfo limitInfo) {
		this.limitInfo = limitInfo;
	}

	public InInfo getInInfo() {
		return inInfo;
	}

	public void setInInfo(InInfo inInfo) {
		this.inInfo = inInfo;
	}

	public FilterInfo[] getFilterInfos() {
		return filterInfos;
	}

	public void setFilterInfos(FilterInfo[] filterInfos) {
		this.filterInfos = filterInfos;
	}

	public ModInfo getModInfo() {
		return modInfo;
	}

	public void setModInfo(ModInfo modInfo) {
		this.modInfo = modInfo;
	}

	public byte[] toByte(String encoding) throws UnsupportedEncodingException {
		// <indexid> <op> <vlen> <v1> ... <vn> [LIM] [IN] [FILTER ...] MOD
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getIndexId()).append(Const.TOKEN_SEPARATOR)
		  .append(this.operator.getValue()).append(Const.TOKEN_SEPARATOR)
		  .append(this.fieldValues.length).append(Const.TOKEN_SEPARATOR);
		
		for (String str : fieldValues) {
			sb.append(str).append(Const.TOKEN_SEPARATOR);
		}

		if (this.limitInfo != null) {
			sb.append(this.limitInfo.getLimit())
					.append(Const.TOKEN_SEPARATOR)
					.append(this.limitInfo.getOffset())
					.append(Const.TOKEN_SEPARATOR);
		}

		if (this.inInfo != null) {
			sb.append(this.inInfo.getInOperator())
					.append(Const.TOKEN_SEPARATOR)
					.append(this.inInfo.getiCol())
					.append(Const.TOKEN_SEPARATOR)
					.append(this.inInfo.getInValues().length)
					.append(Const.TOKEN_SEPARATOR);
			for (String str : this.inInfo.getInValues()) {
				sb.append(str).append(Const.TOKEN_SEPARATOR);
			}
		}

		if (this.filterInfos != null) {
			for (FilterInfo filterInfo : this.filterInfos) {
				sb.append(filterInfo.getfType())
						.append(Const.TOKEN_SEPARATOR)
						.append(filterInfo.getfOp())
						.append(Const.TOKEN_SEPARATOR)
						.append(filterInfo.getfCol())
						.append(Const.TOKEN_SEPARATOR)
						.append(filterInfo.getfVal())
						.append(Const.TOKEN_SEPARATOR);
			}
		}

		sb.append(ModOperator.INC).append(Const.TOKEN_SEPARATOR);

		for (String mk : this.modInfo.getMopValues()) {
			sb.append(mk).append(Const.TOKEN_SEPARATOR);
		}
		sb.append(Const.COMMAND_TERMINATE);
		return sb.toString().getBytes(encoding);
	}

}

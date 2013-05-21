package com.alibaba.hsclient.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;
import com.alibaba.hsclient.HSClient;
import com.alibaba.hsclient.IndexSession;
import com.alibaba.hsclient.bean.FilterInfo;
import com.alibaba.hsclient.bean.InInfo;
import com.alibaba.hsclient.bean.LimitInfo;
import com.alibaba.hsclient.bean.ModInfo;
import com.alibaba.hsclient.bean.ResultInfo;
import com.alibaba.hsclient.exception.HandlerSocketException;
import com.alibaba.hsclient.util.CompareOperator;

public class IndexSessionImpl implements IndexSession {
	private final HSClient client;
	private final int indexId;
	private final String[] columns;

	public String[] getColumns() {
		return this.columns;
	}

	public IndexSessionImpl(HSClient client, int indexId, String[] columns) {
		super();
		this.client = client;
		this.indexId = indexId;
		this.columns = columns;
	}

	public int getIndexId() {
		return this.indexId;
	}

	
	public ResultInfo find(CompareOperator operator, String[] fieldValues,
			LimitInfo limitInfo, InInfo inInfo, FilterInfo[] filterInfos)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException {
		return this.client.find(this.indexId, operator, fieldValues, limitInfo, inInfo, filterInfos);
	}

	
	public ResultInfo find(String[] fieldValues)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException {
		return this.client.find(this.indexId, fieldValues);
	}

	
	public boolean update(CompareOperator operator, String[] fieldValues,
			LimitInfo limitInfo, InInfo inInfo, FilterInfo[] filterInfos,
			ModInfo modInfo) throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException {
		return this.client.update(this.indexId, operator, fieldValues, limitInfo, inInfo, filterInfos, modInfo);
	}

	
	public boolean update(CompareOperator operator, String[] fieldValues,
			ModInfo modInfo) throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException {
		return this.client.update(this.indexId, operator, fieldValues, modInfo);
	}

	
	public boolean delete(CompareOperator operator, String[] fieldValues,
			LimitInfo limitInfo, InInfo inInfo, FilterInfo[] filterInfos)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException {
		return this.client.delete(this.indexId, operator, fieldValues, limitInfo, inInfo, filterInfos);
	}

	
	public boolean delete(CompareOperator operator, String[] values)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException {
		return this.client.delete(this.indexId, operator, values);
	}

	
	public boolean delete(String[] fieldValues)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException {
		return this.client.delete(this.indexId, fieldValues);
	}

	
	public boolean insert(String[] fieldValues)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException {
		return this.client.insert(this.indexId, fieldValues);
	}

	
	public boolean increment(CompareOperator operator, String[] fieldValues,
			LimitInfo limitInfo, InInfo inInfo, FilterInfo[] filterInfos,
			ModInfo modInfo) throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException {
		return this.client.increment(this.indexId, operator, fieldValues, limitInfo, inInfo, filterInfos, modInfo);
	}

	
	public boolean increment(CompareOperator operator, String[] fieldValues,
			ModInfo modInfo) throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException {
		return this.client.increment(this.indexId, operator, fieldValues, modInfo);
	}

	
	public boolean decrement(CompareOperator operator, String[] fieldValues,
			LimitInfo limitInfo, InInfo inInfo, FilterInfo[] filterInfos,
			ModInfo modInfo) throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException {
		return this.client.decrement(this.indexId, operator, fieldValues, limitInfo, inInfo, filterInfos, modInfo);
	}

	
	public boolean decrement(CompareOperator operator, String[] fieldValues,
			ModInfo modInfo) throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException {
		return this.client.decrement(this.indexId, operator, fieldValues, modInfo); 
	}

}

package com.alibaba.hsclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;
import com.alibaba.hsclient.bean.FilterInfo;
import com.alibaba.hsclient.bean.InInfo;
import com.alibaba.hsclient.bean.LimitInfo;
import com.alibaba.hsclient.bean.ModInfo;
import com.alibaba.hsclient.bean.ResultInfo;
import com.alibaba.hsclient.exception.HandlerSocketException;
import com.alibaba.hsclient.util.CompareOperator;

public interface IndexSession {

	public int getIndexId();

	public String[] getColumns();

	public ResultInfo find(CompareOperator operator, String[] fieldValues,
			LimitInfo limitInfo, InInfo inInfo, FilterInfo[] filterInfos)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public ResultInfo find(String[] fieldValues)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public boolean update(CompareOperator operator, String[] fieldValues,
			LimitInfo limitInfo, InInfo inInfo, FilterInfo[] filterInfos,
			ModInfo modInfo) throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public boolean update(CompareOperator operator, String[] fieldValues,
			ModInfo modInfo) throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public boolean delete(CompareOperator operator, String[] fieldValues,
			LimitInfo limitInfo, InInfo inInfo, FilterInfo[] filterInfos)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public boolean delete(CompareOperator operator, String[] values)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public boolean delete(String[] fieldValues)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public boolean insert(String[] fieldValues)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public boolean increment(CompareOperator operator, String[] fieldValues,
			LimitInfo limitInfo, InInfo inInfo, FilterInfo[] filterInfos,
			ModInfo modInfo) throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public boolean increment(CompareOperator operator, String[] fieldValues,
			ModInfo modInfo) throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public boolean decrement(CompareOperator operator, String[] fieldValues,
			LimitInfo limitInfo, InInfo inInfo, FilterInfo[] filterInfos,
			ModInfo modInfo) throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public boolean decrement(CompareOperator operator, String[] fieldValues,
			ModInfo modInfo) throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

}

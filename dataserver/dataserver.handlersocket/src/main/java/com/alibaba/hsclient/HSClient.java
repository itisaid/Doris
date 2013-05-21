package com.alibaba.hsclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import com.alibaba.hsclient.bean.FilterInfo;
import com.alibaba.hsclient.bean.InInfo;
import com.alibaba.hsclient.bean.IndexInfo;
import com.alibaba.hsclient.bean.LimitInfo;
import com.alibaba.hsclient.bean.ModInfo;
import com.alibaba.hsclient.bean.ResultInfo;
import com.alibaba.hsclient.exception.HandlerSocketException;
import com.alibaba.hsclient.util.CompareOperator;

public interface HSClient {

	public void connect() throws IOException;

//	public void connect(InetAddress address, int port) throws IOException;

	public void close() throws IOException;
	
	public Map<Integer, IndexInfo> getIndexMap();
	
//	public List<ResultInfo> execute() throws IOException;

	public IndexSession openIndexSession(int indexId, String dbname,
			String tableName, String indexName, String[] columns, String[] fcolumns)
			throws InterruptedException, TimeoutException,
			HandlerSocketException,IOException;

	public IndexSession openIndexSession(int indexId, String dbname,
			String tableName, String indexName, String[] columns)
			throws InterruptedException, TimeoutException,
			HandlerSocketException,IOException;
	
	public IndexSession openIndexSession(String dbname, String tableName,
			String indexName, String[] columns) throws InterruptedException,
			TimeoutException, HandlerSocketException,IOException;
	
	public IndexSession openIndexSession(String dbname, String tableName,
			String indexName, String[] columns, String[] fcolumns) throws InterruptedException,
			TimeoutException, HandlerSocketException,IOException;
	
	public boolean openIndex(int indexId, String dbName, String tableName,
			String indexName, String[] columns) throws InterruptedException,
			TimeoutException, HandlerSocketException,
			UnsupportedEncodingException,IOException;

	public boolean openIndex(int indexId, String dbName, String tableName,
			String indexName, String[] columns, String[] fColumns)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public ResultInfo find(int indexId, CompareOperator operator, String[] fieldValues,
			LimitInfo limitInfo, InInfo inInfo, FilterInfo[] filterInfos)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public ResultInfo find(int indexId, String[] fieldValues)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public boolean update(int indexId, CompareOperator operator, String[] fieldValues,
			LimitInfo limitInfo, InInfo inInfo, FilterInfo[] filterInfos,
			ModInfo modInfo) throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public boolean update(int indexId, CompareOperator operator, String[] fieldValues,
			ModInfo modInfo) throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public boolean delete(int indexId, CompareOperator operator, String[] fieldValues,
			LimitInfo limitInfo, InInfo inInfo, FilterInfo[] filterInfos)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public boolean delete(int indexId, CompareOperator operator, String[] values)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public boolean delete(int indexId, String[] fieldValues)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public boolean insert(int indexId, String[] fieldValues)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public boolean increment(int indexId, CompareOperator operator, String[] fieldValues,
			LimitInfo limitInfo, InInfo inInfo, FilterInfo[] filterInfos,
			ModInfo modInfo) throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public boolean increment(int indexId, CompareOperator operator, String[] fieldValues,
			ModInfo modInfo) throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public boolean decrement(int indexId, CompareOperator operator, String[] fieldValues,
			LimitInfo limitInfo, InInfo inInfo, FilterInfo[] filterInfos,
			ModInfo modInfo) throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;

	public boolean decrement(int indexId, CompareOperator operator, String[] fieldValues,
			ModInfo modInfo) throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException,IOException;
}

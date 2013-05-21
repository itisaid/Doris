package com.alibaba.doris.common.adminservice;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.doris.common.AdminServiceConstants;
import com.alibaba.doris.common.adminservice.connenctor.AdminConnector;

abstract public class BaseAdminService<T> {
	private Log log = LogFactory.getLog(BaseAdminService.class);
	private AdminConnector httpService = AdminConnector.getInstance();

	private boolean lock = false;
	private T result;
	private long refreshTime = 500; // ms，该时间段内只访问admin server一次
	private long accessTime; // ms,上次访问的timestamp

	public synchronized T requestForce(Map<String, String> paramMap) {// 定时器使用该方法

		access(paramMap);
		return result;
	}

	public synchronized T requestRefresh(Map<String, String> paramMap) { // 正常请求调用，避免多线程强制刷新给admin
		// server造成压力
		if (!lock) {
			lock = true;

			access(paramMap);

			accessTime = System.currentTimeMillis();
		}

		if ((System.currentTimeMillis() - accessTime) > refreshTime) {

			access(paramMap);

			lock = false;
		}
		return result;
	}

	private void access(Map<String, String> paramMap) {

		if (paramMap == null) {
			paramMap = new HashMap<String, String>();
		}

		if (StringUtils.isNotEmpty(getActionName())) {
			paramMap.put(AdminServiceConstants.ADMIN_SERVICE_ACTION_NAME,
					getActionName());
		}

		String response = httpService.requst(paramMap);
		if (log.isDebugEnabled()) {
			log.debug("access admin server-- request map:" + paramMap
					+ " response:" + response);
		}
		if (AdminServiceConstants.ADMIN_SERVICE_ERROR.equals(response)) {
			result = convert(null);
		} else {
			result = convert(response);
		}
	}

	abstract public T convert(String response);

	abstract public String getActionName();
}

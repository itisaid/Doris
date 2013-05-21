/**
 * @author He Kun (Henry He), Guangzhou, China
 * 2006-4-19
 */
package com.alibaba.doris.dproxy;

import java.lang.reflect.Method;

/**
 * InvokeInfo
 * <br> Description: class InvokeInfo
 * <br> Company: Alibaba, Hangzhou</br>
 * @author He Kun
 * @since 1.0
 * @version 1.0
 * 2006-4-19
 */
public class InvokeInfo {
	
	private Object obj;
	
	private Object proxy;
	
	private Method method;
	
	private Object[] args;
	
	private Object result;
	
	private Throwable exception;
	
	private boolean stopInvoke;
	
	private boolean stopProxy;
	
	public InvokeInfo(Object obj, Object proxy,Method method,Object[] args, Object result, Throwable exception) {
		this.obj = obj;
		this.proxy = proxy;
		this.method = method;
		this.args = args;
		this.result = result;
		this.exception = exception;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object getProxy() {
		return proxy;
	}

	public void setProxy(Object proxy) {
		this.proxy = proxy;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public boolean isStopInvoke() {
		return stopInvoke;
	}

	public void setStopInvoke(boolean stopInvoke) {
		this.stopInvoke = stopInvoke;
	}

	public boolean isStopProxy() {
		return stopProxy;
	}

	public void setStopProxy(boolean stopProxy) {
		this.stopProxy = stopProxy;
	}
	
}

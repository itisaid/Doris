package com.alibaba.doris.dproxy.interceptor;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.alibaba.doris.dproxy.AbstractInterceptor;
import com.alibaba.doris.dproxy.InvokeInfo;

/**
 * e
 * LogInterceptor
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-4-29
 */
public class LogInterceptor extends AbstractInterceptor {
	
	private static Logger log = Logger.getLogger( LogInterceptor.class.getName());
	/**
	 * 
	 */
	public void before(InvokeInfo info) throws Throwable {		
		super.before(info);		
		log.log(Level.FINE , info.getMethod().getName());
		
	}
	
	
	public void after(InvokeInfo info) throws Throwable {
		super.after(info);
		info.setResult("[AOP]" + info.getResult() );
		log.log(Level.FINE , info.getMethod().getName());
	}
}

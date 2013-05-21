/**
 * @author He Kun (Henry He), Guangzhou, China
 * 2006-4-19
 */
package com.alibaba.doris.dproxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * InterceptorGroup
 * <br> Description: class InterceptorGroup
 * <br> Company: Alibaba, Hangzhou</br>
 * @author He Kun
 * @since 1.0
 * @version 1.0
 * 2006-4-19
 */
@SuppressWarnings("unchecked")
public class InterceptorGroup implements MethodInterceptor {
	
	private String name ;
	
	private Class clazz ;
	private String description ;
	
	private List interceptors = new ArrayList(3);
	
	private Map includeMethods = new HashMap(4); //methods included to be intercepted.
	
	private static Map excludeMethods = new HashMap();
	
	static {
		excludeMethods.put("toString","toString");
		excludeMethods.put("hashCode","hashCode");
		excludeMethods.put("clone","clone");
		excludeMethods.put("equals","equals");
		excludeMethods.put("getClass","getClass");				
	}
	
	
	public InterceptorGroup() {
	}
	

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Class getClazz() {
		return clazz;
	}
	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}
	public void setInterceptors(List interceptors) {
		this.interceptors = interceptors;
	}
	
	/**
	 * intercept
	 */
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		
		Object result = null;	
		InvokeInfo invokeInfo = new InvokeInfo(obj,proxy,method,args,result,null);
		
		try {
			//before			
			invokeInterceptorBefore( invokeInfo );

			//execute the method
			if(!invokeInfo.isStopInvoke()) {  

				result = proxy.invokeSuper( obj , args);			
				invokeInfo.setResult(result);
				
				if(invokeInfo.isStopProxy()) 
					return invokeInfo.getResult();
			}
			
			//after
			invokeInterceptorAfter( invokeInfo );
			
		}catch(Throwable e) {
			Throwable t = e instanceof InvocationTargetException ? e.getCause() : e;
			invokeInfo.setException(t);
			
			invokeInterceptorExceptionThrow(invokeInfo);
		}		
		return invokeInfo.getResult();
	}
	
	/**
	 * Judge whether this method is excluded.
	 * @param info
	 */
	public boolean isIncludeMethod(InvokeInfo invokeInfo) {
		return getIncludeMethods().containsKey(invokeInfo.getMethod().getName() ) && !isExcludeMethod(invokeInfo);
	}
	
	/**
	 * 
	 * @param info
	 * @return
	 */
	public boolean isExcludeMethod(InvokeInfo info) {
		Method method = info.getMethod();
		
//		if( "void".equals(method.getReturnType().getName())) {
//			info.setStopProxy(true);
//			return true;
//		}
		if(  !Modifier.isPublic(method.getModifiers()) && !Modifier.isProtected(method.getModifiers()))  {
			info.setStopProxy(true);
			return true;
		}
		
		if(excludeMethods.containsKey(method.getName())) {
			info.setStopProxy(true);
			return true;
		}
		return false;
	}


	
	public List getInterceptors() {	
		if(interceptors==null) interceptors = new ArrayList(3);
		return interceptors;
	}
	public Map getIncludeMethods() {
		return includeMethods;
	}
	
	public void setIncludeMethods(String[] methods) {
		if(methods!=null)
		for (int i = 0; i < methods.length; i++) {
			includeMethods.put(methods[i], methods[i]);
		}
	}	
	
	public void registerInterceptor(Class clazz) {
		if(!Interceptor.class.isAssignableFrom(clazz))
			throw new IllegalArgumentException("Interceptor class must implement interface " + Interceptor.class.getName() +" . But it's " + clazz.getName());
		List interceptorList = getInterceptors();
		try {
			Interceptor interceptor = (Interceptor) clazz.newInstance();
			interceptorList.add(interceptor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void removeInterceptor(Class clazz) {
		List tors = getInterceptors();
		tors.remove(clazz);
	}

	private void invokeInterceptorBefore(InvokeInfo invokeInfo) throws Throwable  {
		
		if( !isIncludeMethod(invokeInfo))
			return ;
		
		List interceptors = getInterceptors();
		
		for (Iterator iter = interceptors.iterator(); iter.hasNext();) {			
			try {
				Interceptor interceptor = (Interceptor)iter.next(); 

				interceptor.before(invokeInfo);
			} catch (Throwable e) {				
				throw e;
//				invokeInfo.setException( e );
			}
		}
	}

	private void invokeInterceptorAfter(InvokeInfo invokeInfo) throws Throwable {
		if( !isIncludeMethod(invokeInfo))
			return ;
		
		List interceptors = getInterceptors();
		
		for (Iterator iter = interceptors.iterator(); iter.hasNext();) {			
			try {
				Interceptor interceptor = (Interceptor)iter.next(); 
				interceptor.after(invokeInfo);
			} catch (Throwable e) {				
				throw e;
			}
		}
	}
	
	private void invokeInterceptorExceptionThrow(InvokeInfo invokeInfo) throws Throwable {
		if( !isIncludeMethod(invokeInfo)) {			
			throw invokeInfo.getException();
		}
			
		List interceptors = getInterceptors();
		
		for (Iterator iter = interceptors.iterator(); iter.hasNext();) {			
			try {
				Interceptor interceptor = (Interceptor)iter.next(); 
				
				interceptor.exceptionThrow(invokeInfo);
			} catch (Throwable e) {				
//				e.printStackTrace();
				throw e;
			}
		}
	}
}

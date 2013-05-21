package com.alibaba.doris.dproxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.cglib.proxy.Enhancer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;



/**
 * Proxy Factory. A AOP factory.
 * It use a specified class to create a instance of which the method invocation would be intercepted by Interceptors. 
 * @author He Kun
 *
 */
public class ProxyFactory {
	
	private static Logger logger = Logger.getLogger( ProxyFactory.class.getName() );
	
	private static String defaultConfigLocation = "common-aop.xml";
	
	private String configLocation = defaultConfigLocation;
	
	private Map<String,InterceptorGroup> handlerMap = new HashMap<String,InterceptorGroup>(5);
	
	static {
		logger.setLevel( Level.INFO );
	}
	
	public ProxyFactory() {		
		loadConfig( defaultConfigLocation );
	}
	
	public ProxyFactory(String configLocation) {
		this.configLocation = configLocation;
		loadConfig( this.configLocation );
	}
	/**
	 * Load config file.
	 * @param configName
	 */
	public void loadConfig(String configName) {
		SAXReader saxReader = new SAXReader();
		URL url = Thread.currentThread().getContextClassLoader().getResource(configName);
		
		if(url == null) {
			url = ProxyFactory.class.getResource(configName);
		}
		
		if(url== null) {
			throw new RuntimeException("Config not found! " + configName);
		}
			
		InputStream in = null;
		Document document;
		try {
			in = url.openStream();
			document = saxReader.read(in);
			Element root = document.getRootElement();
			List<?> handlers = root.selectNodes("interceptorGroups/interceptorGroup");
			
			Iterator<?> iter = handlers.iterator();
			while (iter.hasNext()) {
				Element handlerEle = (Element) iter.next();
				String handlerName = handlerEle.attributeValue("name"); // InterceptorGroup name
				String handlerDescription = handlerEle.attributeValue("description"); //InterceptorGroup description
				String includeMethodString = handlerEle.attributeValue("methods"); //InterceptorGroup description
				
				if(handlerName == null || "".equals(handlerName.trim())) {
					throw new RuntimeException( "InterceptorGroup 'name' attribute can't be empty! ");
				}
				
				
				InterceptorGroup group = new InterceptorGroup();
				group.setName(handlerName);
				group.setDescription(handlerDescription);
				
				if(includeMethodString!=null && includeMethodString.length() > 0) {
					String[] methods = includeMethodString.split(",");
					group.setIncludeMethods(methods);
				}				
				loadHandler(handlerEle,group);
				
			}
		} catch (DocumentException e) {
			throw new RuntimeException("Can't locad config " + configName + " " + e.getMessage());
		} catch (IOException e) {
			throw new RuntimeException("Can't locad config " + configName + " " + e.getMessage()) ;
		}
		finally {
			 if (in!=null) 
				 try { in.close();	 }catch(IOException e) { }
		}
	}
	
	private void loadHandler(Element handlerEle, InterceptorGroup group ) {
		Iterator<?> interceptors = handlerEle.elementIterator("interceptor");
		while (interceptors.hasNext()) {
			Element interceptorEle = (Element) interceptors.next();
			String interceptorClass = interceptorEle.attributeValue("class").trim(); //
			try {
				Class<?> clazz = Class.forName(interceptorClass);
				if(!Interceptor.class.isAssignableFrom(clazz)) {
					throw new RuntimeException("Interceptor class " + interceptorClass +" must implement interface " + Interceptor.class.getName() +" OR extends class " + AbstractInterceptor.class.getName());
				}else {
					group.registerInterceptor(clazz);
				}
					
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Interceptor class " + interceptorClass +" not found");
			}
		}
		handlerMap.put(group.getName(), group);	
	}
	
	/**
	 * Create proxy instance. 
	 * @param proxyClass
	 * @param includeMethods method to be intercepted.
	 * @param handlerName
	 * @return
	 * @throws InstantiationException
	 */
	public Object createObject(Class<?> proxyClass,String handlerName) throws InstantiationException {
		InterceptorGroup handler = (InterceptorGroup) handlerMap.get(handlerName);
		if(handler == null) {
			throw new InstantiationException("InterceptorGroup not found name:" + handlerName);
		}
		
		return createObject(proxyClass, null, handler);
	}
	
	/**
	 * Create proxy instance. 
	 * @param proxyClass
	 * @param includeMethods method to be intercepted.
	 * @param handlerName
	 * @return
	 * @throws InstantiationException
	 */
	public Object createObject(Class<?> proxyClass,String[] includeMethods,String handlerName) throws InstantiationException {
		InterceptorGroup handler = (InterceptorGroup) handlerMap.get(handlerName);
		if(handler == null) {
			throw new InstantiationException("InterceptorGroup not found name:" + handlerName);
		}
		
		return createObject(proxyClass, includeMethods, handler);
	}
	
	public Object createObject(Class<?> proxyClass,InterceptorGroup handler) throws InstantiationException {
		return createObject(proxyClass, null, handler);
	}
	
	public Object createObject(Class<?> proxyClass,String[] includeMethods,InterceptorGroup interceptorGroup) throws InstantiationException {
		 Enhancer enhancer = new Enhancer();
		 enhancer.setSuperclass(proxyClass);  
		 
		//override configed include methods by API
		if(includeMethods != null && includeMethods.length > 0)
			interceptorGroup.setIncludeMethods(includeMethods);
		
		interceptorGroup.setName(interceptorGroup.getName());
		interceptorGroup.setDescription(interceptorGroup.getDescription());
		interceptorGroup.setClazz(interceptorGroup.getClazz());
		
		enhancer.setCallback( interceptorGroup );
		Object proxyObject = enhancer.create();
		return proxyObject;
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		try {
			ProxyFactory proxyFactory = new ProxyFactory("common-aop.xml");
//			Map<String,String> map = (Map<String,String>)proxyFactory.createObject(HashMap.class, new String[] {"get"}, "AddOperationInterceptorHandler");
			
			Map<String,String> map = (Map<String,String>)proxyFactory.createObject(HashMap.class, "MapInterceptorGroup");
			
			System.out.println("AOP Demo: intercept Map.get method.");
			
			map.put("key1", "value1");			
			String v = map.get("key1");
			System.out.println("map.put(\"key1\", \"value1\") \r\n map.get('key1'):" + v);
			map.clear();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
	}
}

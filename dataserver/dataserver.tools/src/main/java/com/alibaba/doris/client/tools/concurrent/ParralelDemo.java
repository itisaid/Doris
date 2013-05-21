/**
 * 
 */
package com.alibaba.doris.client.tools.concurrent;


/**
 * @author raymond
 *
 */
public class ParralelDemo {
	
	public static void main(String[] args) {
		int start = 0;
		int end = 1000;
		int concurrent = 10;
		boolean needProfiling = true;
		ParralelExecutor executor = new ParralelExecutorImpl(start,end, concurrent, needProfiling , ParralelTaskImpl.class);
		executor.setNeedPofiling( true );
		 executor.start();
		
		Object result;
		try {
			result = executor.getResult();
			System.out.println("result: " + result );
		} catch(Exception e) {
		}
		
	}
}

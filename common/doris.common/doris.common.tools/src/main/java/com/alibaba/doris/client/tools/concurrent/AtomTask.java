/**
 * 
 */
package com.alibaba.doris.client.tools.concurrent;

/**
 * @author raymond
 *
 */
public interface AtomTask {
	
	void setIndex(long index) ;
	
	long getIndex();
	
	void doRun(long index);
}

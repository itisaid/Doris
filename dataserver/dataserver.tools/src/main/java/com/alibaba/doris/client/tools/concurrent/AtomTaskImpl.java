/**
 * 
 */
package com.alibaba.doris.client.tools.concurrent;

/**
 * @author raymond
 *
 */
public class AtomTaskImpl implements AtomTask {
	
	private long index;
	/**
	 * @see com.alibaba.doris.client.tools.concurrent.AtomTask#getIndex()
	 */
	public long getIndex() {
		return index;
	}

	/**
	 * @see com.alibaba.doris.client.tools.concurrent.AtomTask#setIndex(long)
	 */
	public void setIndex(long index) {
		this.index = index;
	}
	
	public void doRun(long index) {
		System.out.println("AtomTask.doRun->" + index);
	}

}

/**
 * 
 */
package com.alibaba.doris.client.tools.concurrent;

import java.util.concurrent.CountDownLatch;

/**
 * @author raymond
 *
 */
public interface ParralelTask extends Runnable {

	public long getStart();

	public void setStart(long start);

	public long getEnd();

	public void setEnd(long end);
	
	public void doRun(long index) ;

	public int getNo();

	public void setPermMeter(PermMeter permMeter);

	public void setNeedProfiling(boolean needPofiling);

	public void setNo(int i);


	public void setCountDownLatch(CountDownLatch countDownLatch);

	public void setResultCountDownLatch(CountDownLatch resultCountDownLatch);

	public void setNeedPofiling(boolean needPofiling);

	public PermMeter getPermMeter();
}

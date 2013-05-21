/**
 * 
 */
package com.alibaba.doris.client.tools.concurrent;

/**
 * @author raymond
 *
 */
public interface ParralelExecutor {
	
	public int getStart();

	public void setStart(int start);

	public int getEnd();

	public void setEnd(int end);

	public int getConcurrent();

	public void setConcurrent(int concurrent);

	public boolean isNeedPofiling();

	public void setNeedPofiling(boolean needPofiling);

	public void start();

	public Object getResult();

	public void setParralelTaskFactory(ParralelTaskFactory parralelTaskFactory);

	public PermMeter getPermMeter();
}

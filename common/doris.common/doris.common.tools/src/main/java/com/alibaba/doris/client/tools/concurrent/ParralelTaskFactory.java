/**
 * 
 */
package com.alibaba.doris.client.tools.concurrent;

import java.util.concurrent.CountDownLatch;

/**
 * @author raymond
 *
 */
public class ParralelTaskFactory {

	
	public ParralelTaskFactory() {
		
	}
	public ParralelTask createTask(Class<? extends ParralelTask> parralelTaskClass, int i, long start, long end,
			CountDownLatch countDownLatch, CountDownLatch resultCountDownLatch) {
		
		ParralelTask parralelTask;
		try {
			parralelTask = parralelTaskClass.newInstance();
			parralelTask.setNo( i );
			parralelTask.setStart(start);
			parralelTask.setEnd(end);
			parralelTask.setCountDownLatch(countDownLatch);
			parralelTask.setResultCountDownLatch(resultCountDownLatch);
//			ParralelTask parralelTask = new ParralelTaskImpl( i, start,  end , countDownLatch, resultCountDownLatch);
			return parralelTask;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}

package com.alibaba.doris.dataserver.store.innodb.write;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;

public class InnoWriteThread extends Thread {
	private BlockingQueue<WriteOperation> opsToDo;
	private CountDownLatch latch;
	
	public InnoWriteThread(BlockingQueue<WriteOperation> ops, CountDownLatch latch) {
		this.opsToDo = ops;
		this.latch = latch;
	}
	
	public void run() {
		this.latch.countDown();
		
		while (true) {
			WriteOperation operation = null;
			try {
				operation = opsToDo.take();
				Key key = operation.getKey();
				Value value = operation.getValue();
				
				switch (operation.getOpType()) {
				case INSERT:
				case UPDATE:
					boolean isSetWithCompareVersion = operation.getIsSetWithCompareVersion();
					operation.getStore().set(key, value, isSetWithCompareVersion);
					operation.setResult(true);
					break;
				case DELETE:
					Boolean result = operation.getStore().delete(key, value);
					operation.setResult(result);
					break;
				default:
					break;
				}
			} catch (RuntimeException e) {
				// save exception as result
				operation.setResult(e.getCause());
			} catch (Exception e) {
				operation.setResult(e);
			}
		}
	}
}


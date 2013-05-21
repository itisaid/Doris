/**
 * 
 */
package com.alibaba.doris.client.tools.concurrent;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author raymond
 *
 */
public class PermMeter { 
	private AtomicLong tps = new AtomicLong();
	private AtomicLong maxTps = new AtomicLong();
	
	private AtomicLong count = new AtomicLong();
	private AtomicLong min = new AtomicLong();
	private AtomicLong max = new AtomicLong();
	private AtomicLong avg = new AtomicLong();
	
	private ReentrantLock reentrantLock = new ReentrantLock();
	
	public void startRecord() {
		tps.addAndGet(1);
		
		if( tps.get() > maxTps.get() ) {
			maxTps.compareAndSet( maxTps.get() , tps.get() );
		}
	}
	
	public void endRecord() {
		long current =  tps.get();
		tps.compareAndSet( current , current - 1);
	}
	
	public void addItem(long time) {		
		reentrantLock.lock();		
		try {
			
			count.addAndGet(1);
			
			long cmin = min.get();
			if( time < cmin ) 
				min.compareAndSet( cmin , time);
			
			long cmax = max.get();
			if( time > cmax ) 
				max.compareAndSet( cmax , time);
			
			long cavg = avg.get();
			
			long newAvg = (cavg + time)/2;
			avg.compareAndSet(cavg, newAvg  );
		}finally {
			reentrantLock.unlock();
		}
	}

	public void printReport() {
		System.out.println(String.format("Performance: count:%d, min: %d, max: %d, tps: %d, maxTps: %d, avg:%d", count.get(), min.get(), max.get(), tps.get(), maxTps.get(),  avg.get()  ));
	}
}

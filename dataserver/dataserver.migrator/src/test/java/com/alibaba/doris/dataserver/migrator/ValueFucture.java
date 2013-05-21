/*
Copyright(C) 2010 Alibaba Group Holding Limited
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.alibaba.doris.dataserver.migrator;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Raymond He ( He Kun), raymond.he.kk@gmail.com
 * @since 1.0
 * 2011-6-29
 */
public class ValueFucture<V extends Object, X> implements Future<V> {
	
	private X expectedValue;
	private V  value;
	private boolean done;
	
	private Semaphore signal;
	
	public ValueFucture(X expectedValue) {
		this( expectedValue , new Semaphore(  0 ));
	}
	
	public ValueFucture(X expectedValue, Semaphore signal) {
		
		if( expectedValue == null) {
			throw new NullPointerException("ExpectedValue shoudn't be null!");
		}
		this.expectedValue = expectedValue;
		this.signal = signal;
	}
	
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}
	
	public V get() throws InterruptedException, ExecutionException {
		signal.acquire();
		return getValue();
	}
	
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		signal.tryAcquire(timeout, unit);
		return getValue();
	}
	
	public boolean isCancelled() {
		return false;
	}
	
	public boolean isDone() {
		return done;
	}	
	
	protected void setValue(V v) {
			this.value = v;
	}
	
	public void setChecker(X checker) {
		if( expectedValue.equals( checker)) {
			signal.release();
			done = true;
		}
	}
	
	protected V getValue() {
		return value;
	}
}

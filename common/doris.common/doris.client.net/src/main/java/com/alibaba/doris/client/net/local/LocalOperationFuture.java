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
package com.alibaba.doris.client.net.local;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.alibaba.doris.client.net.OperationFuture;

/**
 * @author Raymond He ( He Kun), raymond.he.kk@gmail.com
 * @since 1.0
 * 2011-7-6
 */
public class LocalOperationFuture<V> implements OperationFuture<V> {

        private Semaphore  signal = new Semaphore(0);
        private V value;
        
	    public LocalOperationFuture(V value) {
	    	this.value = value;
	    	signal.release();
	    }

	    public boolean cancel(boolean mayInterruptIfRunning) {
	        return false;
	    }

	    public V get() throws InterruptedException, ExecutionException {
	        signal.acquire();
	        return getResult();
	    }

	    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
	        if (signal.tryAcquire(timeout, unit)) {
	            return getResult();
	        }

	        return null;
	    }

	    private V getResult() {
	        return value;
	    }

	    public boolean isCancelled() {
	        return false;
	    }

	    public boolean isDone() {
	        if (signal.availablePermits() > 0) {
	            return true;
	        }

	        return false;
	    }
	}

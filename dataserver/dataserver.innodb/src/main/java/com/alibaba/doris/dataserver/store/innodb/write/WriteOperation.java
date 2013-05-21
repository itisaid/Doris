package com.alibaba.doris.dataserver.store.innodb.write;

import java.util.concurrent.Semaphore;

import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.dataserver.store.innodb.db.InnoDBDataBase;

/**
 * 用于存放到指定BlockingQueue中的待执行操作
 * @author long.mal
 *
 */
public class WriteOperation {
	private InnoDBDataBase store;
	private WriteType opType;
	
	private Key key;
	private Value value;
	private boolean isSetWithCompareVersion;
	
	private Semaphore available;
	private Object result = null;
	
	public WriteOperation(InnoDBDataBase store, 
						  WriteType type, 
						  Key key, 
						  Value value,
						  Boolean param, 
						  Semaphore semphore) {
		this.store = store;
		this.opType = type;
		this.key = key;
		this.value = value;
		this.isSetWithCompareVersion = param;
		this.available = semphore;
	}
	
	public WriteType getOpType() {
		return this.opType;
	}
	
	public Key getKey() {
		return this.key;
	}
	
	public Value getValue() {
		return this.value;
	}
	
	public boolean getIsSetWithCompareVersion() {
		return this.isSetWithCompareVersion;
	}
	
	public InnoDBDataBase getStore() {
		return this.store;
	}
	
	public void setResult(Object obj) {
		result = obj;
		available.release();
	}
	
	public Object getResult() {
		try {
			available.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}
}

package com.alibaba.doris.dataserver.store.handlersocket;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.doris.common.data.CompareStatus;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.impl.ValueImpl;
import com.alibaba.doris.common.data.util.ByteUtils;
import com.alibaba.doris.dataserver.store.Storage;
import com.alibaba.doris.dataserver.store.StorageType;
import com.alibaba.doris.dataserver.store.exception.VersionConflictException;
import com.alibaba.hsclient.HSClient;
import com.alibaba.hsclient.IndexSession;
import com.alibaba.hsclient.bean.ResultInfo;
import com.alibaba.hsclient.impl.HSClientImpl;

public class HandlerSocketStorage implements Storage {

	private HSClient hsClient;
	private IndexSession indexSession;
	private HandlerSocketStorageConfig config;
	private static final Logger logger = LoggerFactory
			.getLogger(HandlerSocketStorage.class);
	
	public HandlerSocketStorage(HandlerSocketStorageConfig config) {
		if (config == null) {
			throw new IllegalArgumentException(
					"The HandlerSocket cofing can't be null!");
		}
		this.config = config;
		try {
			this.hsClient = new HSClientImpl(config);
			this.hsClient.connect();
		} catch (IOException e) {
			throw new HandlerSocketException("Failed to create connection ");
		}
	}

	private String getTableName(Key key) {
		//同一个Key，只会散列到同一个VNode,以VNode作为表名
		//需要考虑create table方式，是事先根据VNode手动创建呢，还是采用mysql jdbc方式进行create table
		return "Table_"+key.getVNode();
	}

	public Value get(Key key) {
		try {
			if (null == key || StringUtils.isBlank(key.getKey())) {
				throw new IllegalArgumentException("Key can't be null!");
			}
			if (this.hsClient.getIndexMap().isEmpty())
			{
				indexSession = this.hsClient.openIndexSession(this.config.getDbName(), getTableName(key),
						"PRIMARY", new String[] { "keyColumn","valueColumn","created"});
			}
			ResultInfo resultInfo = this.indexSession.find(new String[] { key.getKey() });

			if (resultInfo == null) {
				return null;
			}
			//value的值只能一个，但带version信息
			if (resultInfo.getMessages() == null || resultInfo.getMessages().size() != 3) {
				throw new HandlerSocketException(
						"the messages size must be 3");
			}
			
			//实际设置Value的timestamp需要由返回的结果进行设置,而非系统时间
			return new ValueImpl(ByteUtils.stringToByte(resultInfo.getMessages().get(1), this.getConfig()
					.getEncoding()));
		} catch (Exception e) {
			throw new HandlerSocketException("Get value exception", e);
		}
	}

	public Map<Key, Value> getAll(Iterable<Key> keyIterator) {
		int size = ((Collection<?>) keyIterator).size();
		Map<Key, Value> result = new HashMap<Key, Value>(size);
		for (Key key : keyIterator) {
			Value value = get(key);
			if (value != null) {
				result.put(key, value);
			}
		}
		return result;
	}

	public void set(Key key, Value value) {
		try {
			if (null == key || StringUtils.isBlank(key.getKey())) {
				throw new IllegalArgumentException("Key can't be null!");
			}

			if (null == value) {
				throw new IllegalArgumentException("Value can't be null!");
			}
			if (this.hsClient.getIndexMap().isEmpty())
			{
				indexSession = this.hsClient.openIndexSession(this.config.getDbName(), getTableName(key),
						"PRIMARY", new String[] { "keyColumn","valueColumn","created"});
			}
			boolean result = this.indexSession.insert(
					new String[] {
							key.getKey(),
							new String(value.getValueBytes(), this.config
									.getEncoding()) });
			
			if (!result) {
				throw new HandlerSocketException("set key,value exception");
			}
		} catch (Exception e) {
			throw new HandlerSocketException("set value exception", e);
		}
	}

	public void set(Key key, Value value, boolean isSetWithCompareVersion) {

		if (null == key || StringUtils.isBlank(key.getKey())) {
			throw new IllegalArgumentException("Key can't be null!");
		}

		if (null == value) {
			throw new IllegalArgumentException("Value can't be null!");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("set: key=" + key + " timestamp="
					+ value.getTimestamp());
		}

		try {
			Value v = get(key);
			if (isSetWithCompareVersion && v != null)
			{
				if (v.compareVersion(value) == CompareStatus.AFTER) {
					throw new VersionConflictException(
							"Key "
									+ key.getKey()
									+ " Namespace "
									+ key.getNamespace()
									+ " "
									+ value.getTimestamp()
									+ " is obsolete, it is no greater than the current version of "
									+ v.getTimestamp() + ".");
				}
			} 
			else {
				// 是否需要执行delete操作，待确定
			}
			set(key, value);
		} catch (Exception e) {
			throw new HandlerSocketException(e);
		}

	}

	public boolean delete(Key key) {
		try {
			if (null == key || StringUtils.isBlank(key.getKey())) {
				throw new IllegalArgumentException("Key can't be null!");
			}
			if (this.hsClient.getIndexMap().isEmpty())
			{
				indexSession = this.hsClient.openIndexSession(this.config.getDbName(), getTableName(key),
						"PRIMARY", new String[] { "keyColumn","valueColumn","created"});
			}
			boolean result = this.indexSession.delete(new String[] { key.getKey() });

			if (!result) {
				return false;
			}
			return true;
		} catch (Exception e) {
			throw new HandlerSocketException("delete key exception", e);
		}
	}

	public boolean delete(Key key, Value value) {
		return false;
	}

	public boolean delete(List<Integer> vnodeList) {
		return false;
	}

	public Iterator<Pair> iterator() {
		return null;
	}

	public Iterator<Pair> iterator(List<Integer> vnodeList) {
		return null;
	}

	public void open() {
		// createTable操作?
	}

	public void close() {
		try {
			this.hsClient.close();
		} catch (IOException e) {
			throw new HandlerSocketException("Failed to close connection");
		}
	}

	public StorageType getType() {
		return HandlerSocketStorageType.HandlerSocket;
	}

	public HandlerSocketStorageConfig getConfig() {
		return config;
	}

	public void setConfig(HandlerSocketStorageConfig config) {
		this.config = config;
	}
}

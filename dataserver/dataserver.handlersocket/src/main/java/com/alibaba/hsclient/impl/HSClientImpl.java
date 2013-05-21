package com.alibaba.hsclient.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import com.alibaba.hsclient.Command;
import com.alibaba.hsclient.HSClient;
import com.alibaba.hsclient.IndexSession;
import com.alibaba.hsclient.bean.FilterInfo;
import com.alibaba.hsclient.bean.InInfo;
import com.alibaba.hsclient.bean.IndexInfo;
import com.alibaba.hsclient.bean.LimitInfo;
import com.alibaba.hsclient.bean.ModInfo;
import com.alibaba.hsclient.bean.ResultInfo;
import com.alibaba.hsclient.commond.DecrementCommand;
import com.alibaba.hsclient.commond.DeleteCommand;
import com.alibaba.hsclient.commond.FindCommand;
import com.alibaba.hsclient.commond.IncrementCommand;
import com.alibaba.hsclient.commond.InsertCommand;
import com.alibaba.hsclient.commond.OpenIndexCommand;
import com.alibaba.hsclient.commond.UpdateCommand;
import com.alibaba.hsclient.conf.HSConfig;
import com.alibaba.hsclient.exception.HandlerSocketException;
import com.alibaba.hsclient.util.CompareOperator;
import com.alibaba.hsclient.util.HSUtils;
import com.alibaba.hsclient.util.ModOperator;

public class HSClientImpl implements HSClient {

	private HSConfig hsConfig;
	private SocketChannel socket;
	private Selector selector;
	private BlockingQueue<byte[]> commandBuffer;
	private int currentResultSize = 0;
	private final ConcurrentHashMap<Integer, IndexInfo> indexIdMap = new ConcurrentHashMap<Integer, IndexInfo>();
	private static AtomicInteger INDEXID_COUNTER = new AtomicInteger();

	public HSClientImpl(HSConfig hsConfig) throws IOException {
		this.hsConfig = hsConfig;
		commandBuffer = new LinkedBlockingQueue<byte[]>();
	}

	public Map<Integer, IndexInfo> getIndexMap() {
		return Collections
				.<Integer, IndexInfo> unmodifiableMap(this.indexIdMap);
	}

	public void connect() throws IOException {
		connect(InetAddress.getByName(this.hsConfig.getHost()),
				this.hsConfig.isReadOnly() ? this.hsConfig.getRPort()
						: this.hsConfig.getWrPort());
	}

	private void connect(InetAddress address, int port) throws IOException {

		if (socket != null && socket.isConnected()) {
			close();
		}

		selector = Selector.open();
		socket = SocketChannel.open();
		socket.configureBlocking(this.hsConfig.isBlocking());
		socket.socket().setReceiveBufferSize(
				this.hsConfig.getReceiveBufferSize());
		socket.socket().setSendBufferSize(this.hsConfig.getSendBufferSize());
		socket.socket().setSoTimeout(this.hsConfig.getSoTimeout());
		socket.socket().setTcpNoDelay(this.hsConfig.isTcpNoDelay());
		socket.socket().setReuseAddress(this.hsConfig.isReuseAddress());
		if (this.hsConfig.isHardClose()) {
			socket.socket().setSoLinger(true, 0);
		}

		socket.connect(new InetSocketAddress(address, port));
		while (!socket.finishConnect()) {
		}
	}

	private synchronized ResultInfo execute() throws IOException {
		if (commandBuffer.size() == 0) {
			return null;
		}
		currentResultSize = 0;
		socket.register(selector, socket.validOps());
		ResultInfo result = new ResultInfo();
		try {
			boolean processComplete = false;
			while (!processComplete && selector.select() > 0) {
				Iterator<SelectionKey> iterator = selector.selectedKeys()
						.iterator();
				while (iterator.hasNext()) {
					SelectionKey key = (SelectionKey) iterator.next();
					iterator.remove();

					if (key.isWritable()) {
						SocketChannel channel = (SocketChannel) key.channel();
						final ByteArrayOutputStream buf = new ByteArrayOutputStream();
						for (byte[] command; (command = commandBuffer.poll()) != null;) {
							buf.write(command);
						}

						channel.register(selector, SelectionKey.OP_READ);

						ByteBuffer wb = ByteBuffer.wrap(buf.toByteArray());
						while (wb.remaining() > 0) {
							channel.write(wb);
						}

					} else if (key.isReadable()) {
						SocketChannel channel = (SocketChannel) key.channel();
						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						ByteBuffer rb = ByteBuffer.allocate(this.hsConfig
								.getExecuteBufferSize());
						rb.clear();
						for (int size = 0; (size = channel.read(rb)) > 0;) {
							currentResultSize += size;
							rb.flip();
							buffer.write(rb.array(), 0, size);
							rb.position(0);
							rb.clear();
							if (size < this.hsConfig.getExecuteBufferSize()) {
								break;
							}
						}
						HSUtils parser = new HSUtils(
								this.hsConfig.getEncoding());
						result = parser.parse(buffer.toByteArray());
						processComplete = true;
						break;
					}
				}
			}
		} finally {
		}
		return result;
	}

	public void close() throws IOException {
		socket.socket().close();
		socket.close();
		try {
			Iterator<SelectionKey> itr = selector.keys().iterator();
			while (itr.hasNext()) {
				SelectionKey key = (SelectionKey) itr.next();
				key.channel().close();
				key.cancel();
			}
		} catch (IOException e) {
			throw e;
		}
		selector.close();
	}

	public int getCurrentResponseSize() {
		return currentResultSize;
	}

	public void clear() {
		this.commandBuffer.clear();
		this.currentResultSize = 0;
	}

	
	public boolean openIndex(int indexId, String dbName, String tableName,
			String indexName, String[] columns, String[] fColumns)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException, IOException {
		this.checkParams(dbName, tableName, indexName, columns, fColumns);
		IndexInfo record = new IndexInfo(indexId, dbName, tableName, indexName,
				columns, fColumns);
		this.indexIdMap.put(indexId, record);

		Command command = new OpenIndexCommand(indexId, dbName, tableName,
				indexName, columns, fColumns);
		this.commandBuffer.add(command.toByte(this.hsConfig.getEncoding()));
		ResultInfo resultInfo = this.execute();

		return resultInfo.getErrorCode() == 0 ? true : false;
	}

	
	public boolean openIndex(int indexId, String dbName, String tableName,
			String indexName, String[] columns) throws InterruptedException,
			TimeoutException, HandlerSocketException,
			UnsupportedEncodingException, IOException {
		return this.openIndex(indexId, dbName, tableName, indexName, columns,
				null);
	}

	
	public ResultInfo find(int indexId, CompareOperator operator,
			String[] fieldValues, LimitInfo limitInfo, InInfo inInfo,
			FilterInfo[] filterInfos) throws InterruptedException,
			TimeoutException, HandlerSocketException,
			UnsupportedEncodingException, IOException {
		Command command = new FindCommand(indexId, operator, fieldValues,
				limitInfo, inInfo, filterInfos);
		this.commandBuffer.add(command.toByte(this.hsConfig.getEncoding()));
		return this.execute();
	}

	
	public ResultInfo find(int indexId, String[] fieldValues)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException, IOException {
		return this.find(indexId, CompareOperator.EQ, fieldValues, null, null,
				null);
	}

	
	public boolean update(int indexId, CompareOperator operator,
			String[] fieldValues, LimitInfo limitInfo, InInfo inInfo,
			FilterInfo[] filterInfos, ModInfo modInfo)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException, IOException {
		if (this.hsConfig.isReadOnly()) {
			throw new UnsupportedOperationException(
					"update is not supported for readonly sesion");
		}
		Command command = new UpdateCommand(indexId, operator, fieldValues,
				limitInfo, inInfo, filterInfos, modInfo);
		this.commandBuffer.add(command.toByte(this.hsConfig.getEncoding()));
		ResultInfo resultInfo = this.execute();
		return resultInfo.getErrorCode() == 0 ? true : false;
	}

	
	public boolean update(int indexId, CompareOperator operator,
			String[] fieldValues, ModInfo modInfo) throws InterruptedException,
			TimeoutException, HandlerSocketException,
			UnsupportedEncodingException, IOException {
		return this.update(indexId, operator, fieldValues, new LimitInfo(),
				null, null, modInfo);
	}

	
	public boolean delete(int indexId, CompareOperator operator,
			String[] fieldValues, LimitInfo limitInfo, InInfo inInfo,
			FilterInfo[] filterInfos) throws InterruptedException,
			TimeoutException, HandlerSocketException,
			UnsupportedEncodingException, IOException {
		if (this.hsConfig.isReadOnly()) {
			throw new UnsupportedOperationException(
					"delete is not supported for readonly sesion");
		}
		ModInfo modInfo = new ModInfo();
		modInfo.setModType(ModOperator.D);
		Command command = new DeleteCommand(indexId, operator, fieldValues,
				limitInfo, inInfo, filterInfos, modInfo);
		this.commandBuffer.add(command.toByte(this.hsConfig.getEncoding()));
		ResultInfo resultInfo = this.execute();
		return resultInfo.getErrorCode() == 0 ? true : false;
	}

	
	public boolean delete(int indexId, CompareOperator operator,
			String[] fieldValues) throws InterruptedException,
			TimeoutException, HandlerSocketException,
			UnsupportedEncodingException, IOException {
		return this.delete(indexId, operator, fieldValues, new LimitInfo(),
				null, null);
	}

	
	public boolean delete(int indexId, String[] fieldValues)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException, IOException {
		return this.delete(indexId, CompareOperator.EQ, fieldValues,
				new LimitInfo(), null, null);
	}

	
	public boolean insert(int indexId, String[] fieldValues)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException, IOException {
		if (this.hsConfig.isReadOnly()) {
			throw new UnsupportedOperationException(
					"insert is not supported for readonly sesion");
		}
		Command command = new InsertCommand(indexId, fieldValues);
		this.commandBuffer.add(command.toByte(this.hsConfig.getEncoding()));
		ResultInfo resultInfo = this.execute();
		return resultInfo.getErrorCode() == 0 ? true : false;
	}

	
	public boolean increment(int indexId, CompareOperator operator,
			String[] fieldValues, LimitInfo limitInfo, InInfo inInfo,
			FilterInfo[] filterInfos, ModInfo modInfo)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException, IOException {
		if (this.hsConfig.isReadOnly()) {
			throw new UnsupportedOperationException(
					"increment is not supported for readonly sesion");
		}
		Command command = new IncrementCommand(indexId, operator, fieldValues,
				limitInfo, inInfo, filterInfos, modInfo);
		this.commandBuffer.add(command.toByte(this.hsConfig.getEncoding()));
		ResultInfo resultInfo = this.execute();
		return resultInfo.getErrorCode() == 0 ? true : false;
	}

	
	public boolean increment(int indexId, CompareOperator operator,
			String[] fieldValues, ModInfo modInfo) throws InterruptedException,
			TimeoutException, HandlerSocketException,
			UnsupportedEncodingException, IOException {
		return this.increment(indexId, operator, fieldValues, null, null, null,
				modInfo);
	}

	
	public boolean decrement(int indexId, CompareOperator operator,
			String[] fieldValues, LimitInfo limitInfo, InInfo inInfo,
			FilterInfo[] filterInfos, ModInfo modInfo)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, UnsupportedEncodingException, IOException {
		if (this.hsConfig.isReadOnly()) {
			throw new UnsupportedOperationException(
					"decrement is not supported for readonly sesion");
		}
		Command command = new DecrementCommand(indexId, operator, fieldValues,
				limitInfo, inInfo, filterInfos, modInfo);
		this.commandBuffer.add(command.toByte(this.hsConfig.getEncoding()));
		ResultInfo resultInfo = this.execute();
		return resultInfo.getErrorCode() == 0 ? true : false;
	}

	
	public boolean decrement(int indexId, CompareOperator operator,
			String[] fieldValues, ModInfo modInfo) throws InterruptedException,
			TimeoutException, HandlerSocketException,
			UnsupportedEncodingException, IOException {
		return this.decrement(indexId, operator, fieldValues, null, null, null,
				modInfo);
	}

	private void checkParams(String dbname, String tableName, String indexName,
			String[] columns, String[] fcolumns) {
		if (HSUtils.isBlank(dbname)) {
			throw new IllegalArgumentException("blank dbName:" + dbname);
		}
		if (HSUtils.isBlank(tableName)) {
			throw new IllegalArgumentException("blank tableName:" + tableName);
		}
		if (HSUtils.isBlank(indexName)) {
			throw new IllegalArgumentException("blank indexName:" + indexName);
		}
		if (columns == null || columns.length == 0) {
			throw new IllegalArgumentException("empty columns");
		}
		for (String col : columns) {
			if (HSUtils.isBlank(col)) {
				throw new IllegalArgumentException("blank column name:" + col);
			}
		}

		if (fcolumns != null && fcolumns.length != 0) {
			for (String col : fcolumns) {
				if (HSUtils.isBlank(col)) {
					throw new IllegalArgumentException("blank fcolumn name:"
							+ col);
				}
			}
		}
	}

	
	public IndexSession openIndexSession(int indexId, String dbname,
			String tableName, String indexName, String[] columns,
			String[] fcolumns) throws InterruptedException, TimeoutException,
			HandlerSocketException, IOException {
		this.checkParams(dbname, tableName, indexName, columns, fcolumns);
		if (this.openIndex(indexId, dbname, tableName, indexName, columns,
				fcolumns)) {
			return new IndexSessionImpl(this, indexId, columns);
		} else {
			return null;
		}
	}

	
	public IndexSession openIndexSession(int indexId, String dbname,
			String tableName, String indexName, String[] columns)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, IOException {
		return this.openIndexSession(indexId, dbname, tableName, indexName,
				columns, null);
	}

	
	public IndexSession openIndexSession(String dbname, String tableName,
			String indexName, String[] columns) throws InterruptedException,
			TimeoutException, HandlerSocketException, IOException {
		return this.openIndexSession(INDEXID_COUNTER.incrementAndGet(), dbname,
				tableName, indexName, columns);
	}

	
	public IndexSession openIndexSession(String dbname, String tableName,
			String indexName, String[] columns, String[] fcolumns)
			throws InterruptedException, TimeoutException,
			HandlerSocketException, IOException {
		return this.openIndexSession(INDEXID_COUNTER.incrementAndGet(), dbname,
				tableName, indexName, columns, fcolumns);
	}
}

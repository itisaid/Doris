package com.alibaba.hsclient.commond;

import java.io.UnsupportedEncodingException;

import com.alibaba.hsclient.Command;
import com.alibaba.hsclient.util.Const;

/**
 * P <indexid> <dbname> <tablename> <indexname> <columns> [<fcolumns>]
 * @author yinghui.huangyh
 *
 */
public class OpenIndexCommand implements Command {

	private String operator = Const.OPEN_INDEX_OPERATOR;
	private int indexId;
	private String dbName;
	private String tableName;
	private String indexName;
	private String[] columns;
	private String[] fColumns;
	
	public OpenIndexCommand(int indexId, String dbName, String tableName,
			String indexName, String[] columns, String[] fColumns) {
		super();
		this.indexId = indexId;
		this.dbName = dbName;
		this.tableName = tableName;
		this.indexName = indexName;
		this.columns = columns;
		this.fColumns = fColumns;
	}

	public String getOperator() {
		return operator;
	}

	public int getIndexId() {
		return indexId;
	}

	public void setIndexId(int indexId) {
		this.indexId = indexId;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public String[] getColumns() {
		return columns;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

	public String[] getfColumns() {
		return fColumns;
	}

	public void setfColumns(String[] fColumns) {
		this.fColumns = fColumns;
	}


	public byte[] toByte(String encoding) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		sb.append(this.operator).append(Const.TOKEN_SEPARATOR)
		  .append(this.getIndexId()).append(Const.TOKEN_SEPARATOR)
		  .append(this.getDbName()).append(Const.TOKEN_SEPARATOR)
		  .append(this.getTableName()).append(Const.TOKEN_SEPARATOR)
		  .append(this.getIndexName()).append(Const.TOKEN_SEPARATOR);
		
		for (int i = 0; i < this.columns.length; i++) {
			if (i == columns.length -1)
			{
				sb.append(columns[i]);
			}
			else
			{
				sb.append(columns[i]).append(Const.COMMA_SEPARATOR);
			}
		}
		
		if (this.fColumns == null || this.fColumns.length == 0) {
			sb.append(Const.COMMAND_TERMINATE);
			return sb.toString().getBytes(encoding);
		} else {
			for (int i = 0; i < this.columns.length; i++) {
				if (i == this.columns.length - 1) {
					sb.append(this.columns[i]);
				}
				else
				{
					sb.append(this.columns[i]).append(Const.COMMA_SEPARATOR);
				}
			}
		}
		sb.append(Const.COMMAND_TERMINATE);
		return sb.toString().getBytes(encoding);
	}
}

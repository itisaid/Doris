package com.alibaba.hsclient.bean;

public class IndexInfo {
	public final int id;
	public final String db;
	public final String tableName;
	public final String indexName;
	public final String[] fieldList;
	public final String[] filterFieldList;

	public IndexInfo(int id, String db, String tableName, String indexName,
			String[] fieldList, String[] filterFieldList) {
		super();
		this.id = id;
		this.db = db;
		this.tableName = tableName;
		this.indexName = indexName;
		this.fieldList = fieldList;
		this.filterFieldList = filterFieldList;
	}

}

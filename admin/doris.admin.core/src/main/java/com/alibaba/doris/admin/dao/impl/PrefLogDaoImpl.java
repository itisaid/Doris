package com.alibaba.doris.admin.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.alibaba.doris.admin.dao.PrefLogDao;
import com.alibaba.doris.admin.dataobject.PrefLogDO;
import com.alibaba.doris.admin.support.PrefQuery;
import com.ibatis.sqlmap.client.SqlMapExecutor;

public class PrefLogDaoImpl extends SqlMapClientDaoSupport implements PrefLogDao {

    public void insert(PrefLogDO record) {
        getSqlMapClientTemplate().insert("PREF_LOG.insert", record);
    }

    public PrefLogDO selectByPrimaryKey(Integer id) {
        PrefLogDO key = new PrefLogDO();
        key.setId(id);
        PrefLogDO record = (PrefLogDO) getSqlMapClientTemplate().queryForObject(
                "PREF_LOG.selectByPrimaryKey", key);
        return record;
    }

    public void batchInsert(final List<PrefLogDO> reports) {
        getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                executor.startBatch();
                for (PrefLogDO record : reports) {
                    if (record != null) {
                        executor.insert("PREF_LOG.insert", record);
                    }
                }
                executor.executeBatch();
                return "";
            }
        });
    }

    public List<PrefLogDO> statByQuery(PrefQuery query) {
        return getSqlMapClientTemplate().queryForList("PREF_LOG.statByQuery", query);
    }

    public List<PrefLogDO> statWithNameSpace(PrefQuery query) {
        return getSqlMapClientTemplate().queryForList("PREF_LOG.statWithNameSpaceView", query);
    }

    public List<PrefLogDO> statWithPhysicalId(PrefQuery query) {
        return getSqlMapClientTemplate().queryForList("PREF_LOG.statWithPhysicalIdView", query);
    }

    public Date getMinTimeStart(Date before) {
        Date date = (Date) getSqlMapClientTemplate().queryForObject("PREF_LOG.queryMinTimeStart",
                before);
        return date;
    }

    public List<PrefLogDO> queryNeedArchive(Date timeStart, Date timeEnd) {
        HashMap query = new HashMap();
        query.put("timeStart", timeStart);
        query.put("timeEnd", timeEnd);

        return getSqlMapClientTemplate().queryForList("PREF_LOG.queryNeedArchive", query);
    }

    public void deleteRecoredByTime(Date timeStart, Date timeEnd) {
        HashMap query = new HashMap();
        query.put("timeStart", timeStart);
        query.put("timeEnd", timeEnd);
        getSqlMapClientTemplate().delete("PREF_LOG.deleteRecoredByTime", query);
    }

    public void batchInsertToArchived(final List<PrefLogDO> reports) {
        getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                executor.startBatch();
                for (PrefLogDO record : reports) {
                    if (record != null) {
                        executor.insert("PREF_LOG_ACHIVED.insert", record);
                    }
                }
                executor.executeBatch();
                return "";
            }
        });

    }

    public void deleteArchivedRecoredByTime(Date before) {
        HashMap query = new HashMap();
        query.put("timeEnd", before);
        getSqlMapClientTemplate().delete("PREF_LOG_ACHIVED.deleteArchivedRecoredByTime", query);
    }
}

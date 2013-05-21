package com.alibaba.doris.admin.dao;

import java.util.Date;
import java.util.List;

import com.alibaba.doris.admin.dataobject.PrefLogDO;
import com.alibaba.doris.admin.support.PrefQuery;

public interface PrefLogDao {

    void insert(PrefLogDO record);

    void batchInsert(List<PrefLogDO> reports);

    List<PrefLogDO> statByQuery(PrefQuery query);

    List<PrefLogDO> statWithNameSpace(PrefQuery query);

    List<PrefLogDO> statWithPhysicalId(PrefQuery query);

    /**
     * for 数据迁移
     * 
     * @param before
     * @param query
     * @return
     */
    Date getMinTimeStart(Date before);


    List<PrefLogDO> queryNeedArchive(Date timeStart, Date timeEnd);


    void deleteRecoredByTime(Date timeStart, Date timeEnd);

    void batchInsertToArchived(List<PrefLogDO> reports);

    void deleteArchivedRecoredByTime(Date before);

}
